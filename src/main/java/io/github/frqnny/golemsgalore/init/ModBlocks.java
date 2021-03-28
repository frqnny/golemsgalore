package io.github.frqnny.golemsgalore.init;

import io.github.frqnny.golemsgalore.GolemsGalore;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.registry.Registry;

public class ModBlocks {
    public static Block OBAMIUM_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).requiresTool().breakByTool(FabricToolTags.PICKAXES, 2).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL));

    public static void init() {
        Registry.register(Registry.BLOCK, GolemsGalore.id("obamium_block"), OBAMIUM_BLOCK);
    }
}
