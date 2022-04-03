package io.github.frqnny.golemsgalore.entity;

import io.github.frqnny.golemsgalore.entity.ai.ghastly.FlyAroundPOIGoal;
import io.github.frqnny.golemsgalore.entity.ai.ghastly.GhastlyGolemFlyAroundGoal;
import io.github.frqnny.golemsgalore.entity.ai.ghastly.PumpkingProjectileAttack;
import io.github.frqnny.golemsgalore.entity.ai.ghastly.TrackGhastlyGolemTargetGoal;
import io.github.frqnny.golemsgalore.entity.projectile.PumpkinProjectileEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.EnumSet;

public class GhastlyGolemEntity extends ModGolemEntity implements RangedAttackMob {

    public GhastlyGolemEntity(EntityType<? extends IronGolemEntity> entityType, World world) {
        super(entityType, world);
        moveControl = new GhostMoveControl(this);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new PumpkingProjectileAttack<>(this));
        this.goalSelector.add(2, new GhastlyGolemEntity.LookAtTargetGoal());

        this.goalSelector.add(3, new FlyAroundPOIGoal(this, 0.6D, false));
        this.goalSelector.add(5, new GhastlyGolemFlyAroundGoal(this, 0.6D));


        this.goalSelector.add(9, new IronGolemLookGoal(this));

        this.targetSelector.add(1, new TrackGhastlyGolemTargetGoal(this));
        this.targetSelector.add(2, new RevengeGoal(this));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, MobEntity.class, 5, false, false, (livingEntity) -> livingEntity instanceof Monster && !(livingEntity instanceof CreeperEntity )  && !(livingEntity instanceof EndermanEntity)));
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
        //this.noClip = true;
        super.tick();
        //this.goalSelector.getRunningGoals().forEach((goalOptional) -> System.out.println(goalOptional.getGoal()));

        this.setNoGravity(true);


    }

    @Override
    public void attack(LivingEntity target, float pullProgress) {
        this.world.spawnEntity(new PumpkinProjectileEntity(this.world, this, this.getTarget()));

    }

    private class GhostMoveControl extends MoveControl {
        public GhostMoveControl(MobEntity entity) {
            super(entity);
        }

        @Override
        public void tick() {
            if (this.state == State.MOVE_TO) {
                Vec3d vec3d = new Vec3d(this.targetX - GhastlyGolemEntity.this.getX(), this.targetY - GhastlyGolemEntity.this.getY(), this.targetZ - GhastlyGolemEntity.this.getZ());
                double d = vec3d.length();
                //quickly get out of the way
                if (d < GhastlyGolemEntity.this.getBoundingBox().getAverageSideLength()) {
                    this.state = State.WAIT;
                    GhastlyGolemEntity.this.setVelocity(GhastlyGolemEntity.this.getVelocity().multiply(0.5D));
                } else {
                    GhastlyGolemEntity.this.setVelocity(GhastlyGolemEntity.this.getVelocity().add(vec3d.multiply(this.speed * 0.07D / d)));
                    if (GhastlyGolemEntity.this.getTarget() == null) {
                        Vec3d velocity = GhastlyGolemEntity.this.getVelocity();
                        GhastlyGolemEntity.this.setYaw(-((float) MathHelper.atan2(velocity.x, velocity.z)) * 57.295776F);

                        int topY = GhastlyGolemEntity.this.getWorld().getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, GhastlyGolemEntity.this.getBlockX(), GhastlyGolemEntity.this.getBlockZ());
                        int entityY = GhastlyGolemEntity.this.getBlockY();
                        if (entityY - topY > 7) {
                            GhastlyGolemEntity.this.setVelocity(GhastlyGolemEntity.this.getVelocity().add(new Vec3d(0, -0.1, 0)));
                        }
                    } else {
                        double e = GhastlyGolemEntity.this.getTarget().getX() - GhastlyGolemEntity.this.getX();
                        double f = GhastlyGolemEntity.this.getTarget().getZ() - GhastlyGolemEntity.this.getZ();
                        GhastlyGolemEntity.this.setYaw(-((float) MathHelper.atan2(e, f)) * 57.295776F);
                    }
                    GhastlyGolemEntity.this.bodyYaw = GhastlyGolemEntity.this.getYaw();
                }

            }
        }


    }

    private class LookAtTargetGoal extends Goal {
        public LookAtTargetGoal() {
            this.setControls(EnumSet.of(Control.MOVE));
        }

        public boolean canStart() {
            return !GhastlyGolemEntity.this.getMoveControl().isMoving() && GhastlyGolemEntity.this.random.nextInt(toGoalTicks(7)) == 0;
        }

        public boolean shouldContinue() {
            return false;
        }

        public void tick() {
            BlockPos blockPos = GhastlyGolemEntity.this.getBlockPos();

            for (int i = 0; i < 3; ++i) {
                BlockPos blockPos2 = blockPos.add(GhastlyGolemEntity.this.random.nextInt(15) - 7, GhastlyGolemEntity.this.random.nextInt(11) - 5, GhastlyGolemEntity.this.random.nextInt(15) - 7);
                if (GhastlyGolemEntity.this.world.isAir(blockPos2)) {
                    GhastlyGolemEntity.this.moveControl.moveTo((double) blockPos2.getX() + 0.5D, (double) blockPos2.getY() + 0.5D, (double) blockPos2.getZ() + 0.5D, 0.25D);
                    if (GhastlyGolemEntity.this.getTarget() == null) {
                        GhastlyGolemEntity.this.getLookControl().lookAt((double) blockPos2.getX() + 0.5D, (double) blockPos2.getY() + 0.5D, (double) blockPos2.getZ() + 0.5D, 180.0F, 20.0F);
                    }
                    break;
                }
            }

        }
    }
}
