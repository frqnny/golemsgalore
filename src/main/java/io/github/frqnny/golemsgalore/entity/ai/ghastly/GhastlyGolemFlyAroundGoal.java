package io.github.frqnny.golemsgalore.entity.ai.ghastly;

import net.minecraft.entity.ai.goal.IronGolemWanderAroundGoal;
import net.minecraft.entity.mob.PathAwareEntity;

//Uses move control instead of navigation
public class GhastlyGolemFlyAroundGoal extends IronGolemWanderAroundGoal {
    public GhastlyGolemFlyAroundGoal(PathAwareEntity pathAwareEntity, double d) {
        super(pathAwareEntity, d);
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
