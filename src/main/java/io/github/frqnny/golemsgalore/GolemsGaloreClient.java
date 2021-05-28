package io.github.frqnny.golemsgalore;

import io.github.frqnny.golemsgalore.client.render.*;
import io.github.frqnny.golemsgalore.client.render.model.GhastlyGolemEntityModel;
import io.github.frqnny.golemsgalore.client.render.model.ModGolemEntityModel;
import io.github.frqnny.golemsgalore.client.render.projectile.GhastlyPumpkinProjectileEntityRenderer;
import io.github.frqnny.golemsgalore.entity.ModGolemEntity;
import io.github.frqnny.golemsgalore.init.ModEntities;
import io.github.frqnny.golemsgalore.init.ModPackets;
import io.github.frqnny.golemsgalore.init.ModParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.EntityType;

public class GolemsGaloreClient implements ClientModInitializer {
    public static final EntityModelLayer MOD_GOLEM = new EntityModelLayer(GolemsGalore.id("mod_golem_render_layer"), "mod_golem_render_layer");
    public static final EntityModelLayer GHASTLY_GOLEM = new EntityModelLayer(GolemsGalore.id("ghastly_golem_render_layer"), "ghastly_golem_render_layer");

    private static void register(EntityType<ModGolemEntity> golem) {
        EntityRendererRegistry.INSTANCE.register(golem, ModGolemEntityRenderer::new);
    }

    @Override
    public void onInitializeClient() {
        register(ModEntities.DIAMOND_GOLEM);
        register(ModEntities.NETHERITE_GOLEM);
        register(ModEntities.GOLDEN_GOLEM);
        register(ModEntities.QUARTZ_GOLEM);
        register(ModEntities.OBSIDIAN_GOLEM);
        register(ModEntities.HAY_GOLEM);

        EntityRendererRegistry.INSTANCE.register(ModEntities.AMETHYST_GOLEM, AmethystGolemEntityRenderer::new);
        EntityRendererRegistry.INSTANCE.register(ModEntities.LASER_GOLEM, LaserGolemEntityRenderer::new);
        EntityRendererRegistry.INSTANCE.register(ModEntities.ANTI_CREEPER_GOLEM, AntiCreeperGolemEntityRenderer::new);
        EntityRendererRegistry.INSTANCE.register(ModEntities.DIAMOND_LASER_GOLEM, DiamondLaserGolemEntityRenderer::new);
        EntityRendererRegistry.INSTANCE.register(ModEntities.OBAMA_PRISM_GOLEM, ObamaPyramidGolemEntityRenderer::new);
        EntityRendererRegistry.INSTANCE.register(ModEntities.GHASTLY_GOLEM, GhastlyGolemEntityRenderer::new);
        EntityRendererRegistry.INSTANCE.register(ModEntities.PUMPKIN_PROJECTILE, GhastlyPumpkinProjectileEntityRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(MOD_GOLEM, ModGolemEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(GHASTLY_GOLEM, GhastlyGolemEntityModel::getTexturedModelData);

        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE).register(((atlasTexture, registry) -> {
            registry.register(GolemsGalore.id("particle/laser_particle"));
        }));

        ParticleFactoryRegistry.getInstance().register(ModParticles.LASER, FlameParticle.Factory::new);

        ModPackets.clientInit();
        //if (FabricLoader.getInstance().isModLoaded("frex")) {
        //    Renderer.get().registerMaterial(GolemsGalore.id("laser_material"), Renderer.get().)
        //}
    }
}
