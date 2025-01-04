package com.dead_comedian.agaric.entity;

import com.dead_comedian.agaric.entity.client.animations.AgaricAnimations;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class AgarachnidEntity extends TamableAnimal implements NeutralMob{
    protected AgarachnidEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    ////////////
    //VARIABLE//
    ////////////


    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState sitAnimationState = new AnimationState();
    public final AnimationState sitUpAnimationState = new AnimationState();
    public final AnimationState sitDownAnimationState = new AnimationState();
    public final AnimationState bounceAnimationState = new AnimationState();
    private int bounceAnimationStateTimeout = 0;
    private int sitDownAnimationStateTimeout = 0;
    private int sitUpAnimationStateTimeout = 0;

    public static final EntityDataAccessor<Long> LAST_POSE_CHANGE_TICK = SynchedEntityData.defineId(AgarachnidEntity.class, EntityDataSerializers.LONG);
    public static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(AgarachnidEntity.class, EntityDataSerializers.BOOLEAN);

    ///////
    //NBT//
    ///////


    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SITTING,this.isOrderedToSit());
        this.entityData.define(LAST_POSE_CHANGE_TICK, 0L);
    }

    @VisibleForTesting
    public void resetLastPoseChangeTick(long pLastPoseChangeTick) {
        this.entityData.set(LAST_POSE_CHANGE_TICK, pLastPoseChangeTick);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putLong("LastPoseTick", this.entityData.get(LAST_POSE_CHANGE_TICK));
        pCompound.putBoolean("Tamed", this.isTame());
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setTame(pCompound.getBoolean("Tamed"));
        long i = pCompound.getLong("LastPoseTick");
        if (i < 0L) {
            this.setPose(Pose.SITTING);
        }

        this.resetLastPoseChangeTick(i);
    }


    ////////
    //MISC//
    ////////

    @Override
    protected int calculateFallDamage(float pFallDistance, float pDamageMultiplier) {
        return 0;
    }

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
        if (this.sitUpAnimationStateTimeout >= 32) {
            this.sitUpAnimationState.stop();
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
        return Animal.createLivingAttributes().add(Attributes.MAX_HEALTH, 70D).add(Attributes.FOLLOW_RANGE, 24D).add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.ARMOR_TOUGHNESS, 0.1f).add(Attributes.ATTACK_KNOCKBACK, 0.5f).add(Attributes.ATTACK_DAMAGE, 2f);
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
        if (!this.isOrderedToSit() && !(this.getDeltaMovement().horizontalDistance() > 0.01F )) {
            this.idleAnimationState.startIfStopped(this.tickCount);
            this.sitAnimationState.stop();
            this.walkAnimationState.stop();
        }else if(!this.isOrderedToSit() && this.getDeltaMovement().horizontalDistance() > 0.01F){
            this.walkAnimationState.startIfStopped(this.tickCount);
            this.sitAnimationState.stop();
            this.idleAnimationState.stop();
        }
        else if(this.isOrderedToSit() && !this.sitDownAnimationState.isStarted()){
            this.sitAnimationState.startIfStopped(this.tickCount);
            this.walkAnimationState.stop();
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

        //sitting
        if (this.isTame() && this.level().isClientSide()) {

            InteractionResult interactionresult = super.mobInteract(pPlayer, pHand);
            if (!interactionresult.consumesAction() && this.isOwnedBy(pPlayer) && !pPlayer.isCrouching()) {

                System.out.println(this.isOrderedToSit());
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
//                this.setOrderedToSit(true);
//
//                if (this.isOrderedToSit()) {
//                    sitDown();
//                }


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



    /////////////
    // NEUTRAL //
    /////////////

    @Override
    public int getRemainingPersistentAngerTime() {
        return 0;
    }

    @Override
    public void setRemainingPersistentAngerTime(int pRemainingPersistentAngerTime) {

    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return null;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID pPersistentAngerTarget) {

    }

    @Override
    public void startPersistentAngerTimer() {

    }
}