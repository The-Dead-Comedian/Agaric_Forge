package com.dead_comedian.agaric.mixin;


import com.dead_comedian.agaric.entity.SporderEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class PlayerMixin {
    @Shadow public abstract void readAdditionalSaveData(CompoundTag pCompound);

    @ModifyArg(
            method =  "causeFallDamage"  ,
            at = @At(value = "RETURN")
    )
    private boolean calculateFalldamage(float pFallDistance, float pMultiplier, DamageSource pSource, CallbackInfoReturnable<Boolean> cir){
        Player player = ((Player) (Object) this);
        Entity vehicle = player.getVehicle();
                return !(vehicle instanceof SporderEntity);
    }
}
