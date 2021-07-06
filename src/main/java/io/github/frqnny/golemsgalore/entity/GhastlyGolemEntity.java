package io.github.frqnny.golemsgalore.entity;

import io.github.frqnny.golemsgalore.entity.ai.ghastly.IronGolemWanderAroundGoalFix;
import io.github.frqnny.golemsgalore.entity.ai.ghastly.PumpkingProjectileAttack;
import io.github.frqnny.golemsgalore.entity.ai.ghastly.WanderAroundPOIGoalFix;
import io.github.frqnny.golemsgalore.entity.ai.ghastly.WanderNearTargetGoalFix;
import io.github.frqnny.golemsgalore.entity.projectile.PumpkinProjectileEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GhastlyGolemEntity extends ModGolemEntity implements RangedAttackMob {

    public GhastlyGolemEntity(EntityType<? extends IronGolemEntity> entityType, World world) {
        super(entityType, world);
        moveControl = new GhostMoveControl(this);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new PumpkingProjectileAttack<>(this));
        //this.goalSelector.add(1, new ProjectileAttackGoal(0.2, ));
        //this.goalSelector.add(2, new FlyRandomlyGoal());
        this.goalSelector.add(2, new WanderNearTargetGoalFix(this, 0.9D, 32.0F));
        this.goalSelector.add(2, new WanderAroundPOIGoalFix(this, 0.6D, false));
        this.goalSelector.add(4, new IronGolemWanderAroundGoalFix(this, 0.6D));
        this.goalSelector.add(5, new IronGolemLookGoal(this));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackIronGolemTargetGoal(this));
        this.targetSelector.add(2, new RevengeGoal(this));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, MobEntity.class, 5, false, false, (livingEntity) -> livingEntity instanceof Monster && !(livingEntity instanceof CreeperEntity)));
        this.targetSelector.add(4, new UniversalAngerGoal<>(this, false));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound tag) {
        super.writeCustomDataToNbt(tag);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound tag) {
        super.readCustomDataFromNbt(tag);
    }

    @Override
    public void tick() {
        this.setNoGravity(true);
        //this.noClip = true;
        super.tick();

        this.isAlive();

    }

    @Override
    public void attack(LivingEntity target, float pullProgress) {
        this.world.spawnEntity(new PumpkinProjectileEntity(this.world, this, this.getTarget()));

    }

    private static class GhostMoveControl extends MoveControl {
        public GhostMoveControl(MobEntity entity) {
            super(entity);
        }

        @Override
        public void tick() {
            if (state == MoveControl.State.MOVE_TO) {
                Vec3d vec3d = new Vec3d(this.targetX - entity.getX(), this.targetY - entity.getY(), this.targetZ - entity.getZ());
                double d = vec3d.length();
                if (d < entity.getBoundingBox().getAverageSideLength()) {
                    this.state = MoveControl.State.WAIT;
                    entity.setVelocity(entity.getVelocity().multiply(0.5D));
                } else {
                    entity.setVelocity(entity.getVelocity().add(vec3d.multiply(this.speed * 0.05D / d)));
                    if (entity.getTarget() == null) {
                        Vec3d vec3d2 = entity.getVelocity();
                        entity.setYaw(-((float) MathHelper.atan2(vec3d2.x, vec3d2.z)) * 57.295776F);
                    } else {
                        double e = entity.getTarget().getX() - entity.getX();
                        double f = entity.getTarget().getZ() - entity.getZ();
                        entity.setYaw(-((float) MathHelper.atan2(e, f)) * 57.295776F);
                    }
                    entity.bodyYaw = entity.getYaw();
                }

            } else if (this.state == MoveControl.State.STRAFE) {
                float q;
                float f = (float) this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
                float g = (float) this.speed * f;
                float h = this.forwardMovement;
                float i = this.sidewaysMovement;
                float j = MathHelper.sqrt(h * h + i * i);
                if (j < 1.0F) {
                    j = 1.0F;
                }

                j = g / j;
                h *= j;
                i *= j;
                float k = MathHelper.sin(this.entity.getYaw() * 0.017453292F);
                float l = MathHelper.cos(this.entity.getYaw() * 0.017453292F);
                float m = h * l - i * k;
                q = i * l + h * k;
                if (!this.method_25946(m, q)) {
                    this.forwardMovement = 1.0F;
                    this.sidewaysMovement = 0.0F;
                }

                this.entity.setMovementSpeed(g);
                this.entity.setForwardSpeed(this.forwardMovement);
                this.entity.setSidewaysSpeed(this.sidewaysMovement);
                this.state = MoveControl.State.WAIT;
            }
        }

        private boolean method_25946(float f, float g) {
            EntityNavigation entityNavigation = this.entity.getNavigation();
            if (entityNavigation != null) {
                PathNodeMaker pathNodeMaker = entityNavigation.getNodeMaker();
                return pathNodeMaker == null || pathNodeMaker.getDefaultNodeType(this.entity.world, MathHelper.floor(this.entity.getX() + (double) f), MathHelper.floor(this.entity.getY()), MathHelper.floor(this.entity.getZ() + (double) g)) == PathNodeType.WALKABLE;
            }

            return true;
        }
    }
}
