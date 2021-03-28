package io.github.frqnny.golemsgalore;

import draylar.omegaconfig.OmegaConfig;
import draylar.structurized.api.StructurePoolAddCallback;
import io.github.frqnny.golemsgalore.config.GolemsGaloreConfig;
import io.github.frqnny.golemsgalore.init.ModBlocks;
import io.github.frqnny.golemsgalore.init.ModEntities;
import io.github.frqnny.golemsgalore.init.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;

public class GolemsGalore implements ModInitializer {
    public static final String MODID = "golemsgalore";
    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(
            id("item_group"),
            () -> new ItemStack(ModItems.GOLEM_SOUL));
    public static BlockState OBAMIUM_STATE = ModBlocks.OBAMIUM_ORE.getDefaultState();
    public static ConfiguredFeature<?, ?> OBAMIUM_ORE_FEATURE = Feature.ORE.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, OBAMIUM_STATE, 8)).rangeOf(16).spreadHorizontally().decorate(Decorator.RANGE.configure(new RangeDecoratorConfig(0, 0, 25)));
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
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, id("obamium_ore_feature"), OBAMIUM_ORE_FEATURE);

        BiomeModifications.create(id("obamium_ore_feature")).add(
                ModificationPhase.ADDITIONS,
                BiomeSelectors.foundInOverworld().and((context) -> config.generateObamiumOre),
                (context) -> context.getGenerationSettings().addBuiltInFeature(GenerationStep.Feature.UNDERGROUND_ORES, OBAMIUM_ORE_FEATURE)
        );

        StructurePoolAddCallback.EVENT.register(structurePool -> {
            if (structurePool.getStructurePool().getId().toString().equals("minecraft:village/common/iron_golem")) {
                structurePool.addStructurePoolElement(StructurePoolElement.method_30425("golemsgalore:obsidian_golem").apply(StructurePool.Projection.RIGID), 2);
                structurePool.addStructurePoolElement(StructurePoolElement.method_30425("golemsgalore:quartz_golem").apply(StructurePool.Projection.RIGID), 2);
            }
        });

    }

}
