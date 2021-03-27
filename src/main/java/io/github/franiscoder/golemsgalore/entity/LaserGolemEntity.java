package io.github.franiscoder.golemsgalore.entity;

import io.github.franiscoder.golemsgalore.GolemsGalore;
import io.github.franiscoder.golemsgalore.entity.ai.GolemLookGoal;
import io.github.franiscoder.golemsgalore.entity.ai.TrackGolemTargetGoal;
import io.github.franiscoder.golemsgalore.entity.ai.laser.FireLaserGoal;
import io.github.franiscoder.golemsgalore.entity.ai.laser.TrackLaserGolemTargetGoal;
import io.github.franiscoder.golemsgalore.init.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;


public class LaserGolemEntity extends ModGolemEntity {
    protected static final TrackedData<Boolean> IS_FIRING = DataTracker.registerData(LaserGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> BEAM_TARGET_ID = DataTracker.registerData(LaserGolemEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private final boolean renderLaserFlames = GolemsGalore.getConfig().renderLaserFlames;
    private LivingEntity cachedBeamTarget;
    private int beamTicks;

    public LaserGolemEntity(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
    }

    public static int getWarmupTime() {
        return 20;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new FireLaserGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.add(2, new WanderNearTargetGoal(this, 0.9D, 32.0F));
        this.goalSelector.add(2, new WanderAroundPointOfInterestGoal(this, 0.6D, false));
        this.goalSelector.add(3, new MoveThroughVillageGoal(this, 0.6D, false, 4, () -> false));
        this.goalSelector.add(5, new GolemLookGoal(this));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.6D));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackLaserGolemTargetGoal<>(this, LivingEntity.class, 10, true, false,
                (livingEntity) -> livingEntity instanceof Monster && !(livingEntity instanceof CreeperEntity)));
        this.targetSelector.add(2, new RevengeGoal(this));
        this.targetSelector.add(2, new TrackGolemTargetGoal(this));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(IS_FIRING, false);
        this.dataTracker.startTracking(BEAM_TARGET_ID, 0);
    }

    @Override
    public void handleStatus(byte status) {
        if (status == 4) {
            this.attackTicksLeft = 10;
            this.playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        } else {
            super.handleStatus(status);
        }
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        Item handItem = player.getStackInHand(hand).getItem();

        if (handItem != Items.IRON_INGOT) {
            return ActionResult.PASS;
        } else if (handItem == Items.GLASS_BOTTLE) {
            this.damage(DamageSource.player(player), this.getMaxHealth());
            player.setStackInHand(hand, new ItemStack(ModItems.GOLEM_SOUL));
            return ActionResult.PASS;
        } else {
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
        }
    }


    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putBoolean("IsFiring", getIsFiring());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setIsFiring(tag.getBoolean("IsFiring"));
    }

    public boolean getIsFiring() {
        return dataTracker.get(IS_FIRING);
    }

    protected void setIsFiring(boolean isFiring) {
        dataTracker.set(IS_FIRING, isFiring);
    }

    @Nullable
    public LivingEntity getBeamTarget() {
        if (!this.hasBeamTarget()) {
            return null;
        } else if (this.world.isClient) {
            if (this.cachedBeamTarget != null) {
                return this.cachedBeamTarget;
            } else {
                Entity entity = this.world.getEntityById(this.dataTracker.get(BEAM_TARGET_ID));
                if (entity instanceof LivingEntity) {
                    this.cachedBeamTarget = (LivingEntity) entity;
                    return this.cachedBeamTarget;
                } else {
                    return null;
                }
            }
        } else {
            return this.getTarget();
        }
    }


    public void setBeamTarget(int progress) {
        this.dataTracker.set(BEAM_TARGET_ID, progress);
    }

    public void tickMovement() {
        if (this.isAlive()) {
            if (this.world.isClient) {
                if (this.hasBeamTarget()) {
                    if (this.beamTicks < getWarmupTime()) {
                        ++this.beamTicks;
                    }

                    LivingEntity target = this.getBeamTarget();
                    if (target != null) {
                        this.getLookControl().lookAt(target, 90.0F, 90.0F);
                        this.getLookControl().tick();
                        double d = this.getBeamProgress(0.0F);
                        double e = target.getX() - this.getX();
                        double f = target.getBodyY(0.5D) - this.getEyeY();
                        double g = target.getZ() - this.getZ();
                        double h = Math.sqrt(e * e + f * f + g * g);
                        e /= h;
                        f /= h;
                        g /= h;
                        double j = this.random.nextDouble();

                        while (j < h) {
                            j += 1.8D - d + this.random.nextDouble() * (1.7D - d);

                            if (random.nextBoolean() && this.renderLaserFlames) {
                                this.world.addParticle(ParticleTypes.FLAME, this.getX() + e * j, this.getEyeY() + f * j, this.getZ() + g * j, 0.0D, 0.0D, 0.0D);
                            }
                        }
                    }
                }
            }

            if (this.hasBeamTarget()) {
                this.yaw = this.headYaw;
            }
            if (!this.world.isClient) {
                this.tickAngerLogic((ServerWorld) this.world, true);
            }
        }

        super.tickMovement();
    }


    public boolean hasBeamTarget() {
        return this.dataTracker.get(BEAM_TARGET_ID) != 0;
    }

    public float getBeamProgress(float tickDelta) {
        return ((float) this.beamTicks + tickDelta) / (float) getWarmupTime();
    }

    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (BEAM_TARGET_ID.equals(data)) {
            this.beamTicks = 0;
            this.cachedBeamTarget = null;
        }

    }
}
