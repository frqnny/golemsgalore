package io.github.franiscoder.golemsgalore;

import draylar.structurized.api.StructurePoolAddCallback;
import io.github.franiscoder.golemsgalore.config.GolemsGaloreConfig;
import io.github.franiscoder.golemsgalore.init.ModEntities;
import io.github.franiscoder.golemsgalore.init.ModItems;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.structure.pool.SinglePoolElement;
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
        config = AutoConfig.getConfigHolder(GolemsGaloreConfig.class).getConfig();
        return config;
    }

    @Override
    public void onInitialize() {
        AutoConfig.register(GolemsGaloreConfig.class, JanksonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(GolemsGaloreConfig.class).getConfig();

        ModEntities.init();
        ModItems.init();

        StructurePoolAddCallback.EVENT.register(structurePool -> {
            if (structurePool.getUnderlying().getId().toString().equals("minecraft:village/common/iron_golem")) {
                structurePool.addStructurePoolElement(new SinglePoolElement("golemsgalore:obsidian_golem"), 1);
                structurePool.addStructurePoolElement(new SinglePoolElement("golemsgalore:quartz_golem"), 1);
            }
        });

    }

}
