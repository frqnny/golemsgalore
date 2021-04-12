package io.github.frqnny.golemsgalore.entity.ai.ghastly;

import io.github.frqnny.golemsgalore.entity.GhastlyGolemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.goal.Goal;

public class PumpkingProjectileAttack<T extends GhastlyGolemEntity & RangedAttackMob> extends Goal {
    private final T actor;
    private int counter;

    public PumpkingProjectileAttack(T actor) {
        this.actor = actor;
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
        this.counter = 40;
    }

    public void stop() {
        super.stop();
        this.actor.setAttacking(false);
    }

    public void tick() {
        LivingEntity livingEntity = this.actor.getTarget();
        if (livingEntity != null) {
            LookControl lookControl = this.actor.getLookControl();
            lookControl.lookAt(livingEntity, 90F, 90F);
            if (counter <= 0) {
                ((RangedAttackMob) actor).attack(livingEntity, 0);
                this.counter = 40;
            } else {
                counter--;
            }

        }
    }
}
