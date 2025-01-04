package com.dead_comedian.agaric.mixin;


import com.dead_comedian.agaric.entity.SporderEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin  extends LivingEntity {
    protected PlayerMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Shadow public abstract void readAdditionalSaveData(CompoundTag pCompound);

    @Inject(
            method =  "causeFallDamage"  ,
            at = @At(value = "HEAD"),
            cancellable = true)
    private void causeFalldamage(float pFallDistance, float pMultiplier, DamageSource pSource, CallbackInfoReturnable<Boolean> cir){
        Entity vehicle =    this.getVehicle();
       if(vehicle instanceof SporderEntity){
           cir.setReturnValue(false);
       }
    }
}
