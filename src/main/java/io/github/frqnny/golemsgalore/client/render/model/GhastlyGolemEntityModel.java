package io.github.frqnny.golemsgalore.client.render.model;

import io.github.frqnny.golemsgalore.entity.GhastlyGolemEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;

public class GhastlyGolemEntityModel extends SinglePartEntityModel<GhastlyGolemEntity> {
    private final ModelPart root;
    private final ModelPart rightArm;
    private final ModelPart head;

    public GhastlyGolemEntityModel(ModelPart root) {
        this.root = root;
        ModelPart body = root.getChild("body");
        ModelPart cube_r1 = root.getChild("r1");
        setRotationAngle(cube_r1, 0.2618F, 0.0F, 0.0F);
        ModelPart cube_r2 = root.getChild("r2");
        setRotationAngle(cube_r2, 0.1745F, 0.0F, 0.0F);
        ModelPart cube_r3 = root.getChild("r3");
        setRotationAngle(cube_r3, 0.0873F, 0.0F, 0.0F);
        this.head = root.getChild("head");
        this.rightArm = root.getChild("right_arm");
        ModelPart leftArm = root.getChild("left_arm");

    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("head",
                ModelPartBuilder.create()
                        .uv(40, 42)
                        .cuboid(-4.0F, -18.99F, -7.5F, 8.0F, 10.0F, 8.0F)
                        .uv(0, 0)
                        .cuboid(-1.0F, -11.99F, -9.5F, 2.0F, 4.0F, 2.0F),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        modelPartData.addChild("right_arm",
                ModelPartBuilder.create()
                        .uv(20, 37)
                        .cuboid(8.99F, -4.5F, -3.0F, 4.0F, 30.0F, 6.0F),
                ModelTransform.pivot(0.0F, -5.0F, 0.0F));
        modelPartData.addChild("left_arm",
                ModelPartBuilder.create()
                        .uv(0, 37)
                        .cuboid(-12.99F, -9.5F, -3.0F, 4.0F, 30.0F, 6.0F),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        modelPartData.addChild("body",
                ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(-9.0F, -9.0F, -6.0F, 18.0F, 12.0F, 11.0F),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        modelPartData.addChild("r1",
                ModelPartBuilder.create()
                        .uv(47, 0)
                        .cuboid(-5.0F, 13.0F, 4.0F, 10.0F, 4.0F, 7.0F),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        modelPartData.addChild("r2",
                ModelPartBuilder.create()
                        .uv(37, 30)
                        .cuboid(-6.0F, 8.0F, 2.0F, 12.0F, 5.0F, 7.0F),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        modelPartData.addChild("r3",
                ModelPartBuilder.create()
                        .uv(0, 23)
                        .cuboid(-7.0F, 2.0F, -1.0F, 14.0F, 6.0F, 8.0F),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 128, 128);
    }

    public static void setRotationAngle(ModelPart bone, float x, float y, float z) {
        bone.pitch = x;
        bone.yaw = y;
        bone.roll = z;
    }

    @Override
    public ModelPart getPart() {
        return root;
    }

    @Override
    public void setAngles(GhastlyGolemEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.head.yaw = headYaw * 0.017453292F;
        this.head.pitch = headPitch * 0.017453292F;
    }

    @Override
    public void animateModel(GhastlyGolemEntity golem, float limbAngle, float limbDistance, float tickDelta) {

        int i = golem.getAttackTicksLeft();
        if (i > 0) {
            this.rightArm.pitch = -.25F * i;
        } else {
            this.rightArm.pitch = 0F;
        }


        /*else {
            int j = golem.getLookingAtVillagerTicks();
            if (j > 0) {
                this.rightArm.pitch = -0.8F + 0.025F * MathHelper.method_24504((float) j, 70.0F);
                this.leftArm.pitch = 0.0F;
            } else {
                this.rightArm.pitch = (-0.2F + 1.5F * MathHelper.method_24504(limbAngle, 13.0F)) * limbDistance;
                this.leftArm.pitch = (-0.2F - 1.5F * MathHelper.method_24504(limbAngle, 13.0F)) * limbDistance;
            }
        }
         */

    }

}