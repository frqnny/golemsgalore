package io.github.frqnny.golemsgalore.util;

import io.github.frqnny.golemsgalore.GolemsGalore;
import io.github.frqnny.golemsgalore.entity.*;
import io.github.frqnny.golemsgalore.init.ModBlocks;
import io.github.frqnny.golemsgalore.init.ModEntities;
import io.github.frqnny.golemsgalore.init.ModItems;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.MaterialPredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class GolemSpawningUtil {
    public static final Predicate<BlockState> IS_PUMPKIN_PREDICATE = (blockState) -> blockState != null &&
            (blockState.isOf(Blocks.CARVED_PUMPKIN) || blockState.isOf(Blocks.JACK_O_LANTERN));
    public static final Predicate<BlockState> IS_VALID_BLOCK = (blockState) -> blockState != null && (
            blockState.isOf(Blocks.DIAMOND_BLOCK) ||
                    blockState.isOf(Blocks.NETHERITE_BLOCK) ||
                    blockState.isOf(Blocks.GOLD_BLOCK) ||
                    blockState.isOf(Blocks.QUARTZ_BLOCK) ||
                    blockState.isOf(Blocks.OBSIDIAN) ||
                    blockState.isOf(Blocks.HAY_BLOCK) ||
                    blockState.isOf(Blocks.AMETHYST_BLOCK)
    );
    public static final Predicate<BlockState> IRON_BLOCK_PREDICATE = getPredicateFromBlock(Blocks.IRON_BLOCK);
    public static final Predicate<BlockState> REDSTONE_BLOCK_PREDICATE = getPredicateFromBlock(Blocks.REDSTONE_BLOCK);
    public static final Predicate<BlockState> TNT_PREDICATE = getPredicateFromBlock(Blocks.TNT);
    public static final Predicate<BlockState> DIAMOND_BLOCK_PREDICATE = getPredicateFromBlock(Blocks.DIAMOND_BLOCK);
    public static final Predicate<BlockState> OBAMIUM_BLOCK_PREDICATE = getPredicateFromBlock(ModBlocks.OBAMIUM_BLOCK);
    public static final Predicate<BlockState> HONEY_BLOCK_PREDICATE = getPredicateFromBlock(Blocks.HONEY_BLOCK);
    public static final Predicate<BlockState> COBWEB_PREDICATE = getPredicateFromBlock(Blocks.COBWEB);

    public static final BlockPattern commonPattern = getSingleBlockPattern(IS_VALID_BLOCK);
    public static final BlockPattern laserPattern = getSpecialPattern(IRON_BLOCK_PREDICATE, REDSTONE_BLOCK_PREDICATE);
    public static final BlockPattern antiCreeperPattern = getSpecialPattern(IRON_BLOCK_PREDICATE, TNT_PREDICATE);
    public static final BlockPattern diamondLaserPattern = getSpecialPattern(DIAMOND_BLOCK_PREDICATE, REDSTONE_BLOCK_PREDICATE);
    public static final BlockPattern obamaPattern = getSingleBlockPattern(OBAMIUM_BLOCK_PREDICATE);
    public static final BlockPattern ghastlyPattern = getSingleBlockPattern(IS_PUMPKIN_PREDICATE);
    public static final BlockPattern beePattern = getSingleBlockPattern(HONEY_BLOCK_PREDICATE);
    public static final BlockPattern spiderPattern = getSingleBlockPattern(COBWEB_PREDICATE);


    public static Predicate<BlockState> getPredicateFromBlock(Block block) {
        return (blockState) -> blockState != null && blockState.isOf(block);
    }

    public static BlockPattern getSingleBlockPattern(Predicate<BlockState> pattern) {
        return BlockPatternBuilder.start().aisle("~^~", "###", "~#~")
                .where('^', CachedBlockPosition.matchesBlockState(IS_PUMPKIN_PREDICATE))
                .where('#', CachedBlockPosition.matchesBlockState(pattern))
                .where('~', CachedBlockPosition.matchesBlockState(MaterialPredicate.create(Material.AIR)))
                .build();
    }

    public static BlockPattern getSpecialPattern(Predicate<BlockState> block, Predicate<BlockState> center) {
        return BlockPatternBuilder.start().aisle("~^~", "#%#", "~#~")
                .where('^', CachedBlockPosition.matchesBlockState(IS_PUMPKIN_PREDICATE))
                .where('#', CachedBlockPosition.matchesBlockState(block))
                .where('%', CachedBlockPosition.matchesBlockState(center))
                .where('~', CachedBlockPosition.matchesBlockState(MaterialPredicate.create(Material.AIR)))
                .build();
    }

    public static <T extends ModGolemEntity> void spawnGolem(World world, PlayerEntity player, Hand hand, BlockPattern.Result resultPattern, T golem) {
        if (!player.isCreative()) {
            player.getStackInHand(hand).decrement(1);
        }

        int width = commonPattern.getWidth();
        int height = commonPattern.getHeight();

        for (int k = 0; k < width; ++k) {
            for (int l = 0; l < height; ++l) {
                CachedBlockPosition cachedBlockPosition3 = resultPattern.translate(k, l, 0);
                world.setBlockState(cachedBlockPosition3.getBlockPos(), Blocks.AIR.getDefaultState(), 2);
                world.syncWorldEvent(2001, cachedBlockPosition3.getBlockPos(), Block.getRawIdFromState(cachedBlockPosition3.getBlockState()));
            }
        }

        if (golem != null) {
            golem.setPlayerCreated(true);

            BlockPos blockPos2 = resultPattern.translate(1, 2, 0).getBlockPos();
            golem.refreshPositionAndAngles((double) blockPos2.getX() + 0.5D, (double) blockPos2.getY() + 0.05D, (double) blockPos2.getZ() + 0.5D, 0.0F, 0.0F);
            world.spawnEntity(golem);

            for (ServerPlayerEntity serverPlayer : world.getNonSpectatingEntities(ServerPlayerEntity.class, golem.getBoundingBox().expand(5.0D))) {
                Criteria.SUMMONED_ENTITY.trigger(serverPlayer, golem);
            }

            for (int m = 0; m < width; ++m) {
                for (int n = 0; n < height; ++n) {
                    CachedBlockPosition cachedBlockPosition4 = resultPattern.translate(m, n, 0);
                    world.updateNeighbors(cachedBlockPosition4.getBlockPos(), Blocks.AIR);
                }
            }
        }
    }


    public static ActionResult golemHook(World world, BlockPos pos, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem() == ModItems.GOLEM_SOUL) {
            BlockPattern.Result common = commonPattern.searchAround(world, pos);
            BlockPattern.Result laser = laserPattern.searchAround(world, pos);
            BlockPattern.Result antiCreeper = antiCreeperPattern.searchAround(world, pos);
            BlockPattern.Result diamondLaser = diamondLaserPattern.searchAround(world, pos);
            BlockPattern.Result obama = obamaPattern.searchAround(world, pos);
            BlockPattern.Result ghastly = ghastlyPattern.searchAround(world, pos);
            BlockPattern.Result bee = beePattern.searchAround(world, pos);
            BlockPattern.Result spider = spiderPattern.searchAround(world, pos);

            if (common != null) {
                if (!player.isCreative()) {
                    player.getStackInHand(hand).decrement(1);
                }

                Block block = null;
                int width = GolemSpawningUtil.commonPattern.getWidth();
                int height = GolemSpawningUtil.commonPattern.getHeight();
                for (int k = 0; k < width; ++k) {
                    for (int l = 0; l < height; ++l) {
                        CachedBlockPosition cachedBlockPosition3 = common.translate(k, l, 0);
                        if (GolemSpawningUtil.IS_VALID_BLOCK.test(cachedBlockPosition3.getBlockState())) {
                            block = cachedBlockPosition3.getBlockState().getBlock();
                        }
                        world.setBlockState(cachedBlockPosition3.getBlockPos(), Blocks.AIR.getDefaultState(), 2);
                        world.syncWorldEvent(2001, cachedBlockPosition3.getBlockPos(), Block.getRawIdFromState(cachedBlockPosition3.getBlockState()));
                    }
                }

                BlockPos blockPos2 = common.translate(1, 2, 0).getBlockPos();
                if (block != null) {
                    Type type = Type.fromBlock(block);

                    ModGolemEntity golem = ModEntities.typeMap.get(type).create(world);
                    if (golem != null) {
                        golem.setGolemType(type);
                        golem.setPlayerCreated(true);
                        golem.refreshPositionAndAngles((double) blockPos2.getX() + 0.5D, (double) blockPos2.getY() + 0.05D, (double) blockPos2.getZ() + 0.5D, 0.0F, 0.0F);
                        world.spawnEntity(golem);

                        for (ServerPlayerEntity serverPlayer : world.getNonSpectatingEntities(ServerPlayerEntity.class, golem.getBoundingBox().expand(5.0D))) {
                            Criteria.SUMMONED_ENTITY.trigger(serverPlayer, golem);
                        }

                        for (int m = 0; m < width; ++m) {
                            for (int n = 0; n < height; ++n) {
                                CachedBlockPosition cachedBlockPosition4 = common.translate(m, n, 0);
                                world.updateNeighbors(cachedBlockPosition4.getBlockPos(), Blocks.AIR);
                            }
                        }
                    }

                }
                return ActionResult.CONSUME;

            } else if (laser != null) {
                LaserGolemEntity golem = ModEntities.LASER_GOLEM.create(world);
                GolemSpawningUtil.spawnGolem(world, player, hand, laser, golem);

                return ActionResult.CONSUME;

            } else if (antiCreeper != null) {
                AntiCreeperGolemEntity golem = ModEntities.ANTI_CREEPER_GOLEM.create(world);
                GolemSpawningUtil.spawnGolem(world, player, hand, antiCreeper, golem);

                return ActionResult.CONSUME;

            } else if (diamondLaser != null) {
                DiamondLaserGolemEntity golem = ModEntities.DIAMOND_LASER_GOLEM.create(world);
                GolemSpawningUtil.spawnGolem(world, player, hand, diamondLaser, golem);

                return ActionResult.CONSUME;

            } else if (obama != null) {
                ObamaPyramidGolemEntity golem = ModEntities.OBAMA_PRISM_GOLEM.create(world);
                GolemSpawningUtil.spawnGolem(world, player, hand, obama, golem);

                return ActionResult.CONSUME;
            } else if (ghastly != null) {
                GhastlyGolemEntity golem = ModEntities.GHASTLY_GOLEM.create(world);
                GolemSpawningUtil.spawnGolem(world, player, hand, ghastly, golem);

                if (world.isClient) {
                    player.sendMessage(Text.translatable("message.golemsgalore.ghastly"), false);
                }
                return ActionResult.CONSUME;
            } else if (bee != null) {
                for (int i = 0; i < GolemsGalore.getConfig().beeSpawnAmount; i++) {
                    BeeGolemEntity golem = ModEntities.BEE_GOLEM.create(world);
                    GolemSpawningUtil.spawnGolem(world, player, hand, bee, golem);
                }
            } else if (spider != null) {
                SpiderGolemEntity golem = ModEntities.SPIDER_GOLEM.create(world);
                GolemSpawningUtil.spawnGolem(world, player, hand, spider, golem);
            }
        }
        return ActionResult.PASS;
    }
}
