package io.github.frqnny.golemsgalore.client.render;

import io.github.frqnny.golemsgalore.GolemsGalore;
import io.github.frqnny.golemsgalore.GolemsGaloreClient;
import io.github.frqnny.golemsgalore.client.render.feature.ModGolemCrackFeatureRenderer;
import io.github.frqnny.golemsgalore.client.render.feature.ModGolemFlowerFeatureRenderer;
import io.github.frqnny.golemsgalore.client.render.model.ModGolemEntityModel;
import io.github.frqnny.golemsgalore.entity.LaserGolemEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;

public class LaserGolemEntityRenderer extends MobEntityRenderer<LaserGolemEntity, ModGolemEntityModel<LaserGolemEntity>> {
    private static final Identifier TEXTURE = GolemsGalore.id("textures/entity/golem/laser_golem.png");
    private static final Identifier EXPLOSION_BEAM_TEXTURE = GolemsGalore.id("textures/entity/laser_beam.png");
    private static final RenderLayer LAYER = RenderLayer.getEntityCutoutNoCull(EXPLOSION_BEAM_TEXTURE);


    public LaserGolemEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new ModGolemEntityModel<>(ctx.getPart(GolemsGaloreClient.MOD_GOLEM)), 1);
        this.addFeature(new ModGolemCrackFeatureRenderer<>(this));
        this.addFeature(new ModGolemFlowerFeatureRenderer<>(this));
    }

    private static Vec3d fromLerpedPosition(LivingEntity entity, double yOffset, float delta) {
        double x = MathHelper.lerp(delta, entity.lastRenderX, entity.getX() - 0);
        double y = MathHelper.lerp(delta, entity.lastRenderY, entity.getY()) + yOffset;
        double z = MathHelper.lerp(delta, entity.lastRenderZ, entity.getZ());
        return new Vec3d(x, y, z);
    }

    private static void vertexLaser(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, float x, float y, float z, float u, float v) {
        vertexConsumer.vertex(matrix4f, x, y, z).color(255, 1, 1, 255).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
    }

    @Override
    public Identifier getTexture(LaserGolemEntity entity) {
        return TEXTURE;
    }

    protected void setupTransforms(LaserGolemEntity ironGolemEntity, MatrixStack matrixStack, float f, float g, float h) {
        super.setupTransforms(ironGolemEntity, matrixStack, f, g, h);
        if ((double) ironGolemEntity.limbDistance >= 0.01D) {
            float j = ironGolemEntity.limbAngle - ironGolemEntity.limbDistance * (1.0F - h) + 6.0F;
            float k = (Math.abs(j % 13.0F - 6.5F) - 3.25F) / 3.25F;
            matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(6.5F * k));
        }
    }

    public boolean shouldRender(LaserGolemEntity guardianEntity, Frustum frustum, double d, double e, double f) {
        if (super.shouldRender(guardianEntity, frustum, d, e, f)) {
            return true;
        } else {
            if (guardianEntity.hasBeamTarget()) {
                LivingEntity livingEntity = guardianEntity.getBeamTarget();
                if (livingEntity != null) {
                    Vec3d vec3d = fromLerpedPosition(livingEntity, (double) livingEntity.getHeight() * 0.5D, 1.0F);
                    Vec3d vec3d2 = fromLerpedPosition(guardianEntity, guardianEntity.getStandingEyeHeight(), 1.0F);
                    return frustum.isVisible(new Box(vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y, vec3d.z));
                }
            }

            return false;
        }
    }

    public void render(LaserGolemEntity golem, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int light) {
        super.render(golem, yaw, tickDelta, matrices, vertexConsumerProvider, light);
        LivingEntity target = golem.getBeamTarget();
        if (target != null) {
            //some simple variables we need to get out of the way
            float exactTime = (float) golem.world.getTime() + tickDelta;
            float k = exactTime * 0.5F % 1.0F;
            float eyeHeight = golem.getStandingEyeHeight() + 0.08F;
            matrices.push();

            //translates this to the starting eyeheight
            matrices.translate(0.0D, eyeHeight, 0.0D);
            //the following is used to point the matrix towards the target
            Vec3d targetVec = fromLerpedPosition(target, (double) target.getHeight() * 0.5D, tickDelta);
            Vec3d originVec = fromLerpedPosition(golem, eyeHeight, tickDelta);
            Vec3d vec3d3 = targetVec.subtract(originVec);
            float length = (float) (vec3d3.length());
            vec3d3 = vec3d3.normalize();
            float arc_cosine_y = (float) Math.acos(vec3d3.y);
            float o = (float) Math.atan2(vec3d3.z, vec3d3.x);

            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((((float) Math.PI / 2F) - o) * (180F / (float) Math.PI)));
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(arc_cosine_y * (180F / (float) Math.PI)));

            //???
            float q = exactTime * 0.05F * -1.5F;
            float x = MathHelper.cos(q + 2.3561945F) * 0.282F;
            float y = MathHelper.sin(q + 2.3561945F) * 0.282F;
            float z = MathHelper.cos(q + ((float) Math.PI / 4F)) * 0.282F;
            float aa = MathHelper.sin(q + ((float) Math.PI / 4F)) * 0.282F;
            float ab = MathHelper.cos(q + 3.926991F) * 0.282F;
            float ac = MathHelper.sin(q + 3.926991F) * 0.282F;
            float ad = MathHelper.cos(q + 5.4977875F) * 0.282F;
            float ae = MathHelper.sin(q + 5.4977875F) * 0.282F;
            float af = MathHelper.cos(q + (float) Math.PI) * 0.2F;
            float ag = MathHelper.sin(q + (float) Math.PI) * 0.2F;
            float ah = MathHelper.cos(q) * 0.2F;
            float ai = MathHelper.sin(q) * 0.2F;
            float aj = MathHelper.cos(q + ((float) Math.PI / 2F)) * 0.2F;
            float ak = MathHelper.sin(q + ((float) Math.PI / 2F)) * 0.2F;
            float al = MathHelper.cos(q + ((float) Math.PI * 1.5F)) * 0.2F;
            float am = MathHelper.sin(q + ((float) Math.PI * 1.5F)) * 0.2F;
            float aq = -1.0F + k;
            float ar = length * 2.5F + aq;
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);

            MatrixStack.Entry entry = matrices.peek();
            Matrix4f matrix4f = entry.getPositionMatrix();
            Matrix3f matrix3f = entry.getNormalMatrix();

            vertexLaser(vertexConsumer, matrix4f, matrix3f, af, length, ag, 0.4999F, ar);
            vertexLaser(vertexConsumer, matrix4f, matrix3f, af, 0.0F, ag, 0.4999F, aq);
            vertexLaser(vertexConsumer, matrix4f, matrix3f, ah, 0.0F, ai, 0.0F, aq);
            vertexLaser(vertexConsumer, matrix4f, matrix3f, ah, length, ai, 0.0F, ar);
            vertexLaser(vertexConsumer, matrix4f, matrix3f, aj, length, ak, 0.4999F, ar);
            vertexLaser(vertexConsumer, matrix4f, matrix3f, aj, 0.0F, ak, 0.4999F, aq);
            vertexLaser(vertexConsumer, matrix4f, matrix3f, al, 0.0F, am, 0.0F, aq);
            vertexLaser(vertexConsumer, matrix4f, matrix3f, al, length, am, 0.0F, ar);
            float as = 0.0F;
            if (golem.age % 2 == 0) {
                as = 0.5F;
            }


            vertexLaser(vertexConsumer, matrix4f, matrix3f, x, length, y, 0.5F, as + 0.5F);
            vertexLaser(vertexConsumer, matrix4f, matrix3f, z, length, aa, 1.0F, as + 0.5F);
            vertexLaser(vertexConsumer, matrix4f, matrix3f, ad, length, ae, 1.0F, as);
            vertexLaser(vertexConsumer, matrix4f, matrix3f, ab, length, ac, 0.5F, as);
            matrices.pop();
        }

    }
}
