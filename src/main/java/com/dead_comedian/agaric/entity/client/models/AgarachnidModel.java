// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

package com.dead_comedian.agaric.entity.client.models;

import com.dead_comedian.agaric.entity.AgarachnidEntity;
import com.dead_comedian.agaric.entity.client.animations.AgaricAnimations;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.joml.Math;

public class AgarachnidModel<T extends AgarachnidEntity> extends HierarchicalModel<T> {
    private final ModelPart bone;

    private final ModelPart head;


    public AgarachnidModel(ModelPart root) {
        this.bone = root.getChild("bone");
        this.head = root.getChild("bone").getChild("funnymoney").getChild("body").getChild("head");

    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition funnymoney = bone.addOrReplaceChild("funnymoney", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition body = funnymoney.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, -15.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-28.0F, -46.0F, -28.0F, 56.0F, 20.0F, 56.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, 0.0F));

        PartDefinition Sleeping = head.addOrReplaceChild("Sleeping", CubeListBuilder.create().texOffs(112, 192).addBox(-18.0F, -42.0F, -18.0F, 36.0F, 28.0F, 36.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.0F, 0.0F));

        PartDefinition Not_Sleeping = head.addOrReplaceChild("Not_Sleeping", CubeListBuilder.create().texOffs(0, 76).addBox(-18.0F, -42.0F, -18.0F, 36.0F, 28.0F, 36.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.0F, 0.0F));

        PartDefinition Right_Fang = head.addOrReplaceChild("Right_Fang", CubeListBuilder.create().texOffs(86, 140).addBox(-3.0F, -3.5F, -5.0F, 6.0F, 11.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, -2.5F, -19.0F));

        PartDefinition Left_Fang = head.addOrReplaceChild("Left_Fang", CubeListBuilder.create().texOffs(86, 140).mirror().addBox(-3.0F, -6.5F, -5.0F, 6.0F, 11.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(6.0F, 0.5F, -19.0F));

        PartDefinition Right_Hand = body.addOrReplaceChild("Right_Hand", CubeListBuilder.create().texOffs(0, 140).addBox(-15.5F, -4.0F, -4.0F, 19.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-21.5F, -11.0F, -13.0F, 0.0F, 0.0F, -1.309F));

        PartDefinition Left_Hand = body.addOrReplaceChild("Left_Hand", CubeListBuilder.create().texOffs(0, 140).addBox(-3.5F, -4.0F, -4.0F, 19.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(21.5F, -11.0F, -13.0F, 0.0F, 0.0F, 1.309F));

        PartDefinition right_front_leg = funnymoney.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(54, 140).addBox(-4.0F, -2.5F, -8.0F, 8.0F, 19.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-22.0F, -16.5F, -7.0F));

        PartDefinition left_front_leg = funnymoney.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(54, 140).mirror().addBox(-4.0F, -2.5F, -4.0F, 8.0F, 19.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(22.0F, -16.5F, -7.0F));

        PartDefinition right_front_leg2 = funnymoney.addOrReplaceChild("right_front_leg2", CubeListBuilder.create().texOffs(54, 140).addBox(-4.0F, -2.5F, -4.0F, 8.0F, 19.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-22.0F, -16.5F, 12.0F));

        PartDefinition left_front_leg2 = funnymoney.addOrReplaceChild("left_front_leg2", CubeListBuilder.create().texOffs(54, 140).mirror().addBox(-4.0F, -2.5F, -4.0F, 8.0F, 19.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(22.0F, -16.5F, 12.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        if (!entity.isOrderedToSit()) {
            this.applyHeadRotation(netHeadYaw, headPitch, ageInTicks);

        }

        this.animate(entity.walkAnimationState, AgaricAnimations.AGARACHNID_WALK, ageInTicks, 1f);
        this.animate(entity.idleAnimationState, AgaricAnimations.AGARACHNID_IDLE, ageInTicks, 1f);
        this.animate(entity.sitAnimationState, AgaricAnimations.AGARACHNID_SIT, ageInTicks, 1f);
        this.animate(entity.sitDownAnimationState, AgaricAnimations.AGARACHNID_SIT_TRANSITION_IN, ageInTicks, 1f);
        this.animate(entity.sitUpAnimationState, AgaricAnimations.AGARACNID_SIT_TRANSITION_OUT, ageInTicks, 1f);

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
