package io.github.frqnny.golemsgalore.mixin;

import io.github.frqnny.golemsgalore.api.GolemSpawningUtil;
import io.github.frqnny.golemsgalore.api.enums.Type;
import io.github.frqnny.golemsgalore.entity.*;
import io.github.frqnny.golemsgalore.init.ModEntities;
import io.github.frqnny.golemsgalore.init.ModItems;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CarvedPumpkinBlock.class)
public class MixinCarvedPumpkinBlock {

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem() == ModItems.GOLEM_SOUL) {
            BlockPattern.Result common = GolemSpawningUtil.commonPattern.searchAround(world, pos);
            BlockPattern.Result laser = GolemSpawningUtil.laserPattern.searchAround(world, pos);
            BlockPattern.Result antiCreeper = GolemSpawningUtil.antiCreeperPattern.searchAround(world, pos);
            BlockPattern.Result diamondLaser = GolemSpawningUtil.diamondLaserPattern.searchAround(world, pos);
            BlockPattern.Result obama = GolemSpawningUtil.obamaPattern.searchAround(world, pos);
            if (common != null) {
                if (!player.abilities.creativeMode) {
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
            }
        }
        return ActionResult.PASS;
    }
}