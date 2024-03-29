package io.github.frqnny.golemsgalore.block;

import io.github.frqnny.golemsgalore.entity.BeeGolemEntity;
import io.github.frqnny.golemsgalore.init.ModBlocks;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.*;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GolemBeehiveBlock extends BlockWithEntity {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final IntProperty HONEY_LEVEL = Properties.HONEY_LEVEL;
    public static final int FULL_HONEY_LEVEL = 5;
    private static final int DROPPED_HONEYCOMB_COUNT = 3;

    public GolemBeehiveBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(HONEY_LEVEL, 0).with(FACING, Direction.NORTH));
    }

    public static void dropHoneycomb(World world, BlockPos pos) {
        dropStack(world, pos, new ItemStack(Items.HONEYCOMB, DROPPED_HONEYCOMB_COUNT));
    }

    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return state.get(HONEY_LEVEL);
    }

    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        super.afterBreak(world, player, pos, state, blockEntity, stack);
        if (!world.isClient && blockEntity instanceof GolemBeehiveBlockEntity beehiveBlockEntity) {
            if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) == 0) {
                beehiveBlockEntity.angerBees(player, state, GolemBeehiveBlockEntity.BeeState.EMERGENCY);
                world.updateComparators(pos, this);
                this.angerNearbyBees(world, pos);
            }

            Criteria.BEE_NEST_DESTROYED.trigger((ServerPlayerEntity) player, state, stack, beehiveBlockEntity.getBeeCount());
        }

    }

    private void angerNearbyBees(World world, BlockPos pos) {
        List<BeeGolemEntity> list = world.getNonSpectatingEntities(BeeGolemEntity.class, (new Box(pos)).expand(8.0, 6.0, 8.0));
        if (!list.isEmpty()) {
            List<PlayerEntity> list2 = world.getNonSpectatingEntities(PlayerEntity.class, (new Box(pos)).expand(8.0, 6.0, 8.0));
            int i = list2.size();

            for (BeeGolemEntity golem : list) {
                if (golem.getTarget() == null) {
                    golem.setTarget(list2.get(world.random.nextInt(i)));
                }
            }
        }

    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        int i = state.get(HONEY_LEVEL);
        boolean couldAngerBees = false;
        if (i >= FULL_HONEY_LEVEL) {
            Item item = itemStack.getItem();
            if (itemStack.isOf(Items.SHEARS)) {
                world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                dropHoneycomb(world, pos);
                itemStack.damage(1, player, (playerx) -> playerx.sendToolBreakStatus(hand));
                couldAngerBees = true;
                world.emitGameEvent(player, GameEvent.SHEAR, pos);
            } else if (itemStack.isOf(Items.GLASS_BOTTLE)) {
                itemStack.decrement(1);
                world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                if (itemStack.isEmpty()) {
                    player.setStackInHand(hand, new ItemStack(Items.HONEY_BOTTLE));
                } else if (!player.getInventory().insertStack(new ItemStack(Items.HONEY_BOTTLE))) {
                    player.dropItem(new ItemStack(Items.HONEY_BOTTLE), false);
                }

                couldAngerBees = true;
                world.emitGameEvent(player, GameEvent.FLUID_PICKUP, pos);
            }

            if (!world.isClient() && couldAngerBees) {
                player.incrementStat(Stats.USED.getOrCreateStat(item));
            }
        }

        if (couldAngerBees) {
            if (!CampfireBlock.isLitCampfireInRange(world, pos)) {
                if (this.hasBees(world, pos)) {
                    this.angerNearbyBees(world, pos);
                }

                this.takeHoney(world, state, pos, player, GolemBeehiveBlockEntity.BeeState.EMERGENCY);
            } else {
                this.takeHoney(world, state, pos);
            }

            return ActionResult.success(world.isClient);
        } else {
            return super.onUse(state, world, pos, player, hand, hit);
        }
    }

    private boolean hasBees(World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof GolemBeehiveBlockEntity beehiveBlockEntity) {
            return beehiveBlockEntity.hasBees();
        } else {
            return false;
        }
    }

    public void takeHoney(World world, BlockState state, BlockPos pos, @Nullable PlayerEntity player, GolemBeehiveBlockEntity.BeeState beeState) {
        this.takeHoney(world, state, pos);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof GolemBeehiveBlockEntity beehiveBlockEntity) {
            beehiveBlockEntity.angerBees(player, state, beeState);
        }

    }

    public void takeHoney(World world, BlockState state, BlockPos pos) {
        world.setBlockState(pos, state.with(HONEY_LEVEL, 0), 3);
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, AbstractRandom random) {
        if (state.get(HONEY_LEVEL) >= FULL_HONEY_LEVEL) {
            for (int i = 0; i < random.nextInt(1) + 1; ++i) {
                this.spawnHoneyParticles(world, pos, state);
            }
        }

    }

    private void spawnHoneyParticles(World world, BlockPos pos, BlockState state) {
        if (state.getFluidState().isEmpty() && !(world.random.nextFloat() < 0.3F)) {
            VoxelShape voxelShape = state.getCollisionShape(world, pos);
            double d = voxelShape.getMax(Direction.Axis.Y);
            if (d >= 1.0 && !state.isIn(BlockTags.IMPERMEABLE)) {
                double e = voxelShape.getMin(Direction.Axis.Y);
                if (e > 0.0) {
                    this.addHoneyParticle(world, pos, voxelShape, (double) pos.getY() + e - 0.05);
                } else {
                    BlockPos blockPos = pos.down();
                    BlockState blockState = world.getBlockState(blockPos);
                    VoxelShape voxelShape2 = blockState.getCollisionShape(world, blockPos);
                    double f = voxelShape2.getMax(Direction.Axis.Y);
                    if ((f < 1.0 || !blockState.isFullCube(world, blockPos)) && blockState.getFluidState().isEmpty()) {
                        this.addHoneyParticle(world, pos, voxelShape, (double) pos.getY() - 0.05);
                    }
                }
            }

        }
    }

    private void addHoneyParticle(World world, BlockPos pos, VoxelShape shape, double height) {
        this.addHoneyParticle(world, (double) pos.getX() + shape.getMin(Direction.Axis.X), (double) pos.getX() + shape.getMax(Direction.Axis.X), (double) pos.getZ() + shape.getMin(Direction.Axis.Z), (double) pos.getZ() + shape.getMax(Direction.Axis.Z), height);
    }

    private void addHoneyParticle(World world, double minX, double maxX, double minZ, double maxZ, double height) {
        world.addParticle(ParticleTypes.DRIPPING_HONEY, MathHelper.lerp(world.random.nextDouble(), minX, maxX), height, MathHelper.lerp(world.random.nextDouble(), minZ, maxZ), 0.0, 0.0, 0.0);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HONEY_LEVEL, FACING);
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : checkType(type, ModBlocks.GOLEM_BEEHIVE_BLOCK_ENTITY, GolemBeehiveBlockEntity::serverTick);
    }

    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient && player.isCreative() && world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof GolemBeehiveBlockEntity beehiveBlockEntity) {
                ItemStack itemStack = new ItemStack(this);
                int i = state.get(HONEY_LEVEL);
                boolean bl = beehiveBlockEntity.hasBees();
                if (bl || i > 0) {
                    NbtCompound nbtCompound;
                    if (bl) {
                        nbtCompound = new NbtCompound();
                        nbtCompound.put("Bees", beehiveBlockEntity.getBees());
                        BlockItem.setBlockEntityNbt(itemStack, BlockEntityType.BEEHIVE, nbtCompound);
                    }

                    nbtCompound = new NbtCompound();
                    nbtCompound.putInt("honey_level", i);
                    itemStack.setSubNbt("BlockStateTag", nbtCompound);
                    ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
                    itemEntity.setToDefaultPickupDelay();
                    world.spawnEntity(itemEntity);
                }
            }
        }

        super.onBreak(world, pos, state, player);
    }

    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        Entity entity = builder.getNullable(LootContextParameters.THIS_ENTITY);
        if (entity instanceof TntEntity || entity instanceof CreeperEntity || entity instanceof WitherSkullEntity || entity instanceof WitherEntity || entity instanceof TntMinecartEntity) {
            BlockEntity blockEntity = builder.getNullable(LootContextParameters.BLOCK_ENTITY);
            if (blockEntity instanceof GolemBeehiveBlockEntity beehiveBlockEntity) {
                beehiveBlockEntity.angerBees(null, state, GolemBeehiveBlockEntity.BeeState.EMERGENCY);
            }
        }

        return super.getDroppedStacks(state, builder);
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (world.getBlockState(neighborPos).getBlock() instanceof FireBlock) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof GolemBeehiveBlockEntity beehiveBlockEntity) {
                beehiveBlockEntity.angerBees(null, state, GolemBeehiveBlockEntity.BeeState.EMERGENCY);
            }
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }


    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GolemBeehiveBlockEntity(pos, state);
    }
}
