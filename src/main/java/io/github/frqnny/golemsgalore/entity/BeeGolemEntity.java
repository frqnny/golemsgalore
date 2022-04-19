package io.github.frqnny.golemsgalore.entity;

import com.google.common.collect.Lists;
import io.github.frqnny.golemsgalore.GolemsGalore;
import io.github.frqnny.golemsgalore.block.GolemBeehiveBlockEntity;
import io.github.frqnny.golemsgalore.init.ModBlocks;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.AboveGroundTargeting;
import net.minecraft.entity.ai.NoPenaltySolidTargeting;
import net.minecraft.entity.ai.NoWaterTargeting;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.WorldView;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//TODO test
//TODO do not attack owner of golem
public class BeeGolemEntity extends ModGolemEntity implements Flutterer {
    public static final int field_28638 = MathHelper.ceil(1.4959966f);
    public static final String CROPS_GROWN_SINCE_POLLINATION_KEY = "CropsGrownSincePollination";
    public static final String CANNOT_ENTER_HIVE_TICKS_KEY = "CannotEnterHiveTicks";
    public static final String TICKS_SINCE_POLLINATION_KEY = "TicksSincePollination";
    public static final String HAS_NECTAR_KEY = "HasNectar";
    public static final String FLOWER_POS_KEY = "FlowerPos";
    public static final String HIVE_POS_KEY = "HivePos";
    private static final TrackedData<Byte> BEE_FLAGS = DataTracker.registerData(BeeGolemEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Integer> ANGER = DataTracker.registerData(BeeGolemEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final int NEAR_TARGET_FLAG = 2;
    private static final int HAS_NECTAR_FLAG = 8;
    /**
     * A bee will start moving to a flower once this time in ticks has passed from a pollination.
     */
    private static final int FLOWER_NAVIGATION_START_TICKS = 2400;
    /**
     * The duration in ticks when a bee's pollination is considered failed.
     */
    private static final int POLLINATION_FAIL_TICKS = 3600;
    private static final int HARD_DIFFICULTY_STING_POISON_DURATION = 18;
    /**
     * The minimum distance that bees lose their hive or flower position at.
     */
    private static final int TOO_FAR_DISTANCE = 32;
    /**
     * The minimum distance that bees will immediately return to their hive at.
     */
    private static final int MIN_HIVE_RETURN_DISTANCE = 16;
    private static final UniformIntProvider ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 39);
    int ticksSincePollination;
    int ticksLeftToFindHive;
    int ticksUntilCanPollinate;
    @Nullable
    BlockPos flowerPos;
    @Nullable
    BlockPos hivePos;
    BeeGolemEntity.PollinateGoal pollinateGoal;
    BeeGolemEntity.MoveToHiveGoal moveToHiveGoal;
    @Nullable
    private UUID angryAt;
    private float currentPitch;
    private float lastPitch;
    private int cannotEnterHiveTicks;
    private int cropsGrownSincePollination;
    private int ticksInsideWater;

    public BeeGolemEntity(EntityType<? extends BeeGolemEntity> entityType, World world) {
        super(entityType, world);
        this.ticksUntilCanPollinate = MathHelper.nextInt(this.random, 20, 60);
        this.moveControl = new FlightMoveControl(this, 20, true);
        this.lookControl = new BeeGolemEntity.BeeLookControl(this);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, -1.0f);
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0f);
        this.setPathfindingPenalty(PathNodeType.WATER_BORDER, 16.0f);
        this.setPathfindingPenalty(PathNodeType.COCOA, -1.0f);
        this.setPathfindingPenalty(PathNodeType.FENCE, -1.0f);
    }

    public static DefaultAttributeContainer.Builder createBeeAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, GolemsGalore.getConfig().healthBee).add(EntityAttributes.GENERIC_FLYING_SPEED, GolemsGalore.getConfig().speedBee).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, GolemsGalore.getConfig().attackDamageBee).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 12.0).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, GolemsGalore.getConfig().knockbackResistanceBee);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(BEE_FLAGS, (byte) 0);
        this.dataTracker.startTracking(ANGER, 0);
    }

    @Override
    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        if (world.getBlockState(pos).isAir()) {
            return 10.0f;
        }
        return 0.0f;
    }

    @Override
    protected void initGoals() {
        this.moveToHiveGoal = new BeeGolemEntity.MoveToHiveGoal();
        this.pollinateGoal = new BeeGolemEntity.PollinateGoal();


        this.goalSelector.add(0, new BeeGolemEntity.StingGoal(this, 1.4f, true));
        this.goalSelector.add(1, new BeeGolemEntity.EnterHiveGoal());

        this.goalSelector.add(3, new TemptGoal(this, 1.25, Ingredient.fromTag(ItemTags.FLOWERS), false));
        this.goalSelector.add(4, this.pollinateGoal);

        this.goalSelector.add(5, new BeeGolemEntity.FindHiveGoal());
        this.goalSelector.add(5, this.moveToHiveGoal);
        this.goalSelector.add(6, new MoveToFlowerGoal());

        this.goalSelector.add(7, new BeeGolemEntity.GrowCropsGoal());

        this.goalSelector.add(8, new IronGolemLookGoal(this));
        this.goalSelector.add(10, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(11, new BeeGolemEntity.BeeWanderAroundGoal());

        this.goalSelector.add(12, new SwimGoal(this));

        this.targetSelector.add(1, new TrackIronGolemTargetGoal(this));
        this.targetSelector.add(2, new BeeGolemEntity.BeeRevengeGoal(this).setGroupRevenge());
        this.targetSelector.add(2, new BeeGolemEntity.StingTargetGoal(this));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, MobEntity.class, 5, false, false, entity -> entity instanceof Monster && !(entity instanceof CreeperEntity)));
        this.targetSelector.add(3, new UniversalAngerGoal<>(this, true));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.hasHive()) {
            nbt.put(HIVE_POS_KEY, NbtHelper.fromBlockPos(this.getHivePos()));
        }
        if (this.hasFlower()) {
            nbt.put(FLOWER_POS_KEY, NbtHelper.fromBlockPos(this.getFlowerPos()));
        }
        nbt.putBoolean(HAS_NECTAR_KEY, this.hasNectar());
        nbt.putInt(TICKS_SINCE_POLLINATION_KEY, this.ticksSincePollination);
        nbt.putInt(CANNOT_ENTER_HIVE_TICKS_KEY, this.cannotEnterHiveTicks);
        nbt.putInt(CROPS_GROWN_SINCE_POLLINATION_KEY, this.cropsGrownSincePollination);
        this.writeAngerToNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        this.hivePos = null;
        if (nbt.contains(HIVE_POS_KEY)) {
            this.hivePos = NbtHelper.toBlockPos(nbt.getCompound(HIVE_POS_KEY));
        }
        this.flowerPos = null;
        if (nbt.contains(FLOWER_POS_KEY)) {
            this.flowerPos = NbtHelper.toBlockPos(nbt.getCompound(FLOWER_POS_KEY));
        }
        super.readCustomDataFromNbt(nbt);
        this.setHasNectar(nbt.getBoolean(HAS_NECTAR_KEY));
        this.ticksSincePollination = nbt.getInt(TICKS_SINCE_POLLINATION_KEY);
        this.cannotEnterHiveTicks = nbt.getInt(CANNOT_ENTER_HIVE_TICKS_KEY);
        this.cropsGrownSincePollination = nbt.getInt(CROPS_GROWN_SINCE_POLLINATION_KEY);
        this.readAngerFromNbt(this.world, nbt);
    }

    @Override
    //TODO redo
    public boolean tryAttack(Entity target) {
        boolean bl = target.damage(DamageSource.sting(this), (int) this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE));
        if (bl) {
            this.applyDamageEffects(this, target);
            if (target instanceof LivingEntity) {
                ((LivingEntity) target).setStingerCount(((LivingEntity) target).getStingerCount() + 1);
                int i = 0;
                if (this.world.getDifficulty() == Difficulty.NORMAL) {
                    i = 10;
                } else if (this.world.getDifficulty() == Difficulty.HARD) {
                    i = HARD_DIFFICULTY_STING_POISON_DURATION;
                }
                if (i > 0) {
                    ((LivingEntity) target).addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, i * 20, 0), this);
                }
            }
            this.stopAnger();
            this.playSound(SoundEvents.ENTITY_BEE_STING, 1.0f, 1.0f);
        }
        return bl;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.hasNectar() && this.getCropsGrownSincePollination() < 10 && this.random.nextFloat() < 0.05f) {
            for (int i = 0; i < this.random.nextInt(2) + 1; ++i) {
                this.addParticle(this.world, this.getX() - (double) 0.3f, this.getX() + (double) 0.3f, this.getZ() - (double) 0.3f, this.getZ() + (double) 0.3f, this.getBodyY(0.5), ParticleTypes.FALLING_NECTAR);
            }
        }
        this.updateBodyPitch();
    }

    private void addParticle(World world, double lastX, double x, double lastZ, double z, double y, ParticleEffect effect) {
        world.addParticle(effect, MathHelper.lerp(world.random.nextDouble(), lastX, x), y, MathHelper.lerp(world.random.nextDouble(), lastZ, z), 0.0, 0.0, 0.0);
    }

    void startMovingTo(BlockPos pos) {
        Vec3d vec3d2;
        Vec3d vec3d = Vec3d.ofBottomCenter(pos);
        int i = 0;
        BlockPos blockPos = this.getBlockPos();
        int j = (int) vec3d.y - blockPos.getY();
        if (j > 2) {
            i = 4;
        } else if (j < -2) {
            i = -4;
        }
        int k = 6;
        int l = 8;
        int m = blockPos.getManhattanDistance(pos);
        if (m < 15) {
            k = m / 2;
            l = m / 2;
        }
        if ((vec3d2 = NoWaterTargeting.find(this, k, l, i, vec3d, 0.3141592741012573)) == null) {
            return;
        }
        this.navigation.setRangeMultiplier(0.5f);
        this.navigation.startMovingTo(vec3d2.x, vec3d2.y, vec3d2.z, 1.0);
    }

    @Nullable
    public BlockPos getFlowerPos() {
        return this.flowerPos;
    }

    public void setFlowerPos(@Nullable BlockPos flowerPos) {
        this.flowerPos = flowerPos;
    }

    public boolean hasFlower() {
        return this.flowerPos != null;
    }

    private boolean failedPollinatingTooLong() {
        return this.ticksSincePollination > POLLINATION_FAIL_TICKS;
    }

    boolean canEnterHive() {
        if (this.cannotEnterHiveTicks > 0 || this.pollinateGoal.isRunning() || this.getTarget() != null) {
            return false;
        }
        boolean bl = this.failedPollinatingTooLong() || this.world.isRaining() || this.hasNectar();
        return bl && !this.isHiveNearFire();
    }

    public void setCannotEnterHiveTicks(int cannotEnterHiveTicks) {
        this.cannotEnterHiveTicks = cannotEnterHiveTicks;
    }

    public float getBodyPitch(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.lastPitch, this.currentPitch);
    }

    private void updateBodyPitch() {
        this.lastPitch = this.currentPitch;
        this.currentPitch = this.isNearTarget() ? Math.min(1.0f, this.currentPitch + 0.2f) : Math.max(0.0f, this.currentPitch - 0.24f);
    }

    @Override
    protected void mobTick() {
        //boolean hasStung = this.hasStung();
        this.ticksInsideWater = this.isInsideWaterOrBubbleColumn() ? ++this.ticksInsideWater : 0;
        if (this.ticksInsideWater > 20) {
            this.damage(DamageSource.DROWN, 1.0f);
        }

        if (!this.hasNectar()) {
            ++this.ticksSincePollination;
        }
        if (!this.world.isClient) {
            this.tickAngerLogic((ServerWorld) this.world, false);
        }
    }

    public void resetPollinationTicks() {
        this.ticksSincePollination = 0;
    }

    private boolean isHiveNearFire() {
        if (this.hivePos == null) {
            return false;
        }
        BlockEntity blockEntity = this.world.getBlockEntity(this.hivePos);
        return blockEntity instanceof GolemBeehiveBlockEntity && ((GolemBeehiveBlockEntity) blockEntity).isNearFire();
    }

    @Override
    public int getAngerTime() {
        return this.dataTracker.get(ANGER);
    }

    @Override
    public void setAngerTime(int angerTime) {
        this.dataTracker.set(ANGER, angerTime);
    }

    @Override
    @Nullable
    public UUID getAngryAt() {
        return this.angryAt;
    }

    @Override
    public void setAngryAt(@Nullable UUID angryAt) {
        this.angryAt = angryAt;
    }

    @Override
    public void chooseRandomAngerTime() {
        this.setAngerTime(ANGER_TIME_RANGE.get(this.random));
    }

    private boolean doesHiveHaveSpace(BlockPos pos) {
        BlockEntity blockEntity = this.world.getBlockEntity(pos);
        if (blockEntity instanceof GolemBeehiveBlockEntity) {
            return !((GolemBeehiveBlockEntity) blockEntity).isFullOfBees();
        }
        return false;
    }

    @Debug
    public boolean hasHive() {
        return this.hivePos != null;
    }

    @Nullable
    @Debug
    public BlockPos getHivePos() {
        return this.hivePos;
    }

    @Override
    protected void sendAiDebugData() {
        super.sendAiDebugData();
        //DebugInfoSender.sendBeeDebugData(this);
    }

    int getCropsGrownSincePollination() {
        return this.cropsGrownSincePollination;
    }

    private void resetCropCounter() {
        this.cropsGrownSincePollination = 0;
    }

    void addCropCounter() {
        ++this.cropsGrownSincePollination;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (!this.world.isClient) {
            if (this.cannotEnterHiveTicks > 0) {
                --this.cannotEnterHiveTicks;
            }
            if (this.ticksLeftToFindHive > 0) {
                --this.ticksLeftToFindHive;
            }
            if (this.ticksUntilCanPollinate > 0) {
                --this.ticksUntilCanPollinate;
            }
            boolean bl = this.hasAngerTime() && this.getTarget() != null && this.getTarget().squaredDistanceTo(this) < 4.0;
            this.setNearTarget(bl);
            if (this.age % 20 == 0 && !this.isHiveValid()) {
                this.hivePos = null;
            }
        }
    }

    boolean isHiveValid() {
        if (!this.hasHive()) {
            return false;
        }
        BlockEntity blockEntity = this.world.getBlockEntity(this.hivePos);
        return blockEntity != null && blockEntity.getType() == BlockEntityType.BEEHIVE;
    }

    public boolean hasNectar() {
        return this.getBeeFlag(HAS_NECTAR_FLAG);
    }

    void setHasNectar(boolean hasNectar) {
        if (hasNectar) {
            this.resetPollinationTicks();
        }
        this.setBeeFlag(HAS_NECTAR_FLAG, hasNectar);
    }

    private boolean isNearTarget() {
        return this.getBeeFlag(NEAR_TARGET_FLAG);
    }

    private void setNearTarget(boolean nearTarget) {
        this.setBeeFlag(NEAR_TARGET_FLAG, nearTarget);
    }

    boolean isTooFar(BlockPos pos) {
        return !this.isWithinDistance(pos, 32);
    }

    private void setBeeFlag(int bit, boolean value) {
        if (value) {
            this.dataTracker.set(BEE_FLAGS, (byte) (this.dataTracker.get(BEE_FLAGS) | bit));
        } else {
            this.dataTracker.set(BEE_FLAGS, (byte) (this.dataTracker.get(BEE_FLAGS) & ~bit));
        }
    }

    private boolean getBeeFlag(int location) {
        return (this.dataTracker.get(BEE_FLAGS) & location) != 0;
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        BirdNavigation birdNavigation = new BirdNavigation(this, world) {

            @Override
            public boolean isValidPosition(BlockPos pos) {
                return !this.world.getBlockState(pos.down()).isAir();
            }

            @Override
            public void tick() {
                if (BeeGolemEntity.this.pollinateGoal.isRunning()) {
                    return;
                }
                super.tick();
            }
        };
        birdNavigation.setCanPathThroughDoors(false);
        birdNavigation.setCanSwim(false);
        birdNavigation.setCanEnterOpenDoors(true);
        return birdNavigation;
    }

    boolean isFlowers(BlockPos pos) {
        return this.world.canSetBlock(pos) && this.world.getBlockState(pos).isIn(BlockTags.FLOWERS);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_BEE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_BEE_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4f;
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.5f;
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
    }

    @Override
    public boolean hasWings() {
        return this.isInAir() && this.age % field_28638 == 0;
    }

    @Override
    public boolean isInAir() {
        return !this.onGround;
    }

    public void onHoneyDelivered() {
        this.setHasNectar(false);
        this.resetCropCounter();
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (!this.world.isClient) {
            this.pollinateGoal.cancel();
        }
        return super.damage(source, amount);
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.ARTHROPOD;
    }

    @Override
    protected void swimUpward(TagKey<Fluid> fluid) {
        this.setVelocity(this.getVelocity().add(0.0, 0.01, 0.0));
    }

    @Override
    public Vec3d getLeashOffset() {
        return new Vec3d(0.0, 0.5f * this.getStandingEyeHeight(), this.getWidth() * 0.2f);
    }

    boolean isWithinDistance(BlockPos pos, int distance) {
        return pos.isWithinDistance(this.getBlockPos(), distance);
    }

    static class StingTargetGoal
            extends ActiveTargetGoal<PlayerEntity> {
        StingTargetGoal(BeeGolemEntity bee) {
            super(bee, PlayerEntity.class, 10, true, false, bee::shouldAngerAt);
        }

        @Override
        public boolean canStart() {
            return this.canSting() && super.canStart();
        }

        @Override
        public boolean shouldContinue() {
            boolean bl = this.canSting();
            if (!bl || this.mob.getTarget() == null) {
                this.target = null;
                return false;
            }
            return super.shouldContinue();
        }

        private boolean canSting() {
            BeeGolemEntity beeGolem = (BeeGolemEntity) this.mob;
            return beeGolem.hasAngerTime();
        }
    }

    class PollinateGoal
            extends BeeGolemEntity.NotAngryGoal {
        private final Predicate<BlockState> flowerPredicate;
        private int pollinationTicks;
        private int lastPollinationTick;
        private boolean running;
        @Nullable
        private Vec3d nextTarget;
        private int ticks;

        PollinateGoal() {
            this.flowerPredicate = state -> {
                if (state.contains(Properties.WATERLOGGED) && state.get(Properties.WATERLOGGED)) {
                    return false;
                }
                if (state.isIn(BlockTags.FLOWERS)) {
                    if (state.isOf(Blocks.SUNFLOWER)) {
                        return state.get(TallPlantBlock.HALF) == DoubleBlockHalf.UPPER;
                    }
                    return true;
                }
                return false;
            };
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canBeeStart() {
            if (BeeGolemEntity.this.ticksUntilCanPollinate > 0) {
                return false;
            }
            if (BeeGolemEntity.this.hasNectar()) {
                return false;
            }
            if (BeeGolemEntity.this.world.isRaining()) {
                return false;
            }
            Optional<BlockPos> optional = this.getFlower();
            if (optional.isPresent()) {
                BeeGolemEntity.this.flowerPos = optional.get();
                BeeGolemEntity.this.navigation.startMovingTo((double) BeeGolemEntity.this.flowerPos.getX() + 0.5, (double) BeeGolemEntity.this.flowerPos.getY() + 0.5, (double) BeeGolemEntity.this.flowerPos.getZ() + 0.5, 1.2f);
                return true;
            }
            BeeGolemEntity.this.ticksUntilCanPollinate = MathHelper.nextInt(BeeGolemEntity.this.random, 20, 60);
            return false;
        }

        @Override
        public boolean canBeeContinue() {
            if (!this.running) {
                return false;
            }
            if (!BeeGolemEntity.this.hasFlower()) {
                return false;
            }
            if (BeeGolemEntity.this.world.isRaining()) {
                return false;
            }
            if (this.completedPollination()) {
                return BeeGolemEntity.this.random.nextFloat() < 0.2f;
            }
            if (BeeGolemEntity.this.age % 20 == 0 && !BeeGolemEntity.this.isFlowers(BeeGolemEntity.this.flowerPos)) {
                BeeGolemEntity.this.flowerPos = null;
                return false;
            }
            return true;
        }

        private boolean completedPollination() {
            return this.pollinationTicks > 400;
        }

        boolean isRunning() {
            return this.running;
        }

        void cancel() {
            this.running = false;
        }

        @Override
        public void start() {
            this.pollinationTicks = 0;
            this.ticks = 0;
            this.lastPollinationTick = 0;
            this.running = true;
            BeeGolemEntity.this.resetPollinationTicks();
        }

        @Override
        public void stop() {
            if (this.completedPollination()) {
                BeeGolemEntity.this.setHasNectar(true);
            }
            this.running = false;
            BeeGolemEntity.this.navigation.stop();
            BeeGolemEntity.this.ticksUntilCanPollinate = 200;
        }

        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            ++this.ticks;
            if (this.ticks > 600) {
                BeeGolemEntity.this.flowerPos = null;
                return;
            }
            Vec3d vec3d = Vec3d.ofBottomCenter(BeeGolemEntity.this.flowerPos).add(0.0, 0.6f, 0.0);
            if (vec3d.distanceTo(BeeGolemEntity.this.getPos()) > 1.0) {
                this.nextTarget = vec3d;
                this.moveToNextTarget();
                return;
            }
            if (this.nextTarget == null) {
                this.nextTarget = vec3d;
            }
            boolean bl = BeeGolemEntity.this.getPos().distanceTo(this.nextTarget) <= 0.1;
            boolean bl2 = true;
            if (!bl && this.ticks > 600) {
                BeeGolemEntity.this.flowerPos = null;
                return;
            }
            if (bl) {
                boolean bl3 = BeeGolemEntity.this.random.nextInt(25) == 0;
                if (bl3) {
                    this.nextTarget = new Vec3d(vec3d.getX() + (double) this.getRandomOffset(), vec3d.getY(), vec3d.getZ() + (double) this.getRandomOffset());
                    BeeGolemEntity.this.navigation.stop();
                } else {
                    bl2 = false;
                }
                BeeGolemEntity.this.getLookControl().lookAt(vec3d.getX(), vec3d.getY(), vec3d.getZ());
            }
            if (bl2) {
                this.moveToNextTarget();
            }
            ++this.pollinationTicks;
            if (BeeGolemEntity.this.random.nextFloat() < 0.05f && this.pollinationTicks > this.lastPollinationTick + 60) {
                this.lastPollinationTick = this.pollinationTicks;
                BeeGolemEntity.this.playSound(SoundEvents.ENTITY_BEE_POLLINATE, 1.0f, 1.0f);
            }
        }

        private void moveToNextTarget() {
            BeeGolemEntity.this.getMoveControl().moveTo(this.nextTarget.getX(), this.nextTarget.getY(), this.nextTarget.getZ(), 0.35f);
        }

        private float getRandomOffset() {
            return (BeeGolemEntity.this.random.nextFloat() * 2.0f - 1.0f) * 0.33333334f;
        }

        private Optional<BlockPos> getFlower() {
            return this.findFlower(this.flowerPredicate, 5.0);
        }

        private Optional<BlockPos> findFlower(Predicate<BlockState> predicate, double searchDistance) {
            BlockPos blockPos = BeeGolemEntity.this.getBlockPos();
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            int i = 0;
            while ((double) i <= searchDistance) {
                int j = 0;
                while ((double) j < searchDistance) {
                    int k = 0;
                    while (k <= j) {
                        int l = k < j && k > -j ? j : 0;
                        while (l <= j) {
                            mutable.set(blockPos, k, i - 1, l);
                            if (blockPos.isWithinDistance(mutable, searchDistance) && predicate.test(BeeGolemEntity.this.world.getBlockState(mutable))) {
                                return Optional.of(mutable);
                            }
                            l = l > 0 ? -l : 1 - l;
                        }
                        k = k > 0 ? -k : 1 - k;
                    }
                    ++j;
                }
                i = i > 0 ? -i : 1 - i;
            }
            return Optional.empty();
        }
    }

    class BeeLookControl
            extends LookControl {
        BeeLookControl(MobEntity entity) {
            super(entity);
        }

        @Override
        public void tick() {
            if (BeeGolemEntity.this.hasAngerTime()) {
                return;
            }
            super.tick();
        }

        @Override
        protected boolean shouldStayHorizontal() {
            return !BeeGolemEntity.this.pollinateGoal.isRunning();
        }
    }

    class StingGoal
            extends MeleeAttackGoal {
        StingGoal(PathAwareEntity mob, double speed, boolean pauseWhenMobIdle) {
            super(mob, speed, pauseWhenMobIdle);
        }

        @Override
        public boolean canStart() {
            return super.canStart() && BeeGolemEntity.this.hasAngerTime();
        }

        @Override
        public boolean shouldContinue() {
            return super.shouldContinue() && BeeGolemEntity.this.hasAngerTime();
        }
    }

    class EnterHiveGoal
            extends BeeGolemEntity.NotAngryGoal {
        EnterHiveGoal() {
        }

        @Override
        public boolean canBeeStart() {
            BlockEntity blockEntity;
            if (BeeGolemEntity.this.hasHive() && BeeGolemEntity.this.canEnterHive() && BeeGolemEntity.this.hivePos.isWithinDistance(BeeGolemEntity.this.getPos(), 2.0) && (blockEntity = BeeGolemEntity.this.world.getBlockEntity(BeeGolemEntity.this.hivePos)) instanceof GolemBeehiveBlockEntity) {
                GolemBeehiveBlockEntity GolemBeehiveBlockEntity = (GolemBeehiveBlockEntity) blockEntity;
                if (GolemBeehiveBlockEntity.isFullOfBees()) {
                    BeeGolemEntity.this.hivePos = null;
                } else {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean canBeeContinue() {
            return false;
        }

        @Override
        public void start() {
            BlockEntity blockEntity = BeeGolemEntity.this.world.getBlockEntity(BeeGolemEntity.this.hivePos);
            if (blockEntity instanceof GolemBeehiveBlockEntity GolemBeehiveBlockEntity) {
                GolemBeehiveBlockEntity.tryEnterHive(BeeGolemEntity.this, BeeGolemEntity.this.hasNectar());
            }
        }
    }

    class FindHiveGoal
            extends BeeGolemEntity.NotAngryGoal {
        FindHiveGoal() {
        }

        @Override
        public boolean canBeeStart() {
            return BeeGolemEntity.this.ticksLeftToFindHive == 0 && !BeeGolemEntity.this.hasHive() && BeeGolemEntity.this.canEnterHive();
        }

        @Override
        public boolean canBeeContinue() {
            return false;
        }

        @Override
        public void start() {
            BeeGolemEntity.this.ticksLeftToFindHive = 200;
            List<BlockPos> list = this.getNearbyFreeHives();
            if (list.isEmpty()) {
                return;
            }
            for (BlockPos blockPos : list) {
                if (BeeGolemEntity.this.moveToHiveGoal.isPossibleHive(blockPos)) continue;
                BeeGolemEntity.this.hivePos = blockPos;
                return;
            }
            BeeGolemEntity.this.moveToHiveGoal.clearPossibleHives();
            BeeGolemEntity.this.hivePos = list.get(0);
        }

        private List<BlockPos> getNearbyFreeHives() {
            BlockPos blockPos = BeeGolemEntity.this.getBlockPos();
            PointOfInterestStorage pointOfInterestStorage = ((ServerWorld) BeeGolemEntity.this.world).getPointOfInterestStorage();
            Stream<PointOfInterest> stream = pointOfInterestStorage.getInCircle(poiType -> poiType == ModBlocks.BEEHIVE, blockPos, 20, PointOfInterestStorage.OccupationStatus.ANY);
            return stream.map(PointOfInterest::getPos).filter(BeeGolemEntity.this::doesHiveHaveSpace).sorted(Comparator.comparingDouble(blockPos2 -> blockPos2.getSquaredDistance(blockPos))).collect(Collectors.toList());
        }
    }

    @Debug
    public class MoveToHiveGoal
            extends BeeGolemEntity.NotAngryGoal {
        final List<BlockPos> possibleHives;
        int ticks;
        @Nullable
        private Path path;
        private int ticksUntilLost;

        MoveToHiveGoal() {
            this.ticks = BeeGolemEntity.this.world.random.nextInt(10);
            this.possibleHives = Lists.newArrayList();
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canBeeStart() {
            return BeeGolemEntity.this.hivePos != null && !BeeGolemEntity.this.hasPositionTarget() && BeeGolemEntity.this.canEnterHive() && !this.isCloseEnough(BeeGolemEntity.this.hivePos) && BeeGolemEntity.this.world.getBlockState(BeeGolemEntity.this.hivePos).isIn(ModBlocks.BEEHIVES);
        }

        @Override
        public boolean canBeeContinue() {
            return this.canBeeStart();
        }

        @Override
        public void start() {
            this.ticks = 0;
            this.ticksUntilLost = 0;
            super.start();
        }

        @Override
        public void stop() {
            this.ticks = 0;
            this.ticksUntilLost = 0;
            BeeGolemEntity.this.navigation.stop();
            BeeGolemEntity.this.navigation.resetRangeMultiplier();
        }

        @Override
        public void tick() {
            if (BeeGolemEntity.this.hivePos == null) {
                return;
            }
            ++this.ticks;
            if (this.ticks > this.getTickCount(600)) {
                this.makeChosenHivePossibleHive();
                return;
            }
            if (BeeGolemEntity.this.navigation.isFollowingPath()) {
                return;
            }
            if (BeeGolemEntity.this.isWithinDistance(BeeGolemEntity.this.hivePos, 16)) {
                boolean bl = this.startMovingToFar(BeeGolemEntity.this.hivePos);
                if (!bl) {
                    this.makeChosenHivePossibleHive();
                } else if (this.path != null && BeeGolemEntity.this.navigation.getCurrentPath().equalsPath(this.path)) {
                    ++this.ticksUntilLost;
                    if (this.ticksUntilLost > 60) {
                        this.setLost();
                        this.ticksUntilLost = 0;
                    }
                } else {
                    this.path = BeeGolemEntity.this.navigation.getCurrentPath();
                }
                return;
            }
            if (BeeGolemEntity.this.isTooFar(BeeGolemEntity.this.hivePos)) {
                this.setLost();
                return;
            }
            BeeGolemEntity.this.startMovingTo(BeeGolemEntity.this.hivePos);
        }

        private boolean startMovingToFar(BlockPos pos) {
            BeeGolemEntity.this.navigation.setRangeMultiplier(10.0f);
            BeeGolemEntity.this.navigation.startMovingTo(pos.getX(), pos.getY(), pos.getZ(), 1.0);
            return BeeGolemEntity.this.navigation.getCurrentPath() != null && BeeGolemEntity.this.navigation.getCurrentPath().reachesTarget();
        }

        boolean isPossibleHive(BlockPos pos) {
            return this.possibleHives.contains(pos);
        }

        private void addPossibleHive(BlockPos pos) {
            this.possibleHives.add(pos);
            while (this.possibleHives.size() > 3) {
                this.possibleHives.remove(0);
            }
        }

        void clearPossibleHives() {
            this.possibleHives.clear();
        }

        private void makeChosenHivePossibleHive() {
            if (BeeGolemEntity.this.hivePos != null) {
                this.addPossibleHive(BeeGolemEntity.this.hivePos);
            }
            this.setLost();
        }

        private void setLost() {
            BeeGolemEntity.this.hivePos = null;
            BeeGolemEntity.this.ticksLeftToFindHive = 200;
        }

        private boolean isCloseEnough(BlockPos pos) {
            if (BeeGolemEntity.this.isWithinDistance(pos, 2)) {
                return true;
            }
            Path path = BeeGolemEntity.this.navigation.getCurrentPath();
            return path != null && path.getTarget().equals(pos) && path.reachesTarget() && path.isFinished();
        }
    }

    public class MoveToFlowerGoal
            extends BeeGolemEntity.NotAngryGoal {
        int ticks;

        MoveToFlowerGoal() {
            this.ticks = BeeGolemEntity.this.world.random.nextInt(10);
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canBeeStart() {
            return BeeGolemEntity.this.flowerPos != null && !BeeGolemEntity.this.hasPositionTarget() && this.shouldMoveToFlower() && BeeGolemEntity.this.isFlowers(BeeGolemEntity.this.flowerPos) && !BeeGolemEntity.this.isWithinDistance(BeeGolemEntity.this.flowerPos, 2);
        }

        @Override
        public boolean canBeeContinue() {
            return this.canBeeStart();
        }

        @Override
        public void start() {
            this.ticks = 0;
            super.start();
        }

        @Override
        public void stop() {
            this.ticks = 0;
            BeeGolemEntity.this.navigation.stop();
            BeeGolemEntity.this.navigation.resetRangeMultiplier();
        }

        @Override
        public void tick() {
            if (BeeGolemEntity.this.flowerPos == null) {
                return;
            }
            ++this.ticks;
            if (this.ticks > this.getTickCount(600)) {
                BeeGolemEntity.this.flowerPos = null;
                return;
            }
            if (BeeGolemEntity.this.navigation.isFollowingPath()) {
                return;
            }
            if (BeeGolemEntity.this.isTooFar(BeeGolemEntity.this.flowerPos)) {
                BeeGolemEntity.this.flowerPos = null;
                return;
            }
            BeeGolemEntity.this.startMovingTo(BeeGolemEntity.this.flowerPos);
        }

        private boolean shouldMoveToFlower() {
            return BeeGolemEntity.this.ticksSincePollination > FLOWER_NAVIGATION_START_TICKS;
        }
    }

    class GrowCropsGoal
            extends BeeGolemEntity.NotAngryGoal {

        GrowCropsGoal() {
        }

        @Override
        public boolean canBeeStart() {
            if (BeeGolemEntity.this.getCropsGrownSincePollination() >= 10) {
                return false;
            }
            if (BeeGolemEntity.this.random.nextFloat() < 0.3f) {
                return false;
            }
            return BeeGolemEntity.this.hasNectar() && BeeGolemEntity.this.isHiveValid();
        }

        @Override
        public boolean canBeeContinue() {
            return this.canBeeStart();
        }

        @Override
        public void tick() {
            if (BeeGolemEntity.this.random.nextInt(this.getTickCount(30)) != 0) {
                return;
            }
            for (int i = 1; i <= 2; ++i) {
                BlockPos blockPos = BeeGolemEntity.this.getBlockPos().down(i);
                BlockState blockState = BeeGolemEntity.this.world.getBlockState(blockPos);
                Block block = blockState.getBlock();
                boolean bl = false;
                IntProperty intProperty = null;
                if (!blockState.isIn(BlockTags.BEE_GROWABLES)) continue;
                if (block instanceof CropBlock cropBlock) {
                    if (!cropBlock.isMature(blockState)) {
                        bl = true;
                        intProperty = cropBlock.getAgeProperty();
                    }
                } else if (block instanceof StemBlock) {
                    int j = blockState.get(StemBlock.AGE);
                    if (j < 7) {
                        bl = true;
                        intProperty = StemBlock.AGE;
                    }
                } else if (blockState.isOf(Blocks.SWEET_BERRY_BUSH)) {
                    int j = blockState.get(SweetBerryBushBlock.AGE);
                    if (j < 3) {
                        bl = true;
                        intProperty = SweetBerryBushBlock.AGE;
                    }
                } else if (blockState.isOf(Blocks.CAVE_VINES) || blockState.isOf(Blocks.CAVE_VINES_PLANT)) {
                    ((Fertilizable) blockState.getBlock()).grow((ServerWorld) BeeGolemEntity.this.world, BeeGolemEntity.this.random, blockPos, blockState);
                }
                if (!bl) continue;
                BeeGolemEntity.this.world.syncWorldEvent(WorldEvents.PLANT_FERTILIZED, blockPos, 0);
                BeeGolemEntity.this.world.setBlockState(blockPos, blockState.with(intProperty, blockState.get(intProperty) + 1));
                BeeGolemEntity.this.addCropCounter();
            }
        }
    }

    class BeeWanderAroundGoal
            extends Goal {

        BeeWanderAroundGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return BeeGolemEntity.this.navigation.isIdle() && BeeGolemEntity.this.random.nextInt(10) == 0;
        }

        @Override
        public boolean shouldContinue() {
            return BeeGolemEntity.this.navigation.isFollowingPath();
        }

        @Override
        public void start() {
            Vec3d vec3d = this.getRandomLocation();
            if (vec3d != null) {
                BeeGolemEntity.this.navigation.startMovingAlong(BeeGolemEntity.this.navigation.findPathTo(new BlockPos(vec3d), 1), 1.0);
            }
        }

        @Nullable
        private Vec3d getRandomLocation() {
            Vec3d vec3d2;
            if (BeeGolemEntity.this.isHiveValid() && !BeeGolemEntity.this.isWithinDistance(BeeGolemEntity.this.hivePos, 22)) {
                Vec3d vec3d = Vec3d.ofCenter(BeeGolemEntity.this.hivePos);
                vec3d2 = vec3d.subtract(BeeGolemEntity.this.getPos()).normalize();
            } else {
                vec3d2 = BeeGolemEntity.this.getRotationVec(0.0f);
            }
            Vec3d vec3d3 = AboveGroundTargeting.find(BeeGolemEntity.this, 8, 7, vec3d2.x, vec3d2.z, 1.5707964f, 3, 1);
            if (vec3d3 != null) {
                return vec3d3;
            }
            return NoPenaltySolidTargeting.find(BeeGolemEntity.this, 8, 4, -2, vec3d2.x, vec3d2.z, 1.5707963705062866);
        }
    }

    class BeeRevengeGoal
            extends RevengeGoal {
        BeeRevengeGoal(BeeGolemEntity bee) {
            super(bee);
        }

        @Override
        public boolean shouldContinue() {
            return BeeGolemEntity.this.hasAngerTime() && super.shouldContinue();
        }

        @Override
        protected void setMobEntityTarget(MobEntity mob, LivingEntity target) {
            if (mob instanceof BeeGolemEntity && this.mob.canSee(target)) {
                mob.setTarget(target);
            }
        }
    }

    abstract class NotAngryGoal
            extends Goal {
        NotAngryGoal() {
        }

        public abstract boolean canBeeStart();

        public abstract boolean canBeeContinue();

        @Override
        public boolean canStart() {
            return this.canBeeStart() && !BeeGolemEntity.this.hasAngerTime();
        }

        @Override
        public boolean shouldContinue() {
            return this.canBeeContinue() && !BeeGolemEntity.this.hasAngerTime();
        }
    }
}

