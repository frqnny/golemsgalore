package io.github.franiscoder.golemsgalore.init;

import io.github.franiscoder.golemsgalore.GolemsGalore;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

public class ModItems {
    public static final Item GOLEM_SOUL = new Item(new Item.Settings().rarity(Rarity.UNCOMMON).group(GolemsGalore.ITEM_GROUP).maxCount(1));

    public static void init() {
        Registry.register(Registry.ITEM, GolemsGalore.id("golem_soul"), GOLEM_SOUL);
    }
}
