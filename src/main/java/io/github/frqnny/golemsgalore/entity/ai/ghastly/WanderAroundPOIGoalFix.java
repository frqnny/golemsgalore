package io.github.frqnny.golemsgalore.entity.ai.ghastly;

import net.minecraft.entity.ai.goal.WanderAroundPointOfInterestGoal;
import net.minecraft.entity.mob.PathAwareEntity;

public class WanderAroundPOIGoalFix extends WanderAroundPointOfInterestGoal {
    public WanderAroundPOIGoalFix(PathAwareEntity pathAwareEntity, double d, boolean bl) {
        super(pathAwareEntity, d, bl);
    }

    public boolean shouldContinue() {
        return this.mob.getMoveControl().isMoving() && !this.mob.hasPassengers();
    }

    public void start() {
        this.mob.getMoveControl().moveTo(this.targetX, this.targetY, this.targetZ, this.speed);
    }

    public void stop() {
        this.mob.getNavigation().stop();
        super.stop();
    }
}
