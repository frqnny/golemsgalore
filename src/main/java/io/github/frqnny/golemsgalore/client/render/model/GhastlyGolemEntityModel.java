package io.github.frqnny.golemsgalore.client.render.model;

import com.google.common.collect.ImmutableList;
import io.github.frqnny.golemsgalore.entity.GhastlyGolemEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CompositeEntityModel;

public class GhastlyGolemEntityModel extends CompositeEntityModel<GhastlyGolemEntity> {
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart head;

    public GhastlyGolemEntityModel() {
        textureWidth = 128;
        textureHeight = 128;
        body = new ModelPart(this);
        body.setPivot(0.0F, 0.0F, 0.0F);
        body.setTextureOffset(0, 0).addCuboid(-9.0F, -9.0F, -6.0F, 18.0F, 12.0F, 11.0F, 0.0F, false);

        ModelPart cube_r1 = new ModelPart(this);
        cube_r1.setPivot(0.0F, 0.0F, 0.0F);
        body.addChild(cube_r1);
        setRotationAngle(cube_r1, 0.2618F, 0.0F, 0.0F);
        cube_r1.setTextureOffset(47, 0).addCuboid(-5.0F, 13.0F, 4.0F, 10.0F, 4.0F, 7.0F, 0.0F, false);

        ModelPart cube_r2 = new ModelPart(this);
        cube_r2.setPivot(0.0F, 0.0F, 0.0F);
        body.addChild(cube_r2);
        setRotationAngle(cube_r2, 0.1745F, 0.0F, 0.0F);
        cube_r2.setTextureOffset(37, 30).addCuboid(-6.0F, 8.0F, 2.0F, 12.0F, 5.0F, 7.0F, 0.0F, false);

        ModelPart cube_r3 = new ModelPart(this);
        cube_r3.setPivot(0.0F, 0.0F, 0.0F);
        body.addChild(cube_r3);
        setRotationAngle(cube_r3, 0.0873F, 0.0F, 0.0F);
        cube_r3.setTextureOffset(0, 23).addCuboid(-7.0F, 2.0F, -1.0F, 14.0F, 6.0F, 8.0F, 0.0F, false);

        rightArm = new ModelPart(this);
        rightArm.setPivot(0.0F, -5.0F, 0.0F);
        rightArm.setTextureOffset(20, 37).addCuboid(8.99F, -4.5F, -3.0F, 4.0F, 30.0F, 6.0F, 0.0F, false);

        leftArm = new ModelPart(this);
        leftArm.setPivot(0.0F, 0.0F, 0.0F);
        leftArm.setTextureOffset(0, 37).addCuboid(-12.99F, -9.5F, -3.0F, 4.0F, 30.0F, 6.0F, 0.0F, false);

        head = new ModelPart(this);
        head.setPivot(0.0F, 0.0F, 0.0F);
        head.setTextureOffset(40, 42).addCuboid(-4.0F, -18.99F, -7.5F, 8.0F, 10.0F, 8.0F, 0.0F, false);
        head.setTextureOffset(0, 0).addCuboid(-1.0F, -11.99F, -9.5F, 2.0F, 4.0F, 2.0F, 0.0F, false);
    }

    public static void setRotationAngle(ModelPart bone, float x, float y, float z) {
        bone.pitch = x;
        bone.yaw = y;
        bone.roll = z;
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(head, body, leftArm, rightArm);
    }

    @Override
    public void setAngles(GhastlyGolemEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.head.yaw = headYaw * 0.017453292F;
        this.head.pitch = headPitch * 0.017453292F;
    }

    @Override
    public void animateModel(GhastlyGolemEntity golem, float limbAngle, float limbDistance, float tickDelta) {

        //this.rightArm.pitch = -2.45F;
        //this.rightArm.pitch = -2.0F + 1.5F * MathHelper.method_24504((float) 10, 10.0F);
        //this.leftArm.pitch = -2.0F + 1.5F * MathHelper.method_24504((float) 10 , 10.0F);

        int i = golem.getAttackTicksLeft();
        //System.out.println(i);
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