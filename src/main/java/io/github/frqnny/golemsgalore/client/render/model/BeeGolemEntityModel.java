package io.github.frqnny.golemsgalore.client.render.model;

import com.google.common.collect.ImmutableList;
import io.github.frqnny.golemsgalore.entity.BeeGolemEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.util.math.MathHelper;

public class BeeGolemEntityModel<T extends BeeGolemEntity> extends AnimalModel<T> {
    private static final float BONE_BASE_Y_PIVOT = 19.0F;
    private static final String BONE = "bone";
    private static final String STINGER = "stinger";
    private static final String LEFT_ANTENNA = "left_antenna";
    private static final String RIGHT_ANTENNA = "right_antenna";
    private static final String FRONT_LEGS = "front_legs";
    private static final String MIDDLE_LEGS = "middle_legs";
    private static final String BACK_LEGS = "back_legs";
    private final ModelPart bone;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart frontLegs;
    private final ModelPart middleLegs;
    private final ModelPart backLegs;
    private final ModelPart stinger;
    private final ModelPart leftAntenna;
    private final ModelPart rightAntenna;
    private float bodyPitch;

    public BeeGolemEntityModel(ModelPart root) {
        super(false, 24.0f, 0.0f);
        this.bone = root.getChild(BONE);
        ModelPart modelPart = this.bone.getChild(EntityModelPartNames.BODY);
        this.stinger = modelPart.getChild(STINGER);
        this.leftAntenna = modelPart.getChild(LEFT_ANTENNA);
        this.rightAntenna = modelPart.getChild(RIGHT_ANTENNA);
        this.rightWing = this.bone.getChild(EntityModelPartNames.RIGHT_WING);
        this.leftWing = this.bone.getChild(EntityModelPartNames.LEFT_WING);
        this.frontLegs = this.bone.getChild(FRONT_LEGS);
        this.middleLegs = this.bone.getChild(MIDDLE_LEGS);
        this.backLegs = this.bone.getChild(BACK_LEGS);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(BONE, ModelPartBuilder.create(), ModelTransform.pivot(0.0f, 19.0f, 0.0f));
        ModelPartData modelPartData3 = modelPartData2.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 0).cuboid(-3.5f, -4.0f, -5.0f, 7.0f, 7.0f, 10.0f), ModelTransform.NONE);
        modelPartData3.addChild(STINGER, ModelPartBuilder.create().uv(26, 7).cuboid(0.0f, -1.0f, 5.0f, 0.0f, 1.0f, 2.0f), ModelTransform.NONE);
        modelPartData3.addChild(LEFT_ANTENNA, ModelPartBuilder.create().uv(2, 0).cuboid(1.5f, -2.0f, -3.0f, 1.0f, 2.0f, 3.0f), ModelTransform.pivot(0.0f, -2.0f, -5.0f));
        modelPartData3.addChild(RIGHT_ANTENNA, ModelPartBuilder.create().uv(2, 3).cuboid(-2.5f, -2.0f, -3.0f, 1.0f, 2.0f, 3.0f), ModelTransform.pivot(0.0f, -2.0f, -5.0f));
        modelPartData3.addChild("nose", ModelPartBuilder.create().uv(56, 0).cuboid(-1.0f, 7.0F, -5.0F, 2F, 4F, 2F), ModelTransform.pivot(0.0F, -7.0F, -2.0F));
        //nose
        Dilation dilation = new Dilation(0.001f);
        modelPartData2.addChild(EntityModelPartNames.RIGHT_WING, ModelPartBuilder.create().uv(0, 18).cuboid(-9.0f, 0.0f, 0.0f, 9.0f, 0.0f, 6.0f, dilation), ModelTransform.of(-1.5f, -4.0f, -3.0f, 0.0f, -0.2618f, 0.0f));
        modelPartData2.addChild(EntityModelPartNames.LEFT_WING, ModelPartBuilder.create().uv(0, 18).mirrored().cuboid(0.0f, 0.0f, 0.0f, 9.0f, 0.0f, 6.0f, dilation), ModelTransform.of(1.5f, -4.0f, -3.0f, 0.0f, 0.2618f, 0.0f));
        modelPartData2.addChild(FRONT_LEGS, ModelPartBuilder.create().cuboid(FRONT_LEGS, -5.0f, 0.0f, 0.0f, 7, 2, 0, 26, 1), ModelTransform.pivot(1.5f, 3.0f, -2.0f));
        modelPartData2.addChild(MIDDLE_LEGS, ModelPartBuilder.create().cuboid(MIDDLE_LEGS, -5.0f, 0.0f, 0.0f, 7, 2, 0, 26, 3), ModelTransform.pivot(1.5f, 3.0f, 0.0f));
        modelPartData2.addChild(BACK_LEGS, ModelPartBuilder.create().cuboid(BACK_LEGS, -5.0f, 0.0f, 0.0f, 7, 2, 0, 26, 5), ModelTransform.pivot(1.5f, 3.0f, 2.0f));
        return TexturedModelData.of(modelData, 64, 64);
    }

    public void animateModel(T beeEntity, float f, float g, float h) {
        super.animateModel(beeEntity, f, g, h);
        this.bodyPitch = beeEntity.getBodyPitch(h);
        this.stinger.visible = true;
    }

    public void setAngles(T beeEntity, float f, float g, float h, float i, float j) {
        this.rightWing.pitch = 0.0F;
        this.leftAntenna.pitch = 0.0F;
        this.rightAntenna.pitch = 0.0F;
        this.bone.pitch = 0.0F;
        boolean bl = beeEntity.isOnGround() && beeEntity.getVelocity().lengthSquared() < 1.0E-7D;
        float k;
        if (bl) {
            this.rightWing.yaw = -0.2618F;
            this.rightWing.roll = 0.0F;
            this.leftWing.pitch = 0.0F;
            this.leftWing.yaw = 0.2618F;
            this.leftWing.roll = 0.0F;
            this.frontLegs.pitch = 0.0F;
            this.middleLegs.pitch = 0.0F;
            this.backLegs.pitch = 0.0F;
        } else {
            k = h * 120.32113F * 0.017453292F;
            this.rightWing.yaw = 0.0F;
            this.rightWing.roll = MathHelper.cos(k) * 3.1415927F * 0.15F;
            this.leftWing.pitch = this.rightWing.pitch;
            this.leftWing.yaw = this.rightWing.yaw;
            this.leftWing.roll = -this.rightWing.roll;
            this.frontLegs.pitch = 0.7853982F;
            this.middleLegs.pitch = 0.7853982F;
            this.backLegs.pitch = 0.7853982F;
            this.bone.pitch = 0.0F;
            this.bone.yaw = 0.0F;
            this.bone.roll = 0.0F;
        }

        if (!beeEntity.hasAngerTime()) {
            this.bone.pitch = 0.0F;
            this.bone.yaw = 0.0F;
            this.bone.roll = 0.0F;
            if (!bl) {
                k = MathHelper.cos(h * 0.18F);
                this.bone.pitch = 0.1F + k * 3.1415927F * 0.025F;
                this.leftAntenna.pitch = k * 3.1415927F * 0.03F;
                this.rightAntenna.pitch = k * 3.1415927F * 0.03F;
                this.frontLegs.pitch = -k * 3.1415927F * 0.1F + 0.3926991F;
                this.backLegs.pitch = -k * 3.1415927F * 0.05F + 0.7853982F;
                this.bone.pivotY = BONE_BASE_Y_PIVOT - MathHelper.cos(h * 0.18F) * 0.9F;
            }
        }

        if (this.bodyPitch > 0.0F) {
            this.bone.pitch = ModelUtil.interpolateAngle(this.bone.pitch, 3.0915928F, this.bodyPitch);
        }

    }

    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of();
    }

    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.bone);
    }
}
