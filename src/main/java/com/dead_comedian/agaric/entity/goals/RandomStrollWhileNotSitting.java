package com.dead_comedian.agaric.entity.goals;

import com.dead_comedian.agaric.entity.SporderEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class RandomStrollWhileNotSitting extends WaterAvoidingRandomStrollGoal {
    public RandomStrollWhileNotSitting(TamableAnimal pMob, double pSpeedModifier) {
        super(pMob, pSpeedModifier);

    }


    @Override
    @Nullable
    protected Vec3 getPosition() {

       if (this.mob.isInWaterOrBubble()&&((SporderEntity) this.mob).isOrderedToSit()) {
            Vec3 vec3 = LandRandomPos.getPos(this.mob, 15, 7);
            return vec3 == null ? super.getPosition() : vec3;
        } else {
            return this.mob.getRandom().nextFloat() >= this.probability ? LandRandomPos.getPos(this.mob, 10, 7) : super.getPosition();
        }
    }
}
