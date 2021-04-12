package io.github.frqnny.golemsgalore.client.render.projectile;

import io.github.frqnny.golemsgalore.client.render.ObamaPyramidGolemEntityRenderer;
import io.github.frqnny.golemsgalore.entity.projectile.PumpkinProjectileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class GhastlyPumpkinProjectileEntityRenderer extends EntityRenderer<PumpkinProjectileEntity> {
    public GhastlyPumpkinProjectileEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Override
    public Identifier getTexture(PumpkinProjectileEntity entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }

    @Override
    public void render(PumpkinProjectileEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {

        BlockState state;
        if (entity.isSpawning()) {
            state = Blocks.CARVED_PUMPKIN.getDefaultState();
        } else {
            state = Blocks.JACK_O_LANTERN.getDefaultState();
        }

        matrices.push();

        if (entity.hasTarget()) {
            Vec3d targetVec = ObamaPyramidGolemEntityRenderer.fromLerpedPosition(entity.getTarget(), (double) entity.getTarget().getHeight() * 0.5D, tickDelta);
            Vec3d originVec = ObamaPyramidGolemEntityRenderer.fromLerpedPosition(entity, entity.getY(), tickDelta);
            Vec3d vec3d3 = targetVec.subtract(originVec);
            vec3d3 = vec3d3.normalize();
            float o = (float) Math.atan2(vec3d3.z, vec3d3.x);
            float angle = (((float) Math.PI / 2F) - o) * (180F / (float) Math.PI) + 90;
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(angle));
        }

        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(state, matrices, vertexConsumers, 15, OverlayTexture.DEFAULT_UV);
        matrices.pop();
    }
}
