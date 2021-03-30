package io.github.frqnny.golemsgalore.entity.ai.laser;

import io.github.frqnny.golemsgalore.entity.LaserGolemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.Difficulty;

import java.util.EnumSet;

public class FireLaserGoal extends Goal {
    protected final LaserGolemEntity golem;
    protected int beamTicks;

    public FireLaserGoal(LaserGolemEntity mob) {
        this.golem = mob;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    public boolean canStart() {
        LivingEntity livingEntity = this.golem.getTarget();
        return livingEntity != null && livingEntity.isAlive();
    }

    @Override
    public void start() {
        this.beamTicks = -10;
        this.golem.getNavigation().stop();
        this.golem.getLookControl().lookAt(this.golem.getTarget(), 90.0F, 90.0F);
        this.golem.velocityDirty = true;
    }

    public void stop() {
        this.golem.setBeamTarget(0);
        this.golem.setTarget(null);
    }

    public boolean shouldContinue() {
        return super.shouldContinue() && this.golem.squaredDistanceTo(this.golem.getTarget()) > 1.0D;
    }

    public void tick() {
        LivingEntity target = this.golem.getTarget();
        this.golem.getNavigation().stop();
        this.golem.getLookControl().lookAt(target, 90.0F, 90.0F);
        if (!this.golem.canSee(target)) {
            this.golem.setTarget(null);
        } else {
            ++this.beamTicks;
            if (this.beamTicks == 0) {
                this.golem.setBeamTarget(this.golem.getTarget().getEntityId());
                if (!this.golem.isSilent()) {
                    this.golem.world.sendEntityStatus(this.golem, (byte) 4);
                }
            } else if (this.beamTicks >= 20) {
                float f = 1.0F;
                if (this.golem.world.getDifficulty() == Difficulty.HARD) {
                    f -= 0.5F;
                }

                target.setOnFireFor(5);
                target.damage(DamageSource.magic(this.golem, this.golem), f);
                target.damage(DamageSource.mob(this.golem), (float) this.golem.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE));
                this.golem.setTarget(null);
            }

            super.tick();
        }
    }
}
