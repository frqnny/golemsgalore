package io.github.frqnny.golemsgalore.entity.ai;

import io.github.frqnny.golemsgalore.entity.ModGolemEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.VillagerEntity;

import java.util.EnumSet;

public class GolemLookGoal extends Goal {
    private static final TargetPredicate CLOSE_VILLAGER_PREDICATE = TargetPredicate.createNonAttackable().setBaseMaxDistance(6.0D);
    private final ModGolemEntity golem;
    private VillagerEntity targetVillager;
    private int lookCountdown;

    public GolemLookGoal(ModGolemEntity golem) {
        this.golem = golem;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    public boolean canStart() {
        if (!this.golem.world.isDay()) {
            return false;
        } else if (this.golem.getRandom().nextInt(8000) != 0) {
            return false;
        } else {
            this.targetVillager = this.golem.world.getClosestEntity(VillagerEntity.class, CLOSE_VILLAGER_PREDICATE, this.golem, this.golem.getX(), this.golem.getY(), this.golem.getZ(), this.golem.getBoundingBox().expand(6.0D, 2.0D, 6.0D));
            return this.targetVillager != null;
        }
    }

    public boolean shouldContinue() {
        return this.lookCountdown > 0;
    }

    public void start() {
        this.lookCountdown = 400;
        this.golem.setLookingAtVillager(true);
    }

    public void stop() {
        this.golem.setLookingAtVillager(false);
        this.targetVillager = null;
    }

    public void tick() {
        this.golem.getLookControl().lookAt(this.targetVillager, 30.0F, 30.0F);
        --this.lookCountdown;
    }
}