package com.dead_comedian.agaric.entity;

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
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
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



    private static final EntityDataAccessor<Integer> DATA_BOOST_TIME = SynchedEntityData.defineId(SporderEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_SADDLE_ID = SynchedEntityData.defineId(SporderEntity.class, EntityDataSerializers.BOOLEAN);
    private final ItemBasedSteering steering = new ItemBasedSteering(this.entityData, DATA_BOOST_TIME, DATA_SADDLE_ID);

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState sitAnimationState = new AnimationState();
    public final AnimationState sitUpAnimationState = new AnimationState();
    public final AnimationState sitDownAnimationState = new AnimationState();

    public static final EntityDataAccessor<Long> LAST_POSE_CHANGE_TICK = SynchedEntityData.defineId(SporderEntity.class, EntityDataSerializers.LONG);


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
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        long i = pCompound.getLong("LastPoseTick");
        if (i < 0L) {
            this.setPose(Pose.SITTING);
        }

        this.resetLastPoseChangeTick(i);
    }

    //////////
    //RIDING//
    //////////
    @Override
    public boolean boost() {
        return false;
    }

    @Override
    public void onPlayerJump(int pJumpPower) {
    }

    @Override
    public boolean canJump() {
        return this.isSaddled();
    }


    @Override
    public void handleStartJump(int pJumpPower) {
    }

    @Override
    public void handleStopJump() {
    }


    public double getPassengersRidingOffset() {
        float f = Math.min(0.25F, this.walkAnimation.speed());
        float f1 = this.walkAnimation.position();
        return (double) this.getBbHeight() - 0.19D + (double) (0.12F * Mth.cos(f1 * 1.5F) * 2.0F * f);
    }


    @javax.annotation.Nullable
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        if (entity instanceof Player player) {
            if (player.getMainHandItem().is(Items.WARPED_FUNGUS_ON_A_STICK) || player.getOffhandItem().is(Items.WARPED_FUNGUS_ON_A_STICK)) {
                return player;
            }
        }

        return null;
    }

    protected void tickRidden(Player pPlayer, Vec3 pTravelVector) {
        this.setRot(pPlayer.getYRot(), pPlayer.getXRot() * 0.5F);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();

        super.tickRidden(pPlayer, pTravelVector);
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
        if (this.isOrderedToSit()) {
            List<Entity> entityBelow = this.level().getEntities(this, this.getBoundingBox().expandTowards(0, 0.2, 0));

            for (Entity entity : entityBelow) {
                entity.addDeltaMovement(new Vec3(0, 1, 0));
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
        this.goalSelector.addGoal(1, new BreedGoal(this, 1.15D));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.2D, Ingredient.of(AgaricItems.ROTTEN_FLESH_ON_A_STICK.get()), false));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.1D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 3f));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes().add(Attributes.MAX_HEALTH, 20D).add(Attributes.FOLLOW_RANGE, 24D).add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.ARMOR_TOUGHNESS, 0.1f).add(Attributes.ATTACK_KNOCKBACK, 0.5f).add(Attributes.ATTACK_DAMAGE, 2f);
    }

    /////////////
    //ANIMATION//
    /////////////


    public void sitDown() {
        if (!this.isCamelSitting()) {
            this.playSound(SoundEvents.CAMEL_SIT, 1.0F, 1.0F);
            this.setPose(Pose.SITTING);
            this.resetLastPoseChangeTick(-this.level().getGameTime());
        }
    }

    public void standUp() {
        if (this.isCamelSitting()) {
            this.playSound(SoundEvents.CAMEL_STAND, 1.0F, 1.0F);
            this.setPose(Pose.STANDING);
            this.resetLastPoseChangeTick(this.level().getGameTime());
        }
    }


    public boolean isCamelVisuallySitting() {
        return this.getPoseTime() < 0L != this.isCamelSitting();
    }

    public long getPoseTime() {
        return this.level().getGameTime() - Math.abs(this.entityData.get(LAST_POSE_CHANGE_TICK));
    }

    private boolean isVisuallySittingDown() {
        return this.isCamelSitting() && this.getPoseTime() < 20 && this.getPoseTime() >= 0L;
    }

    public boolean isCamelSitting() {
        return this.entityData.get(LAST_POSE_CHANGE_TICK) < 0L;
    }

    public boolean isInPoseTransition() {
        long i = this.getPoseTime();
        return i < (long) (20);
    }

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
        }

        if (this.isCamelVisuallySitting()) {
            this.sitUpAnimationState.stop();
            this.idleAnimationState.stop();
            if (this.isVisuallySittingDown()) {
                this.sitDownAnimationState.startIfStopped(this.tickCount);
                this.sitAnimationState.stop();
            } else {
                this.sitDownAnimationState.stop();
                this.sitAnimationState.startIfStopped(this.tickCount);
            }
        } else {
            this.sitDownAnimationState.stop();
            this.sitAnimationState.stop();

            this.sitUpAnimationState.animateWhen(this.isInPoseTransition() && this.getPoseTime() >= 0L, this.tickCount);
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
        if (!flag && this.isSaddled() && !this.isVehicle() && !pPlayer.isSecondaryUseActive() && pPlayer.isCrouching()) {
            if (!this.level().isClientSide) {
                pPlayer.startRiding(this);
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        if (this.isTame() && itemstack.is(Items.SADDLE)) {

        }

        //sitting
        if (this.isTame() && this.level().isClientSide()) {

            InteractionResult interactionresult = super.mobInteract(pPlayer, pHand);
            if (!interactionresult.consumesAction() && this.isOwnedBy(pPlayer) && !pPlayer.isCrouching()) {


                System.out.println(this.isOrderedToSit());


                this.setOrderedToSit(!this.isOrderedToSit());

                if (this.isOrderedToSit()) {
                    sitDown();
                } else {
                    standUp();
                }


                this.jumping = false;
                this.navigation.stop();


                return InteractionResult.SUCCESS;
            }
        }


        if (this.level().isClientSide) {
            boolean flag1 = this.isOwnedBy(pPlayer) || this.isTame() || itemstack.is(Items.ROTTEN_FLESH) && !this.isTame();
            return flag1 ? InteractionResult.CONSUME : InteractionResult.PASS;
        } else if (this.isTame()) {
            if (itemstack.is(Items.ROTTEN_FLESH) && this.getHealth() < this.getMaxHealth()) {
                this.heal((float) itemstack.getFoodProperties(this).getNutrition() * 2);
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                this.gameEvent(GameEvent.EAT, this);
                return InteractionResult.SUCCESS;
            } else {

                InteractionResult interactionresult = super.mobInteract(pPlayer, pHand);
                return interactionresult;
            }
        } else if (itemstack.is(Items.ROTTEN_FLESH)) {
            if (!pPlayer.getAbilities().instabuild) {
                itemstack.shrink(1);
            }

            if (this.random.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, pPlayer)) {
                this.tame(pPlayer);
                this.setOrderedToSit(true);
                this.jumping = false;
                this.navigation.stop();
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
            this.level().playSound((Player)null, this, SoundEvents.STRIDER_SADDLE, pSource, 0.5F, 1.0F);
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