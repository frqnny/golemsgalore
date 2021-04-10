package io.github.frqnny.golemsgalore.entity;

import io.github.frqnny.golemsgalore.entity.ai.GolemLookGoal;
import io.github.frqnny.golemsgalore.entity.ai.TrackGolemTargetGoal;
import io.github.frqnny.golemsgalore.entity.ai.ghastly.BowAttackGoal;
import io.github.frqnny.golemsgalore.entity.ai.ghastly.IronGolemWanderAroundGoalFix;
import io.github.frqnny.golemsgalore.entity.ai.ghastly.WanderAroundPOIGoalFix;
import io.github.frqnny.golemsgalore.entity.ai.ghastly.WanderNearTargetGoalFix;
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
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GhastlyGolemEntity extends ModGolemEntity implements RangedAttackMob {

    public GhastlyGolemEntity(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
        moveControl = new GhostMoveControl(this);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new BowAttackGoal<>(this, 1.0D, 20, 15.0F));
        //this.goalSelector.add(1, new ProjectileAttackGoal(0.2, ));
        //this.goalSelector.add(2, new FlyRandomlyGoal());
        this.goalSelector.add(2, new WanderNearTargetGoalFix(this, 0.9D, 32.0F));
        this.goalSelector.add(2, new WanderAroundPOIGoalFix(this, 0.6D, false));
        this.goalSelector.add(4, new IronGolemWanderAroundGoalFix(this, 0.6D));
        this.goalSelector.add(5, new GolemLookGoal(this));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackGolemTargetGoal(this));
        this.targetSelector.add(2, new RevengeGoal(this));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, MobEntity.class, 5, false, false, (livingEntity) -> livingEntity instanceof Monster && !(livingEntity instanceof CreeperEntity)));
        this.targetSelector.add(4, new UniversalAngerGoal<>(this, false));
    }

    @Override
    protected Item getHealItem() {
        return super.getHealItem();
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
    }

    @Override
    public void tick() {
        this.setNoGravity(true);
        //this.noClip = true;
        super.tick();

        this.goalSelector.getRunningGoals().forEach(goal -> System.out.println(goal.getGoal().toString()));
        System.out.println();
    }

    @Override
    public void attack(LivingEntity target, float pullProgress) {
        ItemStack stack = new ItemStack(Items.SPECTRAL_ARROW);
        PersistentProjectileEntity persistentProjectileEntity = this.createArrowProjectile(stack, pullProgress);
        double d = target.getX() - this.getX();
        double e = target.getBodyY(0.3333333333333333D) - persistentProjectileEntity.getY();
        double f = target.getZ() - this.getZ();
        double g = MathHelper.sqrt(d * d + f * f);
        persistentProjectileEntity.setVelocity(d, e + g * 0.20000000298023224D, f, 1.6F, (float) (14 - (this.world.getDifficulty().getId() << 2)));
        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.world.spawnEntity(persistentProjectileEntity);
    }

    protected PersistentProjectileEntity createArrowProjectile(ItemStack arrow, float damageModifier) {
        return ProjectileUtil.createArrowProjectile(this, arrow, damageModifier);
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
                        entity.yaw = -((float) MathHelper.atan2(vec3d2.x, vec3d2.z)) * 57.295776F;
                    } else {
                        double e = entity.getTarget().getX() - entity.getX();
                        double f = entity.getTarget().getZ() - entity.getZ();
                        entity.yaw = -((float) MathHelper.atan2(e, f)) * 57.295776F;
                    }
                    entity.bodyYaw = entity.yaw;
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
                float k = MathHelper.sin(this.entity.yaw * 0.017453292F);
                float l = MathHelper.cos(this.entity.yaw * 0.017453292F);
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
