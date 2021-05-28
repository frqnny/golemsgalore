package io.github.frqnny.golemsgalore;

import draylar.omegaconfig.OmegaConfig;
import draylar.structurized.api.StructurePoolAddCallback;
import io.github.frqnny.golemsgalore.config.GolemsGaloreConfig;
import io.github.frqnny.golemsgalore.init.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.Identifier;

public class GolemsGalore implements ModInitializer {
    public static final String MODID = "golemsgalore";
    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(
            id("item_group"),
            () -> new ItemStack(ModItems.GOLEM_SOUL));
    private static final GolemsGaloreConfig config = OmegaConfig.register(GolemsGaloreConfig.class);

    public static Identifier id(String namespace) {
        return new Identifier(MODID, namespace);
    }

    public static GolemsGaloreConfig getConfig() {
        return config;
    }

    @Override
    public void onInitialize() {

        ModEntities.init();
        ModItems.init();
        ModBlocks.init();
        ModParticles.register();
        ModSounds.init();
        ModPackets.init();
        StructurePoolAddCallback.EVENT.register(structurePool -> {
            if (structurePool.getStructurePool().getId().toString().contains("village/common/iron_golem")) {
                structurePool.addStructurePoolElement(StructurePoolElement.method_30425("golemsgalore:variation_1").apply(StructurePool.Projection.RIGID), 4);
                structurePool.addStructurePoolElement(StructurePoolElement.method_30425("golemsgalore:variation_2").apply(StructurePool.Projection.RIGID), 6);

            }
        });

    }

}
