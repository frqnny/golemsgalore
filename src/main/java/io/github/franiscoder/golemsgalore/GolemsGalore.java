package io.github.franiscoder.golemsgalore;

import draylar.structurized.api.StructurePoolAddCallback;
import io.github.franiscoder.golemsgalore.config.GolemsGaloreConfig;
import io.github.franiscoder.golemsgalore.init.ModEntities;
import io.github.franiscoder.golemsgalore.init.ModItems;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.Identifier;

public class GolemsGalore implements ModInitializer {
    public static final String MODID = "golemsgalore";
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
            if (structurePool.getUnderlying().getId().getPath().equals("village/common/iron_golem")) {
                System.out.println("Called. ");
                structurePool.addStructurePoolElement(StructurePoolElement.method_30425("golemsgalore:obsidian_golem").apply(StructurePool.Projection.RIGID), 2);
                structurePool.addStructurePoolElement(StructurePoolElement.method_30425("golemsgalore:quartz_golem").apply(StructurePool.Projection.RIGID), 2);
            }
        });

    }

}
