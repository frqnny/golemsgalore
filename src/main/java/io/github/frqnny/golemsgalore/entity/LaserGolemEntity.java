package io.github.frqnny.golemsgalore.entity;

import io.github.frqnny.golemsgalore.GolemsGalore;
import io.github.frqnny.golemsgalore.entity.ai.GolemLookGoal;
import io.github.frqnny.golemsgalore.entity.ai.TrackGolemTargetGoal;
import io.github.frqnny.golemsgalore.entity.ai.laser.FireLaserGoal;
import io.github.frqnny.golemsgalore.entity.ai.laser.TrackLaserGolemTargetGoal;
import io.github.frqnny.golemsgalore.init.ModParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
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

        this.goalSelector.add(1, new FireLaserGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.add(2, new WanderNearTargetGoal(this, 0.9D, 32.0F));
        this.goalSelector.add(2, new WanderAroundPointOfInterestGoal(this, 0.6D, false));
        this.goalSelector.add(4, new IronGolemWanderAroundGoal(this, 0.6D));
        this.goalSelector.add(5, new GolemLookGoal(this));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackGolemTargetGoal(this));
        this.targetSelector.add(2, new RevengeGoal(this));
        this.targetSelector.add(1, new TrackLaserGolemTargetGoal<>(this, LivingEntity.class, 10, true, false,
                (livingEntity) -> livingEntity instanceof Monster && !(livingEntity instanceof CreeperEntity)));
        this.targetSelector.add(2, new RevengeGoal(this));
        this.targetSelector.add(2, new TrackGolemTargetGoal(this));
        this.targetSelector.add(4, new UniversalAngerGoal<>(this, false));
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
    protected Item getHealItem() {
        return Items.IRON_INGOT;
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

    @Override
    public void tickMovement() {
        if (this.isAlive()) {
            if (this.world.isClient) {
                if (this.hasBeamTarget()) {
                    if (this.beamTicks < 20) {
                        ++this.beamTicks;
                    }

                    LivingEntity target = this.getBeamTarget();
                    if (target != null) {
                        this.getLookControl().lookAt(target, 90.0F, 90.0F);
                        this.getLookControl().tick();
                        double beamProgress = this.getBeamProgress(0.0F);
                        double distanceX = target.getX() - this.getX();
                        double distanceY = target.getBodyY(0.5D) - this.getEyeY();
                        double distanceZ = target.getZ() - this.getZ();
                        double length = Math.sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ);
                        distanceX /= length;
                        distanceY /= length;
                        distanceZ /= length;
                        double j = this.random.nextDouble();

                        Vec3d vec = target.getPos().subtract(this.getPos()).normalize();

                        float speed = 0.25F;
                        while (j < length) {
                            j += 1.8D - beamProgress + this.random.nextDouble() * (1.7D - beamProgress);

                            if (this.renderLaserFlames) {
                                this.world.addParticle(
                                        ModParticles.LASER,
                                        this.getX() + distanceX * j,
                                        this.getEyeY() + distanceY * j,
                                        this.getZ() + distanceZ * j,
                                        vec.getX() * speed,
                                        vec.getY() * speed,
                                        vec.getZ() * speed
                                );
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
        return ((float) this.beamTicks + tickDelta) / (float) 20;
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (BEAM_TARGET_ID.equals(data)) {
            this.beamTicks = 0;
            this.cachedBeamTarget = null;
        }

    }
}
