package io.github.frqnny.golemsgalore.entity;

import io.github.frqnny.golemsgalore.api.enums.Crack;
import io.github.frqnny.golemsgalore.api.enums.Type;
import io.github.frqnny.golemsgalore.entity.ai.GolemLookGoal;
import io.github.frqnny.golemsgalore.entity.ai.TrackGolemTargetGoal;
import io.github.frqnny.golemsgalore.init.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.Durations;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.IntRange;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ModGolemEntity extends GolemEntity implements Angerable {
    protected static final TrackedData<Byte> PLAYER_CREATED = DataTracker.registerData(ModGolemEntity.class, TrackedDataHandlerRegistry.BYTE);
    protected static final TrackedData<ItemStack> TYPE_TRACKER = DataTracker.registerData(ModGolemEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final IntRange randomIntDuration = Durations.betweenSeconds(20, 39);
    protected int attackTicksLeft;
    private int lookingAtVillagerTicksLeft;
    private int angerTicks;
    private UUID angryAt;


    public ModGolemEntity(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
        this.stepHeight = 1.0F;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.add(2, new WanderNearTargetGoal(this, 0.9D, 32.0F));
        this.goalSelector.add(2, new WanderAroundPointOfInterestGoal(this, 0.6D, false));
        this.goalSelector.add(3, new MoveThroughVillageGoal(this, 0.6D, false, 4, () -> false));
        this.goalSelector.add(5, new GolemLookGoal(this));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.6D));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackGolemTargetGoal(this));
        this.targetSelector.add(2, new RevengeGoal(this));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, MobEntity.class, 5, false, false,
                (livingEntity) -> livingEntity instanceof Monster && !(livingEntity instanceof CreeperEntity)));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(PLAYER_CREATED, (byte) 0);
        this.dataTracker.startTracking(TYPE_TRACKER, new ItemStack(Items.DIRT));
    }

    @Override
    protected int getNextAirUnderwater(int air) {
        return air;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (this.attackTicksLeft > 0) {
            --this.attackTicksLeft;
        }

        if (this.lookingAtVillagerTicksLeft > 0) {
            --this.lookingAtVillagerTicksLeft;
        }

        if (squaredHorizontalLength(this.getVelocity()) > 2.500000277905201E-7D && this.random.nextInt(5) == 0) {
            int i = MathHelper.floor(this.getX());
            int j = MathHelper.floor(this.getY() - 0.20000000298023224D);
            int k = MathHelper.floor(this.getZ());
            BlockState blockState = this.world.getBlockState(new BlockPos(i, j, k));
            if (!blockState.isAir()) {
                this.world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), this.getX() + ((double) this.random.nextFloat() - 0.5D) * (double) this.getWidth(), this.getY() + 0.1D, this.getZ() + ((double) this.random.nextFloat() - 0.5D) * (double) this.getWidth(), 4.0D * ((double) this.random.nextFloat() - 0.5D), 0.5D, ((double) this.random.nextFloat() - 0.5D) * 4.0D);
            }
        }

        if (!this.world.isClient) {
            this.tickAngerLogic((ServerWorld) this.world, true);
        }

    }

    @Override
    public boolean canTarget(EntityType<?> type) {
        if (this.isPlayerCreated() && type == EntityType.PLAYER) {
            return false;
        } else {
            return type != EntityType.CREEPER && super.canTarget(type);
        }
    }

    public float getAttackDamage() {
        return (float) this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
    }

    @Override
    public boolean tryAttack(Entity target) {
        this.attackTicksLeft = 10;
        this.world.sendEntityStatus(this, (byte) 4);
        float attackDamage = this.getAttackDamage();
        float randomizedAttackDamage = (int) attackDamage > 0 ? attackDamage / 2.0F + (float) this.random.nextInt((int) attackDamage) : attackDamage;
        boolean bl = target.damage(DamageSource.mob(this), randomizedAttackDamage);
        if (bl) {

            target.setVelocity(target.getVelocity().add(0.0D, 0.4000000059604645D, 0.0D));
            this.dealDamage(this, target);
        }

        this.playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        return bl;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        Crack crack = this.getCrack();
        boolean bl = super.damage(source, amount);
        if (bl && this.getCrack() != crack) {
            this.playSound(SoundEvents.ENTITY_IRON_GOLEM_DAMAGE, 1.0F, 1.0F);
        }

        return bl;
    }

    public Crack getCrack() {
        return Crack.from(this.getHealth() / this.getMaxHealth());
    }

    @Environment(EnvType.CLIENT)
    public void handleStatus(byte status) {
        switch (status) {
            case 4:
                this.attackTicksLeft = 10;
                this.playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0F, 1.0F);
                break;
            case 11:
                this.lookingAtVillagerTicksLeft = 400;
                break;
            case 34:
                this.lookingAtVillagerTicksLeft = 0;
                break;
            default:
                super.handleStatus(status);
                break;
        }
    }

    @Environment(EnvType.CLIENT)
    public int getAttackTicksLeft() {
        return this.attackTicksLeft;
    }

    public void setLookingAtVillager(boolean lookingAtVillager) {
        if (lookingAtVillager) {
            this.lookingAtVillagerTicksLeft = 400;
            this.world.sendEntityStatus(this, (byte) 11);
        } else {
            this.lookingAtVillagerTicksLeft = 0;
            this.world.sendEntityStatus(this, (byte) 34);
        }

    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_IRON_GOLEM_DEATH;
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putBoolean("PlayerCreated", this.isPlayerCreated());
        tag.putInt("Type", getGolemType().rawId);
        this.angerToTag(tag);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setPlayerCreated(tag.getBoolean("PlayerCreated"));
        this.setGolemType(Type.fromId(tag.getInt("Type")));
        this.angerFromTag((ServerWorld) this.world, tag);
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        Item handItem = player.getStackInHand(hand).getItem();

        if (handItem.equals(this.getHealItem())) {
            float f = this.getHealth();
            this.heal(25.0F);
            if (this.getHealth() == f) {
                return ActionResult.PASS;
            } else {
                float g = 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
                this.playSound(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 1.0F, g);
                if (!player.abilities.creativeMode) {
                    player.getStackInHand(hand).decrement(1);
                }

                return ActionResult.success(this.world.isClient);
            }

        } else if (handItem == Items.GLASS_BOTTLE) {
            this.damage(DamageSource.player(player), this.getMaxHealth());
            player.getStackInHand(hand).decrement(1);
            player.giveItemStack(new ItemStack(ModItems.GOLEM_SOUL));
            return ActionResult.PASS;
        } else {
            return ActionResult.PASS;
        }
    }

    protected Item getHealItem() {
        return this.getGolemType().item;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_IRON_GOLEM_STEP, 1.0F, 1.0F);
    }

    @Environment(EnvType.CLIENT)
    public int getLookingAtVillagerTicks() {
        return this.lookingAtVillagerTicksLeft;
    }

    public boolean isPlayerCreated() {
        return (this.dataTracker.get(PLAYER_CREATED) & 1) != 0;
    }

    public void setPlayerCreated(boolean playerCreated) {
        byte b = this.dataTracker.get(PLAYER_CREATED);
        if (playerCreated) {
            this.dataTracker.set(PLAYER_CREATED, (byte) (b | 1));
        } else {
            this.dataTracker.set(PLAYER_CREATED, (byte) (b & -2));
        }
    }

    public Type getGolemType() {
        return Type.fromItemStack(this.dataTracker.get(TYPE_TRACKER));
    }

    public void setGolemType(Type golemType) {
        this.dataTracker.set(TYPE_TRACKER, new ItemStack(golemType.item));
    }

    @Override
    public boolean canSpawn(WorldView world) {
        BlockPos blockPos = this.getBlockPos();
        BlockPos blockPos2 = blockPos.down();
        BlockState blockState = world.getBlockState(blockPos2);
        if (!blockState.hasSolidTopSurface(world, blockPos2, this)) {
            return false;
        } else {
            for (int i = 1; i < 3; ++i) {
                BlockPos blockPos3 = blockPos.up(i);
                BlockState blockState2 = world.getBlockState(blockPos3);
                if (!SpawnHelper.isClearForSpawn(world, blockPos3, blockState2, blockState2.getFluidState(), EntityType.IRON_GOLEM)) {
                    return false;
                }
            }

            return SpawnHelper.isClearForSpawn(world, blockPos, world.getBlockState(blockPos), Fluids.EMPTY.getDefaultState(), EntityType.IRON_GOLEM) && world.intersectsEntities(this);
        }
    }

    @Override
    public void chooseRandomAngerTime() {
        this.angerTicks = randomIntDuration.choose(this.random);
    }

    @Override
    public int getAngerTime() {
        return this.angerTicks;
    }

    @Override
    public void setAngerTime(int ticks) {
        this.angerTicks = ticks;
    }

    @Override
    public UUID getAngryAt() {
        return this.angryAt;
    }

    @Override
    public void setAngryAt(@Nullable UUID uuid) {
        this.angryAt = uuid;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Vec3d method_29919() {
        return new Vec3d(0.0D, 0.875F * this.getStandingEyeHeight(), this.getWidth() * 0.4F);
    }
}