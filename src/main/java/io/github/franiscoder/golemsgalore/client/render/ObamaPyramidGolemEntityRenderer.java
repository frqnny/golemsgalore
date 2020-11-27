package io.github.franiscoder.golemsgalore.client.render;

import io.github.franiscoder.golemsgalore.entity.LaserGolemEntity;
import io.github.franiscoder.golemsgalore.init.ModItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class ObamaPyramidGolemEntityRenderer extends EntityRenderer<LaserGolemEntity> {
    public static final ItemStack OBAMAPRISM = new ItemStack(ModItems.OBAMA_PRISM);
    private static final Identifier EXPLOSION_BEAM_TEXTURE = new Identifier("textures/entity/guardian_beam.png");
    private static final RenderLayer LAYER = RenderLayer.getEntityCutoutNoCull(EXPLOSION_BEAM_TEXTURE);

    public ObamaPyramidGolemEntityRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    private static Vec3d fromLerpedPosition(LivingEntity entity, double yOffset, float delta) {
        double d = MathHelper.lerp(delta, entity.lastRenderX, entity.getX());
        double e = MathHelper.lerp(delta, entity.lastRenderY, entity.getY()) + yOffset;
        double f = MathHelper.lerp(delta, entity.lastRenderZ, entity.getZ());
        return new Vec3d(d, e, f);
    }

    private static void vertexLaser(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, float f, float g, float h, int i, int j, int k, float l, float m) {
        vertexConsumer.vertex(matrix4f, f, g, h).color(i, j, k, 255).texture(l, m).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
    }

    @Override
    public Identifier getTexture(LaserGolemEntity entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }

    @Override
    public void render(LaserGolemEntity golem, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion((golem.world.getTime() + tickDelta) * 4));
        int lightAbove = WorldRenderer.getLightmapCoordinates(golem.world, golem.getBlockPos().up());
        matrices.translate(0.5, 2.0, 0.5);

        int overlay = LivingEntityRenderer.getOverlay(golem, 0.0F);

        MinecraftClient.getInstance().getItemRenderer().renderItem(OBAMAPRISM, ModelTransformation.Mode.GROUND, lightAbove, overlay, matrices, vertexConsumers);

        matrices.pop();
        super.render(golem, yaw, tickDelta, matrices, vertexConsumers, light);

        Optional<LivingEntity> target = Optional.ofNullable(golem.getBeamTarget());
        if (target.isPresent()) {
            float progress = golem.getBeamProgress(tickDelta);
            float exactTime = (float) golem.world.getTime() + tickDelta;
            float k = exactTime * 0.5F % 1.0F;
            float eyeHeight = golem.getStandingEyeHeight();
            matrices.push();
            matrices.translate(0.0D, eyeHeight, 0.0D);
            Vec3d vec3d = fromLerpedPosition(target.get(), (double) target.get().getHeight() * 0.5D, tickDelta);
            Vec3d vec3d2 = fromLerpedPosition(golem, eyeHeight, tickDelta);
            Vec3d vec3d3 = vec3d.subtract(vec3d2);
            float m = (float) (vec3d3.length() + 1.0D);
            vec3d3 = vec3d3.normalize();
            float n = (float) Math.acos(vec3d3.y);
            float o = (float) Math.atan2(vec3d3.z, vec3d3.x);
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((((float) Math.PI / 2F) - o) * (180F / (float) Math.PI)));
            matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(n * (180F / (float) Math.PI)));
            float q = exactTime * 0.05F * -1.5F;
            float r = progress * progress;
            int s = 64 + (int) (r * 191.0F);
            int t = 32 + (int) (r * 191.0F);
            int u = 128 - (int) (r * 64.0F);
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
            float ah = MathHelper.cos(q + 0.0F) * 0.2F;
            float ai = MathHelper.sin(q + 0.0F) * 0.2F;
            float aj = MathHelper.cos(q + ((float) Math.PI / 2F)) * 0.2F;
            float ak = MathHelper.sin(q + ((float) Math.PI / 2F)) * 0.2F;
            float al = MathHelper.cos(q + ((float) Math.PI * 1.5F)) * 0.2F;
            float am = MathHelper.sin(q + ((float) Math.PI * 1.5F)) * 0.2F;
            float aq = -1.0F + k;
            float ar = m * 2.5F + aq;
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(LAYER);
            MatrixStack.Entry entry = matrices.peek();
            Matrix4f matrix4f = entry.getModel();
            Matrix3f matrix3f = entry.getNormal();
            vertexLaser(vertexConsumer, matrix4f, matrix3f, af, m, ag, s, t, u, 0.4999F, ar);
            vertexLaser(vertexConsumer, matrix4f, matrix3f, af, 0.0F, ag, s, t, u, 0.4999F, aq);
            vertexLaser(vertexConsumer, matrix4f, matrix3f, ah, 0.0F, ai, s, t, u, 0.0F, aq);
            vertexLaser(vertexConsumer, matrix4f, matrix3f, ah, m, ai, s, t, u, 0.0F, ar);
            vertexLaser(vertexConsumer, matrix4f, matrix3f, aj, m, ak, s, t, u, 0.4999F, ar);
            vertexLaser(vertexConsumer, matrix4f, matrix3f, aj, 0.0F, ak, s, t, u, 0.4999F, aq);
            vertexLaser(vertexConsumer, matrix4f, matrix3f, al, 0.0F, am, s, t, u, 0.0F, aq);
            vertexLaser(vertexConsumer, matrix4f, matrix3f, al, m, am, s, t, u, 0.0F, ar);
            float as = 0.0F;
            if (golem.age % 2 == 0) {
                as = 0.5F;
            }

            vertexLaser(vertexConsumer, matrix4f, matrix3f, x, m, y, s, t, u, 0.5F, as + 0.5F);
            vertexLaser(vertexConsumer, matrix4f, matrix3f, z, m, aa, s, t, u, 1.0F, as + 0.5F);
            vertexLaser(vertexConsumer, matrix4f, matrix3f, ad, m, ae, s, t, u, 1.0F, as);
            vertexLaser(vertexConsumer, matrix4f, matrix3f, ab, m, ac, s, t, u, 0.5F, as);
            matrices.pop();
        }
    }
}
