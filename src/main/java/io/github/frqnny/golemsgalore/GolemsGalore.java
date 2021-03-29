package io.github.frqnny.golemsgalore;

import draylar.omegaconfig.OmegaConfig;
import draylar.structurized.api.StructurePoolAddCallback;
import io.github.frqnny.golemsgalore.config.GolemsGaloreConfig;
import io.github.frqnny.golemsgalore.init.ModBlocks;
import io.github.frqnny.golemsgalore.init.ModEntities;
import io.github.frqnny.golemsgalore.init.ModItems;
import io.github.frqnny.golemsgalore.init.ModParticles;
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
    private static GolemsGaloreConfig config;

    public static Identifier id(String namespace) {
        return new Identifier(MODID, namespace);
    }

    public static GolemsGaloreConfig getConfig() {
        return config;
    }

    @Override
    public void onInitialize() {
        config = OmegaConfig.register(GolemsGaloreConfig.class);

        ModEntities.init();
        ModItems.init();
        ModBlocks.init();
        ModParticles.register();


        StructurePoolAddCallback.EVENT.register(structurePool -> {
            if (structurePool.getStructurePool().getId().toString().equals("minecraft:village/common/iron_golem")) {
                structurePool.addStructurePoolElement(StructurePoolElement.method_30425("golemsgalore:obsidian_golem").apply(StructurePool.Projection.RIGID), 2);
                structurePool.addStructurePoolElement(StructurePoolElement.method_30425("golemsgalore:quartz_golem").apply(StructurePool.Projection.RIGID), 2);
            }
        });

    }

}
