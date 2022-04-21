package io.github.frqnny.golemsgalore.block;

import com.google.common.collect.Lists;
import io.github.frqnny.golemsgalore.GolemsGalore;
import io.github.frqnny.golemsgalore.entity.BeeGolemEntity;
import io.github.frqnny.golemsgalore.init.ModBlocks;
import io.github.frqnny.golemsgalore.init.ModEntities;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class GolemBeehiveBlockEntity extends BlockEntity {
    public static final String FLOWER_POS_KEY = "FlowerPos";
    public static final String MIN_OCCUPATION_TICKS_KEY = "MinOccupationTicks";
    public static final String ENTITY_DATA_KEY = "EntityData";
    public static final String TICKS_IN_HIVE_KEY = "TicksInHive";
    public static final String HAS_NECTAR_KEY = "HasNectar";
    public static final String BEES_KEY = "Bees";
    private static final List<String> IRRELEVANT_BEE_NBT_KEYS = Arrays.asList("Air", "ArmorDropChances", "ArmorItems", "Brain", "CanPickUpLoot", "DeathTime", "FallDistance", "FallFlying", "Fire", "HandDropChances", "HandItems", "HurtByTimestamp", "HurtTime", "LeftHanded", "Motion", "NoGravity", "OnGround", "PortalCooldown", "Pos", "Rotation", "CannotEnterHiveTicks", "TicksSincePollination", "CropsGrownSincePollination", "HivePos", "Passengers", "Leash", "UUID");
    public static final int MAX_BEE_COUNT = GolemsGalore.getConfig().maxBeePerGolemHive;
    private static final int ANGERED_CANNOT_ENTER_HIVE_TICKS = 400;
    private static final int MIN_OCCUPATION_TICKS_WITH_NECTAR = GolemsGalore.getConfig().minOccupationTicksWithNectar;
    public static final int MIN_OCCUPATION_TICKS_WITHOUT_NECTAR = 600;
    private final List<Bee> bees = Lists.newArrayList();
    @Nullable
    private BlockPos flowerPos;

    public GolemBeehiveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.GOLEM_BEEHIVE_BLOCK_ENTITY, pos, state);
    }

    public void markDirty() {
        if (this.isNearFire()) {
            this.angerBees(null, this.world.getBlockState(this.getPos()), BeeState.EMERGENCY);
        }

        super.markDirty();
    }

    public boolean isNearFire() {
        if (this.world == null) {
            return false;
        } else {
            Iterator<BlockPos> var1 = BlockPos.iterate(this.pos.add(-1, -1, -1), this.pos.add(1, 1, 1)).iterator();

            BlockPos blockPos;
            do {
                if (!var1.hasNext()) {
                    return false;
                }

                blockPos = var1.next();
            } while(!(this.world.getBlockState(blockPos).getBlock() instanceof FireBlock));

            return true;
        }
    }

    public boolean hasBees() {
        return !this.bees.isEmpty();
    }

    public boolean isFullOfBees() {
        return this.bees.size() == MAX_BEE_COUNT;
    }

    public void angerBees(@Nullable PlayerEntity player, BlockState state, GolemBeehiveBlockEntity.BeeState beeState) {
        List<Entity> list = this.tryReleaseBee(state, beeState);
        if (player != null) {

            for (Entity entity : list) {
                if (entity instanceof BeeGolemEntity BeeGolemEntity) {
                    if (player.getPos().squaredDistanceTo(entity.getPos()) <= 16.0) {
                        if (!this.isSmoked()) {
                            BeeGolemEntity.setTarget(player);
                        } else {
                            BeeGolemEntity.setCannotEnterHiveTicks(ANGERED_CANNOT_ENTER_HIVE_TICKS);
                        }
                    }
                }
            }
        }

    }

    private List<Entity> tryReleaseBee(BlockState state, GolemBeehiveBlockEntity.BeeState beeState) {
        List<Entity> list = Lists.newArrayList();
        this.bees.removeIf((bee) -> releaseBee(this.world, this.pos, state, bee, list, beeState, this.flowerPos));
        if (!list.isEmpty()) {
            super.markDirty();
        }

        return list;
    }

    public void tryEnterHive(Entity entity, boolean hasNectar) {
        this.tryEnterHive(entity, hasNectar, 0);
    }

    @Debug
    public int getBeeCount() {
        return this.bees.size();
    }

    public static int getHoneyLevel(BlockState state) {
        return state.get(BeehiveBlock.HONEY_LEVEL);
    }

    @Debug
    public boolean isSmoked() {
        return CampfireBlock.isLitCampfireInRange(this.world, this.getPos());
    }

    public void tryEnterHive(Entity entity, boolean hasNectar, int ticksInHive) {
        if (this.bees.size() < MAX_BEE_COUNT) {
            entity.stopRiding();
            entity.removeAllPassengers();
            NbtCompound nbtCompound = new NbtCompound();
            entity.saveNbt(nbtCompound);
            this.addBee(nbtCompound, ticksInHive, hasNectar);
            if (this.world != null) {
                if (entity instanceof BeeGolemEntity BeeGolemEntity) {
                    if (BeeGolemEntity.hasFlower() && (!this.hasFlowerPos() || this.world.random.nextBoolean())) {
                        this.flowerPos = BeeGolemEntity.getFlowerPos();
                    }
                }

                BlockPos blockPos = this.getPos();
                this.world.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.BLOCK_BEEHIVE_ENTER, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }

            entity.discard();
            super.markDirty();
        }
    }

    public void addBee(NbtCompound nbtCompound, int ticksInHive, boolean hasNectar) {
        this.bees.add(new GolemBeehiveBlockEntity.Bee(nbtCompound, ticksInHive, hasNectar ? MIN_OCCUPATION_TICKS_WITH_NECTAR : MIN_OCCUPATION_TICKS_WITHOUT_NECTAR));
    }

    private static boolean releaseBee(World world, BlockPos pos, BlockState state, GolemBeehiveBlockEntity.Bee bee, @Nullable List<Entity> entities, GolemBeehiveBlockEntity.BeeState beeState, @Nullable BlockPos flowerPos) {
        if ((world.isNight() || world.isRaining()) && beeState != GolemBeehiveBlockEntity.BeeState.EMERGENCY) {
            return false;
        } else {
            NbtCompound nbtCompound = bee.entityData.copy();
            removeIrrelevantNbtKeys(nbtCompound);
            nbtCompound.put("HivePos", NbtHelper.fromBlockPos(pos));
            nbtCompound.putBoolean("NoGravity", true);
            Direction direction = state.get(BeehiveBlock.FACING);
            BlockPos blockPos = pos.offset(direction);
            boolean bl = !world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty();
            if (bl && beeState != GolemBeehiveBlockEntity.BeeState.EMERGENCY) {
                return false;
            } else {
                Entity entity = EntityType.loadEntityWithPassengers(nbtCompound, world, (entityx) -> entityx);
                if (entity != null) {
                    if (!entity.getType().isIn(ModEntities.GOLEM_BEEHIVE_INHABITORS)) {
                        return false;
                    } else {
                        if (entity instanceof BeeGolemEntity BeeGolemEntity) {
                            if (flowerPos != null && !BeeGolemEntity.hasFlower() && world.random.nextFloat() < 0.9F) {
                                BeeGolemEntity.setFlowerPos(flowerPos);
                            }

                            if (beeState == GolemBeehiveBlockEntity.BeeState.HONEY_DELIVERED) {
                                BeeGolemEntity.onHoneyDelivered();
                                if (state.isIn(ModBlocks.BEEHIVES, (statex) -> statex.contains(BeehiveBlock.HONEY_LEVEL))) {
                                    int i = getHoneyLevel(state);
                                    if (i < 5) {
                                        int j = world.random.nextInt(100) == 0 ? 2 : 1;
                                        if (i + j > 5) {
                                            --j;
                                        }

                                        world.setBlockState(pos, state.with(BeehiveBlock.HONEY_LEVEL, i + j));
                                    }
                                }
                            }

                            if (entities != null) {
                                entities.add(BeeGolemEntity);
                            }

                            float f = entity.getWidth();
                            double d = bl ? 0.0 : 0.55 + (double)(f / 2.0F);
                            double e = (double)pos.getX() + 0.5 + d * (double)direction.getOffsetX();
                            double g = (double)pos.getY() + 0.5 - (double)(entity.getHeight() / 2.0F);
                            double h = (double)pos.getZ() + 0.5 + d * (double)direction.getOffsetZ();
                            entity.refreshPositionAndAngles(e, g, h, entity.getYaw(), entity.getPitch());
                        }

                        world.playSound(null, pos, SoundEvents.BLOCK_BEEHIVE_EXIT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        return world.spawnEntity(entity);
                    }
                } else {
                    return false;
                }
            }
        }
    }

    static void removeIrrelevantNbtKeys(NbtCompound compound) {
        for (String string : IRRELEVANT_BEE_NBT_KEYS) {
            compound.remove(string);
        }

    }

    private boolean hasFlowerPos() {
        return this.flowerPos != null;
    }

    private static void tickBees(World world, BlockPos pos, BlockState state, List<GolemBeehiveBlockEntity.Bee> bees, @Nullable BlockPos flowerPos) {
        boolean bl = false;

        GolemBeehiveBlockEntity.Bee bee;
        for(Iterator<GolemBeehiveBlockEntity.Bee> iterator = bees.iterator(); iterator.hasNext(); ++bee.ticksInHive) {
            bee = iterator.next();
            if (bee.ticksInHive > bee.minOccupationTicks) {
                GolemBeehiveBlockEntity.BeeState beeState = bee.entityData.getBoolean(HAS_NECTAR_KEY) ? GolemBeehiveBlockEntity.BeeState.HONEY_DELIVERED : GolemBeehiveBlockEntity.BeeState.BEE_RELEASED;
                if (releaseBee(world, pos, state, bee, null, beeState, flowerPos)) {
                    bl = true;
                    iterator.remove();
                }
            }
        }

        if (bl) {
            markDirty(world, pos, state);
        }

    }

    public static void serverTick(World world, BlockPos pos, BlockState state, GolemBeehiveBlockEntity blockEntity) {
        tickBees(world, pos, state, blockEntity.bees, blockEntity.flowerPos);
        if (!blockEntity.bees.isEmpty() && world.getRandom().nextDouble() < 0.005) {
            double d = (double)pos.getX() + 0.5;
            double e = pos.getY();
            double f = (double)pos.getZ() + 0.5;
            world.playSound(null, d, e, f, SoundEvents.BLOCK_BEEHIVE_WORK, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.bees.clear();
        NbtList nbtList = nbt.getList(BEES_KEY, 10);

        for(int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            GolemBeehiveBlockEntity.Bee bee = new GolemBeehiveBlockEntity.Bee(nbtCompound.getCompound(ENTITY_DATA_KEY), nbtCompound.getInt(TICKS_IN_HIVE_KEY), nbtCompound.getInt(MIN_OCCUPATION_TICKS_KEY));
            this.bees.add(bee);
        }

        this.flowerPos = null;
        if (nbt.contains(FLOWER_POS_KEY)) {
            this.flowerPos = NbtHelper.toBlockPos(nbt.getCompound(FLOWER_POS_KEY));
        }

    }

    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put(BEES_KEY, this.getBees());
        if (this.hasFlowerPos()) {
            nbt.put(FLOWER_POS_KEY, NbtHelper.fromBlockPos(this.flowerPos));
        }

    }

    public NbtList getBees() {
        NbtList nbtList = new NbtList();

        for (Bee bee : this.bees) {
            NbtCompound nbtCompound = bee.entityData.copy();
            nbtCompound.remove("UUID");
            NbtCompound nbtCompound2 = new NbtCompound();
            nbtCompound2.put(ENTITY_DATA_KEY, nbtCompound);
            nbtCompound2.putInt(TICKS_IN_HIVE_KEY, bee.ticksInHive);
            nbtCompound2.putInt(MIN_OCCUPATION_TICKS_KEY, bee.minOccupationTicks);
            nbtList.add(nbtCompound2);
        }

        return nbtList;
    }

    public enum BeeState {
        HONEY_DELIVERED,
        BEE_RELEASED,
        EMERGENCY;

        BeeState() {
        }
    }

    static class Bee {
        final NbtCompound entityData;
        int ticksInHive;
        final int minOccupationTicks;

        Bee(NbtCompound entityData, int ticksInHive, int minOccupationTicks) {
            GolemBeehiveBlockEntity.removeIrrelevantNbtKeys(entityData);
            this.entityData = entityData;
            this.ticksInHive = ticksInHive;
            this.minOccupationTicks = minOccupationTicks;
        }
    }
}
