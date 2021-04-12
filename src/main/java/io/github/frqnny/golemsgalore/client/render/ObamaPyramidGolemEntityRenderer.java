package io.github.frqnny.golemsgalore.client.render;

import dev.monarkhes.myron.api.Myron;
import io.github.frqnny.golemsgalore.GolemsGalore;
import io.github.frqnny.golemsgalore.entity.LaserGolemEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

public class ObamaPyramidGolemEntityRenderer extends EntityRenderer<LaserGolemEntity> {
    private static final Identifier EXPLOSION_BEAM_TEXTURE = GolemsGalore.id("textures/entity/laser_beam.png");
    private static final Identifier MODEL = GolemsGalore.id("models/misc/final");
    private static final RenderLayer LAYER = RenderLayer.getEntityCutoutNoCull(EXPLOSION_BEAM_TEXTURE);

    public ObamaPyramidGolemEntityRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    public static Vec3d fromLerpedPosition(Entity entity, double yOffset, float delta) {
        double d = MathHelper.lerp(delta, entity.lastRenderX, entity.getX());
        double e = MathHelper.lerp(delta, entity.lastRenderY, entity.getY()) + yOffset;
        double f = MathHelper.lerp(delta, entity.lastRenderZ, entity.getZ());
        return new Vec3d(d, e, f);
    }

    private static void vertexLaser(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, float x, float y, float z, float u, float v) {
        vertexConsumer.vertex(matrix4f, x, y, z).color(255, 1, 1, 255).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
    }

    @Override
    public Identifier getTexture(LaserGolemEntity entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }

    @Override
    public void render(LaserGolemEntity golem, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        VertexConsumer consumer = vertexConsumers.getBuffer(TexturedRenderLayers.getEntityTranslucentCull());
        LivingEntity target = golem.getBeamTarget();
        float eyeHeight = golem.getStandingEyeHeight() + 0.06F;

        BakedModel model = Myron.getModel(MODEL);

        if (model != null) {
            matrices.push();

            if (target != null) {
                Vec3d targetVec = fromLerpedPosition(target, (double) target.getHeight() * 0.5D, tickDelta);
                Vec3d originVec = fromLerpedPosition(golem, eyeHeight, tickDelta);
                Vec3d vec3d3 = targetVec.subtract(originVec);
                vec3d3 = vec3d3.normalize();
                float o = (float) Math.atan2(vec3d3.z, vec3d3.x);
                float angle = (((float) Math.PI / 2F) - o) * (180F / (float) Math.PI) + 90;
                matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(angle));
                //matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(n * (180F / (float) Math.PI)));
            } else {
                matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion((golem.world.getTime() + tickDelta) * 4));

            }
            matrices.translate(0, 2.0, 0);

            MatrixStack.Entry entry = matrices.peek();

            int overlay = LivingEntityRenderer.getOverlay(golem, 0.0F);
            model.getQuads(null, null, golem.world.random).forEach(quad -> consumer.quad(entry, quad, 1F, 1F, 1F, light, overlay));

            matrices.pop();
        }

        super.render(golem, yaw, tickDelta, matrices, vertexConsumers, light);
        if (target != null) {
            //some simple variables we need to get out of the way
            float exactTime = (float) golem.world.getTime() + tickDelta;
            float k = exactTime * 0.5F % 1.0F;
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

            //Vector3f uh = new Vector3f(0, 0, 1);
            //uh.rotate(Vector3f.POSITIVE_Y.getDegreesQuaternion(golem.getHeadYaw()));
            //matrices.translate(uh.getX(), uh.getY(), uh.getZ());
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((((float) Math.PI / 2F) - o) * (180F / (float) Math.PI)));
            matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(arc_cosine_y * (180F / (float) Math.PI)));

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
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(LAYER);
            /*
            if (FabricLoader.getInstance().isModLoaded("frex")) {
                vertexConsumer = ((FrexVertexConsumerProvider) vertexConsumerProvider).getConsumer(Renderer.get().materialFinder().emissive(true).texture(EXPLOSION_BEAM_TEXTURE).cutout(true).cull(false).transparency(0).find());
            } else {
                vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);
            }

             */

            MatrixStack.Entry entry = matrices.peek();
            Matrix4f matrix4f = entry.getModel();
            Matrix3f matrix3f = entry.getNormal();

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
