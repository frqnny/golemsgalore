package io.github.frqnny.golemsgalore.init;

import io.github.frqnny.golemsgalore.GolemsGalore;
import io.github.frqnny.golemsgalore.block.GolemBeehiveBlock;
import io.github.frqnny.golemsgalore.block.GolemBeehiveBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.poi.PointOfInterestType;

public class ModBlocks {
    public static final Block OBAMIUM_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).requiresTool().strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL));
    public static final Block GOLEM_BEEHIVE = new GolemBeehiveBlock(FabricBlockSettings.of(Material.WOOD).strength(0.6f).sounds(BlockSoundGroup.WOOD));
    public static final PointOfInterestType BEEHIVE = PointOfInterestType.register("golemsgalore:beehive", PointOfInterestType.getAllStatesOf(GOLEM_BEEHIVE), 0, 1);    public static final BlockEntityType<GolemBeehiveBlockEntity> GOLEM_BEEHIVE_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(GolemBeehiveBlockEntity::new, GOLEM_BEEHIVE).build();
    public static final TagKey<Block> BEEHIVES = TagKey.of(Registry.BLOCK_KEY, GolemsGalore.id("beehives"));

    public static void init() {
        Registry.register(Registry.BLOCK, GolemsGalore.id("obamium_block"), OBAMIUM_BLOCK);
        Registry.register(Registry.BLOCK, GolemsGalore.id("golem_beehive"), GOLEM_BEEHIVE);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, GolemsGalore.id("golem_beehive"), GOLEM_BEEHIVE_BLOCK_ENTITY);
    }


}
