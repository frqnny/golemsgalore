package io.github.franiscoder.golemsgalore.api.enums;

import io.github.franiscoder.golemsgalore.GolemsGalore;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public enum Type {
    DIAMOND(Blocks.DIAMOND_BLOCK, Items.DIAMOND, GolemsGalore.id("textures/entity/golem/diamond_golem.png")),
    GOLD(Blocks.GOLD_BLOCK, Items.GOLD_INGOT, GolemsGalore.id("textures/entity/golem/golden_golem.png")),
    NETHERITE(Blocks.NETHERITE_BLOCK, Items.NETHERITE_INGOT, GolemsGalore.id("textures/entity/golem/netherite_golem.png")),
    QUARTZ(Blocks.QUARTZ_BLOCK, Items.QUARTZ, GolemsGalore.id("textures/entity/golem/quartz_golem.png")),
    OBSIDIAN(Blocks.OBSIDIAN, Items.OBSIDIAN, GolemsGalore.id("textures/entity/golem/obsidian_golem.png")),
    NULL(Blocks.DIRT, Items.DIRT, null);

    public final Block block;
    public final Item item;
    public final int rawId;
    public final Identifier texture;

    Type(Block material, Item item, Identifier texture) {
        this.item = item;
        this.block = material;
        this.texture = texture;
        this.rawId = Registry.ITEM.getRawId(item);

    }

    public static Type fromId(int id) {
        for (Type type : Type.values()) {
            if (type.rawId == id) {
                return type;
            }
        }
        return NULL;
    }

    public static Type fromBlock(Block block) {
        Item item = block.asItem();
        for (Type type : Type.values()) {
            if (type.block.asItem().equals(item)) {
                return type;
            }
        }
        return NULL;
    }

    //this is a fucking nightmare
    public static Type fromItemStack(ItemStack itemStack) {
        Item item = itemStack.getItem();
        for (Type type : Type.values()) {
            if (type.item.equals(item)) {
                return type;
            }
        }
        return NULL;
    }
}
