package io.github.frqnny.golemsgalore.init;

import io.github.frqnny.golemsgalore.GolemsGalore;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

public class ModItems {
    public static final Item GOLEM_SOUL = new Item(new Item.Settings().rarity(Rarity.UNCOMMON).group(GolemsGalore.ITEM_GROUP).maxCount(1));
    public static final Item OBAMIUM_BLOCK = new BlockItem(ModBlocks.OBAMIUM_BLOCK, new Item.Settings().group(GolemsGalore.ITEM_GROUP));
    public static final Item OBAMIUM_INGOT = new Item(new Item.Settings().rarity(Rarity.COMMON).group(GolemsGalore.ITEM_GROUP).maxCount(64));

    public static void init() {
        Registry.register(Registry.ITEM, GolemsGalore.id("golem_soul"), GOLEM_SOUL);
        Registry.register(Registry.ITEM, GolemsGalore.id("obamium_block"), OBAMIUM_BLOCK);
        Registry.register(Registry.ITEM, GolemsGalore.id("obamium_ingot"), OBAMIUM_INGOT);
    }
}
