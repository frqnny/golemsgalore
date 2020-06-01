package io.github.franiscoder.golemsgalore;

import io.github.franiscoder.golemsgalore.init.ModEntities;
import io.github.franiscoder.golemsgalore.init.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class GolemsGalore implements ModInitializer {
    public static final String MODID = "golemsgalore";
    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(
            id("item_group"),
            () -> new ItemStack(ModItems.GOLEM_SOUL));

    public static Identifier id(String namespace) {
        return new Identifier(MODID, namespace);
    }

    @Override
    public void onInitialize() {
        ModEntities.init();
        ModItems.init();

    }
}
