package io.github.frqnny.golemsgalore.client.render.projectile;

import io.github.frqnny.golemsgalore.client.render.ObamaPyramidGolemEntityRenderer;
import io.github.frqnny.golemsgalore.entity.projectile.PumpkinProjectileEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import java.util.Objects;

public class GhastlyPumpkinProjectileEntityRenderer extends EntityRenderer<PumpkinProjectileEntity> {
    public static final ItemStack JACK_O_LANTERN = new ItemStack(Items.JACK_O_LANTERN);
    public static final ItemStack CARVED_PUMPKIN = new ItemStack(Items.CARVED_PUMPKIN);

    public GhastlyPumpkinProjectileEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(PumpkinProjectileEntity entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }

    @Override
    public void render(PumpkinProjectileEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        if (entity.hasTarget()) {
            Vec3d targetVec = ObamaPyramidGolemEntityRenderer.fromLerpedPosition(entity.getTarget(), (double) entity.getTarget().getHeight() * 0.5D, tickDelta);
            Vec3d originVec = ObamaPyramidGolemEntityRenderer.fromLerpedPosition(entity, entity.getY(), tickDelta);
            Vec3d vec3d3 = targetVec.subtract(originVec);
            vec3d3 = vec3d3.normalize();
            float o = (float) Math.atan2(vec3d3.z, vec3d3.x);
            float angle = (((float) Math.PI / 2F) - o) * (180F / (float) Math.PI) + 90;
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(angle));
        }

        matrices.scale(3, 3, 3);

        int actualLight = WorldRenderer.getLightmapCoordinates(Objects.requireNonNull(entity.getWorld()), entity.getBlockPos());
        MinecraftClient.getInstance().getItemRenderer().renderItem(entity.isSpawning() ? CARVED_PUMPKIN : JACK_O_LANTERN, ModelTransformation.Mode.GROUND, actualLight, 0, matrices, vertexConsumers, 0);
        matrices.pop();
    }
}
