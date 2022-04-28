package io.github.frqnny.golemsgalore;

import draylar.omegaconfig.OmegaConfig;
import draylar.structurized.api.EndDynamicRegistrySetupCallback;
import draylar.structurized.api.FabricStructurePool;
import io.github.frqnny.golemsgalore.config.GolemsGaloreConfig;
import io.github.frqnny.golemsgalore.init.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.TradeOffers;

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

        EndDynamicRegistrySetupCallback.EVENT.register(dynamicRegistries -> {
            Registry<StructurePool> pools = dynamicRegistries.get(Registry.STRUCTURE_POOL_KEY);
            Registry<StructureProcessorList> lists = dynamicRegistries.get(Registry.STRUCTURE_PROCESSOR_LIST_KEY);

            for (StructurePool structurePool : pools) {
                FabricStructurePool pool = new FabricStructurePool(structurePool);
                RegistryEntry<StructureProcessorList> list = lists.getEntry(RegistryKey.of(Registry.STRUCTURE_PROCESSOR_LIST_KEY, new Identifier("empty"))).get();
                if (pool.getUnderlyingPool().getId().toString().contains("village/common/iron_golem")) {
                    pool.addStructurePoolElement(StructurePoolElement.ofProcessedLegacySingle("golemsgalore:variant_1", list).apply(StructurePool.Projection.RIGID), 6);
                    pool.addStructurePoolElement(StructurePoolElement.ofProcessedLegacySingle("golemsgalore:variant_2", list).apply(StructurePool.Projection.RIGID), 4);

                }
            }
        });

        TradeOfferHelper.registerWanderingTraderOffers(1, factories -> factories.add(((entity, random) -> new TradeOffers.SellItemFactory(ModItems.GOLEM_SOUL, 1, 1, 5, 2).create(entity, random))));

    }

}
