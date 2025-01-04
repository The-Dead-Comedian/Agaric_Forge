package com.dead_comedian.agaric.entity;

import com.dead_comedian.agaric.entity.goals.RandomStrollWhileNotSitting;
import com.dead_comedian.agaric.item.AgaricItems;
import com.dead_comedian.agaric.particle.AgaricParticles;
import com.google.common.annotations.VisibleForTesting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SporderEntity extends TamableAnimal implements Saddleable, ItemSteerable, PlayerRideableJumping {
    protected SporderEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    ////////////
    //VARIABLE//
    ////////////

    private int growTimer = 0;
    AgarachnidEntity agarachnidEntity = new AgarachnidEntity(AgaricEntities.AGARACHNID.get(), this.level());
    private boolean isGrowthStunted = false;


    protected boolean allowStandSliding;
    protected boolean isJumping;
    protected float playerJumpPendingScale;
    private static final EntityDataAccessor<Integer> DATA_BOOST_TIME = SynchedEntityData.defineId(SporderEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_SADDLE_ID = SynchedEntityData.defineId(SporderEntity.class, EntityDataSerializers.BOOLEAN);
    private final ItemBasedSteering steering = new ItemBasedSteering(this.entityData, DATA_BOOST_TIME, DATA_SADDLE_ID);

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState sitAnimationState = new AnimationState();
    public final AnimationState sitUpAnimationState = new AnimationState();
    public final AnimationState sitDownAnimationState = new AnimationState();
    public final AnimationState bounceAnimationState = new AnimationState();
    private int bounceAnimationStateTimeout = 0;
    private int sitDownAnimationStateTimeout = 0;
    private int sitUpAnimationStateTimeout = 0;

    public static final EntityDataAccessor<Long> LAST_POSE_CHANGE_TICK = SynchedEntityData.defineId(SporderEntity.class, EntityDataSerializers.LONG);
    public static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(SporderEntity.class, EntityDataSerializers.BOOLEAN);

    ///////
    //NBT//
    ///////

    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        if (DATA_BOOST_TIME.equals(pKey) && this.level().isClientSide) {
            this.steering.onSynced();
        }

        super.onSyncedDataUpdated(pKey);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SITTING, this.isOrderedToSit());
        this.entityData.define(LAST_POSE_CHANGE_TICK, 0L);
        this.entityData.define(DATA_BOOST_TIME, 0);
        this.entityData.define(DATA_SADDLE_ID, false);
    }

    @VisibleForTesting
    public void resetLastPoseChangeTick(long pLastPoseChangeTick) {
        this.entityData.set(LAST_POSE_CHANGE_TICK, pLastPoseChangeTick);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putLong("LastPoseTick", this.entityData.get(LAST_POSE_CHANGE_TICK));
        pCompound.putBoolean("Saddled", this.isSaddled());
        pCompound.putBoolean("Tamed", this.isTame());
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.steering.setSaddle(pCompound.getBoolean("Saddled"));
        this.setTame(pCompound.getBoolean("Tamed"));
        long i = pCompound.getLong("LastPoseTick");
        if (i < 0L) {
            this.setPose(Pose.SITTING);
        }

        this.resetLastPoseChangeTick(i);
    }

    //////////
    //RIDING//
    //////////

    //jump


    @Override
    protected int calculateFallDamage(float pFallDistance, float pDamageMultiplier) {
        return 0;
    }

    public boolean isJumping() {
        return this.isJumping;
    }

    public void setIsJumping(boolean pJumping) {
        this.isJumping = pJumping;
    }

    public double getCustomJump() {
        return this.getAttributeValue(Attributes.JUMP_STRENGTH);
    }

    protected void executeRidersJump(float pPlayerJumpPendingScale, Vec3 pTravelVector) {
        double d0 = this.getCustomJump() * (double) pPlayerJumpPendingScale * (double) this.getBlockJumpFactor();
        double d1 = d0 + (double) this.getJumpBoostPower();
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.x, d1, vec3.z);
        this.setIsJumping(true);
        this.hasImpulse = true;
        net.minecraftforge.common.ForgeHooks.onLivingJump(this);
        if (pTravelVector.z > 0.0D) {
            float f = Mth.sin(this.getYRot() * ((float) Math.PI / 180F));
            float f1 = Mth.cos(this.getYRot() * ((float) Math.PI / 180F));
            this.setDeltaMovement(this.getDeltaMovement().add((double) (-0.4F * f * pPlayerJumpPendingScale), 1.0D, (double) (0.4F * f1 * pPlayerJumpPendingScale)));
        }

    }

    public void onPlayerJump(int pJumpPower) {
        if (this.isSaddled()) {
            if (pJumpPower < 0) {
                pJumpPower = 0;
            } else {
                this.allowStandSliding = true;

            }

            if (pJumpPower >= 90) {
                this.playerJumpPendingScale = 1.0F;
            } else {
                this.playerJumpPendingScale = 0.4F + 0.4F * (float) pJumpPower / 90.0F;
            }

        }
    }

    public boolean canJump() {
        return this.isSaddled();
    }

    public void handleStartJump(int pJumpPower) {
        this.allowStandSliding = true;
    }

    @Override
    public void handleStopJump() {
    }

    //riding

    public double getPassengersRidingOffset() {
        float f = Math.min(0.25F, this.walkAnimation.speed());
        float f1 = this.walkAnimation.position();
        if (!this.isOrderedToSit()) {
            return (double) this.getBbHeight() + 0.2F + (double) (0.12F * Mth.cos(f1 * 2F) * 2.0F * f);
        } else {
            return (double) this.getBbHeight() - 0.2F + (double) (0.12F * Mth.cos(f1 * 2F) * 2.0F * f);
        }
    }

    protected Vec3 getRiddenInput(Player pPlayer, Vec3 pTravelVector) {
        if (this.onGround() && this.playerJumpPendingScale == 0.0F && !this.allowStandSliding) {
            return Vec3.ZERO;
        } else {
            float f = pPlayer.xxa * 0.5F;
            float f1 = pPlayer.zza;
            if (f1 <= 0.0F) {
                f1 *= 0.25F;
            }

            return new Vec3((double) f, 0.0D, (double) f1);
        }
    }


    protected float getRiddenSpeed(Player pPlayer) {
        if (!this.isOrderedToSit()) {
            return (float) (this.getAttributeValue(Attributes.MOVEMENT_SPEED) * (double) this.steering.boostFactor());
        } else {
            return 0;
        }
    }

    protected float nextStep() {
        return this.moveDist + 0.6F;
    }

    @Override
    public boolean boost() {
        return this.steering.boost(this.getRandom());
    }

    protected void tickRidden(Player pPlayer, Vec3 pTravelVector) {
        super.tickRidden(pPlayer, pTravelVector);
        if (!this.isOrderedToSit()) {
            Vec2 vec2 = this.getRiddenRotation(pPlayer);
            this.setRot(vec2.y, vec2.x);
            this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
            if (this.isControlledByLocalInstance()) {
                if (this.onGround()) {
                    this.setIsJumping(false);
                    if (this.playerJumpPendingScale > 0.0F && !this.isJumping()) {
                        this.executeRidersJump(this.playerJumpPendingScale, pTravelVector);
                    }
                    this.playerJumpPendingScale = 0.0F;
                }
            }
        }
    }

    protected Vec2 getRiddenRotation(LivingEntity pEntity) {
        return new Vec2(pEntity.getXRot() * 0.5F, pEntity.getYRot());
    }

    @Override
    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.isTame();
    }

    @javax.annotation.Nullable
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        if (entity instanceof Player player) {
            if (player.getMainHandItem().is(AgaricItems.ROTTEN_FLESH_ON_A_STICK.get()) || player.getOffhandItem().is(AgaricItems.ROTTEN_FLESH_ON_A_STICK.get())) {
                return player;
            }
        }

        return null;
    }

    ////////
    //MISC//
    ////////

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }


    @Override
    public void tick() {
        super.tick();

        if (this.bounceAnimationState.isStarted()) {
            this.bounceAnimationStateTimeout++;
        } else {
            this.bounceAnimationStateTimeout = 0;
        }
        if (this.bounceAnimationStateTimeout >= 7) {
            this.bounceAnimationState.stop();
        }

        if (this.sitDownAnimationState.isStarted()) {
            this.sitDownAnimationStateTimeout++;
        } else {
            this.sitDownAnimationStateTimeout = 0;
        }
        if (this.sitDownAnimationStateTimeout >= 18) {
            this.sitDownAnimationState.stop();
        }

        if (this.sitUpAnimationState.isStarted()) {
            this.sitUpAnimationStateTimeout++;
        } else {
            this.sitUpAnimationStateTimeout = 0;
        }
        if (this.sitUpAnimationStateTimeout >= 18) {
            this.sitUpAnimationState.stop();
        }


        if (this.isTame()) {
            growTimer++;
        }


        if (growTimer >= 240000 && !isGrowthStunted) {
            agarachnidEntity.moveTo(this.blockPosition(), 0.0F, 0.0F);
            this.level().addFreshEntity(agarachnidEntity);
            agarachnidEntity.tame((Player) this.getOwner());
            this.discard();
        }


        if (this.isOrderedToSit()) {
            List<Entity> entityBelow = this.level().getEntities(this, this.getBoundingBox().expandTowards(0, 0.2, 0));
            for (Entity entity : entityBelow) {
                entity.addDeltaMovement(new Vec3(0, 1, 0));
                this.bounceAnimationState.startIfStopped(tickCount);
            }
            level().addParticle(AgaricParticles.SLEEP.get(), this.getX(), this.getY(), this.getZ(), 1, 1, 1);
        }
        if (this.level().isClientSide()) {
            setupAnimationStates();
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.2D, Ingredient.of(AgaricItems.ROTTEN_FLESH_ON_A_STICK.get(), Items.ROTTEN_FLESH), false));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(3, new RandomStrollWhileNotSitting(this, 1.1D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 3f));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes().add(Attributes.JUMP_STRENGTH, 3).add(Attributes.MAX_HEALTH, 20D).add(Attributes.FOLLOW_RANGE, 24D).add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.ARMOR_TOUGHNESS, 0.1f).add(Attributes.ATTACK_KNOCKBACK, 0.5f).add(Attributes.ATTACK_DAMAGE, 2f);
    }

    /////////////
    //ANIMATION//
    /////////////

    @Override
    protected void updateWalkAnimation(float pPartialTick) {
        float f;
        if (this.getPose() == Pose.STANDING) {
            f = Math.min(pPartialTick * 6F, 1f);
        } else {
            f = 0f;
        }

        this.walkAnimation.update(f, 0.2f);
    }

    private void setupAnimationStates() {
        if (!this.isOrderedToSit()) {
            this.idleAnimationState.startIfStopped(this.tickCount);
            this.sitAnimationState.stop();
        } else if (this.isOrderedToSit() && !this.sitDownAnimationState.isStarted()) {
            this.sitAnimationState.startIfStopped(this.tickCount);
            this.idleAnimationState.stop();
        }

    }


    //////////
    // TAME //
    //////////


    @Override
    public boolean isPushable() {
        return !this.isOrderedToSit();
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);

        //riding
        boolean flag = this.isFood(pPlayer.getItemInHand(pHand));
        if (!flag && this.isSaddled() && !this.isVehicle() && !pPlayer.isSecondaryUseActive() && itemstack.is(AgaricItems.ROTTEN_FLESH_ON_A_STICK.get())) {
            if (!this.level().isClientSide) {
                pPlayer.startRiding(this);

            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        //stunted growth

        if (!isGrowthStunted && itemstack.is(Items.FERMENTED_SPIDER_EYE)) {
            isGrowthStunted = true;
            if (!pPlayer.getAbilities().instabuild) {
                itemstack.shrink(1);
            }

        }


        //unequip saddle

        if (this.isSaddled() && itemstack.is(Items.SHEARS)) {
            this.steering.setSaddle(false);
            this.level().playSound((Player) null, this, SoundEvents.STRIDER_SADDLE, pPlayer.getSoundSource(), 0.5F, 1.0F);
            this.spawnAtLocation(new ItemStack(Items.SADDLE), 1.7F);
        }

        //sitting
        if (this.isTame() && this.level().isClientSide()) {
            InteractionResult interactionresult = super.mobInteract(pPlayer, pHand);
            if (!interactionresult.consumesAction() && this.isOwnedBy(pPlayer) && !pPlayer.isCrouching()) {
                this.setOrderedToSit(!this.isOrderedToSit());
                if (this.isOrderedToSit()) {
                    this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 1200, 1));
                    this.sitDownAnimationState.startIfStopped(tickCount);
                } else {
                    this.sitUpAnimationState.startIfStopped(tickCount);
                }
                return InteractionResult.SUCCESS;
            }

        }


        if (this.level().isClientSide) {
            boolean flag1 = this.isOwnedBy(pPlayer) || this.isTame() || itemstack.is(Items.ROTTEN_FLESH) && !this.isTame();
            return flag1 ? InteractionResult.CONSUME : InteractionResult.PASS;


            //tame
        } else if (itemstack.is(Items.ROTTEN_FLESH)) {
            if (!pPlayer.getAbilities().instabuild) {
                itemstack.shrink(1);
            }

            if (this.random.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, pPlayer)) {
                this.tame(pPlayer);

                this.level().broadcastEntityEvent(this, (byte) 7);
            } else {
                this.level().broadcastEntityEvent(this, (byte) 6);
            }


            return InteractionResult.SUCCESS;
        } else {
            return super.mobInteract(pPlayer, pHand);
        }
    }


    ////////////
    // SOUNDS //
    ////////////

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.HOGLIN_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.RAVAGER_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.DOLPHIN_DEATH;
    }

    //////////
    //SADDLE//
    //////////

    public boolean isSaddled() {
        return this.steering.hasSaddle();
    }

    @Override
    public boolean isSaddleable() {
        return this.isAlive() && !this.isBaby() && this.isTame();
    }

    public void equipSaddle(@javax.annotation.Nullable SoundSource pSource) {
        this.steering.setSaddle(true);
        if (pSource != null) {
            this.level().playSound((Player) null, this, SoundEvents.STRIDER_SADDLE, pSource, 0.5F, 1.0F);
        }

    }

    protected void dropEquipment() {
        super.dropEquipment();
        if (this.isSaddled()) {
            this.spawnAtLocation(Items.SADDLE);
        }

    }

    public SoundEvent getSaddleSoundEvent() {
        return SoundEvents.CAMEL_SADDLE;
    }


}