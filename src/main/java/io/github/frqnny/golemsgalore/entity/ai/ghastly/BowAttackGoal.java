package io.github.frqnny.golemsgalore.entity.ai.ghastly;

import io.github.frqnny.golemsgalore.entity.GhastlyGolemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.BowItem;

import java.util.EnumSet;

public class BowAttackGoal<T extends GhastlyGolemEntity & RangedAttackMob> extends Goal {
    private final T actor;
    private final double speed;
    private final float squaredRange;
    private final int attackInterval;
    private final int combatTicks = -1;
    private int cooldown = -1;
    private int targetSeeingTicker;
    private boolean movingToLeft;
    private boolean backward;

    public BowAttackGoal(T actor, double speed, int attackInterval, float range) {
        this.actor = actor;
        this.speed = speed;
        this.attackInterval = attackInterval;
        this.squaredRange = range * range;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }


    public boolean canStart() {
        return this.actor.getTarget() != null;
    }


    public boolean shouldContinue() {
        return (this.canStart() || !this.actor.getNavigation().isIdle());
    }

    public void start() {
        super.start();
        this.actor.setAttacking(true);
    }

    public void stop() {
        super.stop();
        this.actor.setAttacking(false);
        this.targetSeeingTicker = 0;
        this.cooldown = -1;
    }

    public void tick() {
        LivingEntity livingEntity = this.actor.getTarget();
        if (livingEntity != null) {
            ((RangedAttackMob) this.actor).attack(livingEntity, BowItem.getPullProgress(20));

        }
    }
}
