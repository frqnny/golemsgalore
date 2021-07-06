package io.github.frqnny.golemsgalore.api;

import com.google.common.collect.Sets;
import io.github.frqnny.golemsgalore.GolemsGalore;
import io.github.frqnny.golemsgalore.init.ModEntities;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.Set;

public final record Type(Block material, Item item, Identifier texture) {
    public static final Type DIAMOND = new Type(Blocks.DIAMOND_BLOCK, Items.DIAMOND, GolemsGalore.id("textures/entity/golem/diamond_golem.png"));
    public static final Type GOLD = new Type(Blocks.GOLD_BLOCK, Items.GOLD_INGOT, GolemsGalore.id("textures/entity/golem/golden_golem.png"));
    public static final Type NETHERITE = new Type(Blocks.NETHERITE_BLOCK, Items.NETHERITE_INGOT, GolemsGalore.id("textures/entity/golem/netherite_golem.png"));
    public static final Type QUARTZ = new Type(Blocks.QUARTZ_BLOCK, Items.QUARTZ, GolemsGalore.id("textures/entity/golem/quartz_golem.png"));
    public static final Type OBSIDIAN = new Type(Blocks.OBSIDIAN, Items.OBSIDIAN, GolemsGalore.id("textures/entity/golem/obsidian_golem.png"));
    public static final Type HAY = new Type(Blocks.HAY_BLOCK, Items.WHEAT, GolemsGalore.id("textures/entity/golem/hay_golem.png"));
    public static final Type AMETHYST = new Type(Blocks.AMETHYST_BLOCK, Items.AMETHYST_SHARD, GolemsGalore.id("textures/entity/golem/amethyst_golem.png"));
    public static final Type NULL = new Type(Blocks.DIRT, Items.DIRT, GolemsGalore.id("textures/entity/golem/quartz_golem.png"));

    public static final Set<Type> values = Sets.newHashSet(DIAMOND, GOLD, NETHERITE, QUARTZ, OBSIDIAN, HAY, AMETHYST);

    public static Type fromBlock(Block block) {
        for (Type type : Type.values) {
            if (type.material.equals(block)) {
                return type;
            }
        }
        return NULL;
    }

    public static Type getTypeForEntityType(EntityType<?> entityType) {
        for (Type type : values) {
            if (ModEntities.typeMap.get(type).equals(entityType)) {
                return type;
            }
        }

        return NULL;
    }
}
