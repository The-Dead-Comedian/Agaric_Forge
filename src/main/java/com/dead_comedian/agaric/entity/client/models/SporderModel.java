package com.dead_comedian.agaric.entity.client.models;// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.dead_comedian.agaric.entity.SporderEntity;
import com.dead_comedian.agaric.entity.client.animations.AgaricAnimations;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;
import org.checkerframework.checker.units.qual.A;
import org.joml.Math;


public class SporderModel<T extends SporderEntity> extends HierarchicalModel<T> {
    private final ModelPart bone;

    private final ModelPart head;


    public SporderModel(ModelPart root) {
        this.bone = root.getChild("bone");
        this.head = root.getChild("bone").getChild("funnymoney").getChild("body").getChild("head");

    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0F, 9.75F, 0.0F));

        PartDefinition funnymoney = bone.addOrReplaceChild("funnymoney", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition body = funnymoney.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition right_front_leg = body.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(0, 51).addBox(-4.0F, -5.5F, -2.5F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offset(-7.0F, 7.75F, -5.5F));

        PartDefinition left_front_leg = body.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(0, 51).addBox(0.0F, -5.5F, -2.5F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offset(7.0F, 7.75F, -5.5F));

        PartDefinition left_front_leg2 = body.addOrReplaceChild("left_front_leg2", CubeListBuilder.create().texOffs(0, 51).addBox(0.0F, -5.5F, -0.5F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offset(7.0F, 7.75F, 4.5F));

        PartDefinition right_front_leg2 = body.addOrReplaceChild("right_front_leg2", CubeListBuilder.create().texOffs(0, 51).addBox(-4.0F, -5.5F, -0.5F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offset(-7.0F, 7.75F, 4.5F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, -4.25F, 0.0F));

        PartDefinition eepy = head.addOrReplaceChild("eepy", CubeListBuilder.create().texOffs(35, 58).addBox(-7.0F, 0.25F, -7.0F, 14.0F, 8.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.25F, 0.0F));

        PartDefinition noteepy = head.addOrReplaceChild("noteepy", CubeListBuilder.create().texOffs(0, 29).addBox(-7.0F, 0.25F, -7.0F, 14.0F, 8.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.25F, 0.0F));

        PartDefinition fang2 = head.addOrReplaceChild("fang2", CubeListBuilder.create().texOffs(57, 43).addBox(0.0F, 3.25F, -3.0F, 0.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(57, 30).addBox(-1.5F, -0.75F, -2.0F, 3.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.5F, 10.25F, -7.0F));

        PartDefinition fang = head.addOrReplaceChild("fang", CubeListBuilder.create().texOffs(57, 30).addBox(-1.5F, -0.75F, -2.0F, 3.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(57, 43).addBox(0.0F, 3.25F, -3.0F, 0.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.5F, 10.25F, -7.0F));

        PartDefinition no_cap = head.addOrReplaceChild("no_cap", CubeListBuilder.create().texOffs(0, 0).addBox(-10.0F, -4.5F, -10.0F, 20.0F, 9.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }


    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        if (!((SporderEntity) entity).isOrderedToSit()) {
            this.applyHeadRotation(netHeadYaw, headPitch, ageInTicks);
            this.animateWalk(AgaricAnimations.WALK_SPORDER, limbSwing, limbSwingAmount, 2f, 2.5f);
        }


        this.animate(entity.idleAnimationState, AgaricAnimations.IDLE_SPORDER, ageInTicks, 1f);
        this.animate(entity.sitAnimationState, AgaricAnimations.SIT_SPORDER, ageInTicks, 1f);
        this.animate(entity.sitDownAnimationState, AgaricAnimations.SIT_TRANSITION_IN_SPORDER, ageInTicks, 1f);
        this.animate(entity.sitUpAnimationState, AgaricAnimations.SIT_TRANSITION_OUT_SPORDER, ageInTicks, 1f);
        this.animate(entity.bounceAnimationState, AgaricAnimations.SPORDER_BOUNCING, ageInTicks, 1f);
    }

    private void applyHeadRotation(float pNetHeadYaw, float pHeadPitch, float pAgeInTicks) {
        pNetHeadYaw = Math.clamp(pNetHeadYaw, -30.0F, 30.0F);
        pHeadPitch = Math.clamp(pHeadPitch, -25.0F, 45.0F);

        this.head.yRot = pNetHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = pHeadPitch * ((float) Math.PI / 180F);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart root() {
        return bone;
    }
}