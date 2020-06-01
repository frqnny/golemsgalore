package io.github.franiscoder.golemsgalore.mixin;

import io.github.franiscoder.golemsgalore.api.enums.Type;
import io.github.franiscoder.golemsgalore.entity.LaserGolemEntity;
import io.github.franiscoder.golemsgalore.entity.ModGolemEntity;
import io.github.franiscoder.golemsgalore.init.ModEntities;
import io.github.franiscoder.golemsgalore.init.ModItems;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.MaterialPredicate;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Predicate;

@SuppressWarnings("unused")
@Mixin(CarvedPumpkinBlock.class)
public class MixinCarvedPumkinBlock extends Block {
    private static final Predicate<BlockState> IS_VALID_BLOCK;
    private static final Predicate<BlockState> IS_PUMPKIN_PREDICATE;
    private static final Predicate<BlockState> IRON_FUCK;
    private static final Predicate<BlockState> REDSTONE_FUCK;
    private static final BlockPattern commonPattern;
    private static final BlockPattern laserPattern;

    static {
        IS_PUMPKIN_PREDICATE = (blockState) -> blockState != null &&
                (blockState.isOf(Blocks.CARVED_PUMPKIN) || blockState.isOf(Blocks.JACK_O_LANTERN));
        IS_VALID_BLOCK = (blockState) -> blockState != null &&
                //ugh this is not good
                (
                        blockState.isOf(Blocks.DIAMOND_BLOCK)
                                || blockState.isOf(Blocks.NETHERITE_BLOCK)
                                || blockState.isOf(Blocks.GOLD_BLOCK)
                                || blockState.isOf(Blocks.QUARTZ_BLOCK)
                                || blockState.isOf(Blocks.OBSIDIAN)
                );
        IRON_FUCK = (blockState) -> blockState != null && blockState.isOf(Blocks.IRON_BLOCK);

        REDSTONE_FUCK = (blockState) -> blockState != null && blockState.isOf(Blocks.REDSTONE_BLOCK);

        commonPattern = BlockPatternBuilder.start().aisle("~^~", "###", "~#~")
                .where('^', CachedBlockPosition.matchesBlockState(IS_PUMPKIN_PREDICATE))
                .where('#', CachedBlockPosition.matchesBlockState(IS_VALID_BLOCK))
                .where('~', CachedBlockPosition.matchesBlockState(MaterialPredicate.create(Material.AIR)))
                .build();
        laserPattern = BlockPatternBuilder.start().aisle("~^~", "#%#", "~#~")
                .where('^', CachedBlockPosition.matchesBlockState(IS_PUMPKIN_PREDICATE))
                .where('#', CachedBlockPosition.matchesBlockState(IRON_FUCK))
                .where('%', CachedBlockPosition.matchesBlockState(REDSTONE_FUCK))
                .where('~', CachedBlockPosition.matchesBlockState(MaterialPredicate.create(Material.AIR)))
                .build();

    }

    public MixinCarvedPumkinBlock(Settings settings) {
        super(settings);
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem() == ModItems.GOLEM_SOUL) {
            BlockPattern.Result result = commonPattern.searchAround(world, pos);
            BlockPattern.Result laser = laserPattern.searchAround(world, pos);
            if (result != null) {
                if (!player.abilities.creativeMode) {
                    player.getStackInHand(hand).decrement(1);
                }

                Block block = null;
                int width = commonPattern.getWidth();
                int height = commonPattern.getHeight();
                for (int k = 0; k < width; ++k) {
                    for (int l = 0; l < height; ++l) {
                        CachedBlockPosition cachedBlockPosition3 = result.translate(k, l, 0);
                        if (IS_VALID_BLOCK.test(cachedBlockPosition3.getBlockState())) {
                            block = cachedBlockPosition3.getBlockState().getBlock();
                        }
                        world.setBlockState(cachedBlockPosition3.getBlockPos(), Blocks.AIR.getDefaultState(), 2);
                        world.syncWorldEvent(2001, cachedBlockPosition3.getBlockPos(), Block.getRawIdFromState(cachedBlockPosition3.getBlockState()));
                    }
                }

                BlockPos blockPos2 = result.translate(1, 2, 0).getBlockPos();

                assert block != null;
                Type type = Type.fromBlock(block);

                ModGolemEntity golem = ModEntities.typeMap.get(type).create(world);
                assert golem != null;
                golem.setGolemType(type);
                golem.setPlayerCreated(true);
                golem.refreshPositionAndAngles((double) blockPos2.getX() + 0.5D, (double) blockPos2.getY() + 0.05D, (double) blockPos2.getZ() + 0.5D, 0.0F, 0.0F);
                world.spawnEntity(golem);

                for (ServerPlayerEntity serverPlayer : world.getNonSpectatingEntities(ServerPlayerEntity.class, golem.getBoundingBox().expand(5.0D))) {
                    Criteria.SUMMONED_ENTITY.trigger(serverPlayer, golem);
                }

                for (int m = 0; m < width; ++m) {
                    for (int n = 0; n < height; ++n) {
                        CachedBlockPosition cachedBlockPosition4 = result.translate(m, n, 0);
                        world.updateNeighbors(cachedBlockPosition4.getBlockPos(), Blocks.AIR);
                    }
                }
            } else if (laser != null) {
                if (!player.abilities.creativeMode) {
                    player.getStackInHand(hand).decrement(1);
                }

                int width = commonPattern.getWidth();
                int height = commonPattern.getHeight();

                for (int k = 0; k < width; ++k) {
                    for (int l = 0; l < height; ++l) {
                        CachedBlockPosition cachedBlockPosition3 = laser.translate(k, l, 0);
                        world.setBlockState(cachedBlockPosition3.getBlockPos(), Blocks.AIR.getDefaultState(), 2);
                        world.syncWorldEvent(2001, cachedBlockPosition3.getBlockPos(), Block.getRawIdFromState(cachedBlockPosition3.getBlockState()));
                    }
                }

                LaserGolemEntity golem = ModEntities.LASER_GOLEM.create(world);
                assert golem != null;
                golem.setPlayerCreated(true);

                BlockPos blockPos2 = laser.translate(1, 2, 0).getBlockPos();
                golem.refreshPositionAndAngles((double) blockPos2.getX() + 0.5D, (double) blockPos2.getY() + 0.05D, (double) blockPos2.getZ() + 0.5D, 0.0F, 0.0F);
                world.spawnEntity(golem);

                for (ServerPlayerEntity serverPlayer : world.getNonSpectatingEntities(ServerPlayerEntity.class, golem.getBoundingBox().expand(5.0D))) {
                    Criteria.SUMMONED_ENTITY.trigger(serverPlayer, golem);
                }


                for (int m = 0; m < width; ++m) {
                    for (int n = 0; n < height; ++n) {
                        CachedBlockPosition cachedBlockPosition4 = laser.translate(m, n, 0);
                        world.updateNeighbors(cachedBlockPosition4.getBlockPos(), Blocks.AIR);
                    }
                }
            }

            return ActionResult.CONSUME;
        }
        return ActionResult.PASS;
    }
}