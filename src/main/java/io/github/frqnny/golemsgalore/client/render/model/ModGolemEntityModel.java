package io.github.frqnny.golemsgalore.client.render.model;

import com.google.common.collect.ImmutableList;
import io.github.frqnny.golemsgalore.entity.ModGolemEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ModGolemEntityModel<T extends ModGolemEntity> extends CompositeEntityModel<T> {
    private final ModelPart head;
    private final ModelPart torso;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public ModGolemEntityModel() {
        this.head = (new ModelPart(this)).setTextureSize(128, 128);
        this.head.setPivot(0.0F, -7.0F, -2.0F);
        this.head.setTextureOffset(0, 0).addCuboid(-4.0F, -12.0F, -5.5F, 8.0F, 10.0F, 8.0F, 0.0F);
        this.head.setTextureOffset(24, 0).addCuboid(-1.0F, -5.0F, -7.5F, 2.0F, 4.0F, 2.0F, 0.0F);
        this.torso = (new ModelPart(this)).setTextureSize(128, 128);
        this.torso.setPivot(0.0F, -7.0F, 0.0F);
        this.torso.setTextureOffset(0, 40).addCuboid(-9.0F, -2.0F, -6.0F, 18.0F, 12.0F, 11.0F, 0.0F);
        this.torso.setTextureOffset(0, 70).addCuboid(-4.5F, 10.0F, -3.0F, 9.0F, 5.0F, 6.0F, 0.5F);
        this.rightArm = (new ModelPart(this)).setTextureSize(128, 128);
        this.rightArm.setPivot(0.0F, -7.0F, 0.0F);
        this.rightArm.setTextureOffset(60, 21).addCuboid(-13.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F, 0.0F);
        this.leftArm = (new ModelPart(this)).setTextureSize(128, 128);
        this.leftArm.setPivot(0.0F, -7.0F, 0.0F);
        this.leftArm.setTextureOffset(60, 58).addCuboid(9.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F, 0.0F);
        this.rightLeg = (new ModelPart(this, 0, 22)).setTextureSize(128, 128);
        this.rightLeg.setPivot(-4.0F, 11.0F, 0.0F);
        this.rightLeg.setTextureOffset(37, 0).addCuboid(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F, 0.0F);
        this.leftLeg = (new ModelPart(this, 0, 22)).setTextureSize(128, 128);
        this.leftLeg.mirror = true;
        this.leftLeg.setTextureOffset(60, 0).setPivot(5.0F, 11.0F, 0.0F);
        this.leftLeg.addCuboid(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F, 0.0F);
    }

    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.head, this.torso, this.rightLeg, this.leftLeg, this.rightArm, this.leftArm);
    }

    public void setAngles(T golem, float f, float g, float h, float i, float j) {
        this.head.yaw = i * 0.017453292F;
        this.head.pitch = j * 0.017453292F;
        this.rightLeg.pitch = -1.5F * MathHelper.method_24504(f, 13.0F) * g;
        this.leftLeg.pitch = 1.5F * MathHelper.method_24504(f, 13.0F) * g;
        this.rightLeg.yaw = 0.0F;
        this.leftLeg.yaw = 0.0F;
    }

    public void animateModel(T golem, float f, float g, float h) {
        int i = golem.getAttackTicksLeft();
        if (i > 0) {
            this.rightArm.pitch = -2.0F + 1.5F * MathHelper.method_24504((float) i - h, 10.0F);
            this.leftArm.pitch = -2.0F + 1.5F * MathHelper.method_24504((float) i - h, 10.0F);
        } else {
            int j = golem.getLookingAtVillagerTicks();
            if (j > 0) {
                this.rightArm.pitch = -0.8F + 0.025F * MathHelper.method_24504((float) j, 70.0F);
                this.leftArm.pitch = 0.0F;
            } else {
                this.rightArm.pitch = (-0.2F + 1.5F * MathHelper.method_24504(f, 13.0F)) * g;
                this.leftArm.pitch = (-0.2F - 1.5F * MathHelper.method_24504(f, 13.0F)) * g;
            }
        }

    }

    public ModelPart getRightArm() {
        return this.rightArm;
    }
}