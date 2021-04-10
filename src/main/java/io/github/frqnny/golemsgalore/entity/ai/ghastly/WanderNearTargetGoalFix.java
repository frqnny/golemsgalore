package io.github.frqnny.golemsgalore.entity.ai.ghastly;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class WanderNearTargetGoalFix extends Goal {
    private final PathAwareEntity mob;
    private final double speed;
    private final float maxDistance;
    private LivingEntity target;
    private double x;
    private double y;
    private double z;

    public WanderNearTargetGoalFix(PathAwareEntity mob, double speed, float maxDistance) {
        this.mob = mob;
        this.speed = speed;
        this.maxDistance = maxDistance;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    public boolean canStart() {
        this.target = this.mob.getTarget();
        if (this.target == null) {
            return false;
        } else if (this.target.squaredDistanceTo(this.mob) > (double) (this.maxDistance * this.maxDistance)) {
            return false;
        } else {
            Vec3d vec3d = TargetFinder.findTargetTowards(this.mob, 16, 7, this.target.getPos());
            if (vec3d == null) {
                return false;
            } else {
                this.x = vec3d.x;
                this.y = vec3d.y;
                this.z = vec3d.z;
                return true;
            }
        }
    }

    public boolean shouldContinue() {
        return this.mob.getMoveControl().isMoving() && this.target.isAlive() && this.target.squaredDistanceTo(this.mob) < (double) (this.maxDistance * this.maxDistance);
    }

    public void stop() {
        this.mob.getNavigation().stop();
        this.target = null;

    }

    public void start() {
        this.mob.getMoveControl().moveTo(this.x, this.y, this.z, this.speed);
    }
}
