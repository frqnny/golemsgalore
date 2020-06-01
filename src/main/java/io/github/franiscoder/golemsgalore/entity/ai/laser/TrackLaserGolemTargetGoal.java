package io.github.franiscoder.golemsgalore.entity.ai.laser;


import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Predicate;

public class TrackLaserGolemTargetGoal<T extends LivingEntity> extends TrackTargetGoal {
    protected final Class<T> targetClass;
    protected final int reciprocalChance;
    protected final TargetPredicate targetPredicate;
    protected LivingEntity targetEntity;

    public TrackLaserGolemTargetGoal(MobEntity mob, Class<T> targetClass, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate, @Nullable Predicate<LivingEntity> targetPredicate) {
        super(mob, checkVisibility, checkCanNavigate);
        this.targetClass = targetClass;
        this.reciprocalChance = reciprocalChance;
        this.setControls(EnumSet.of(Control.LOOK, Control.TARGET));
        this.targetPredicate = (new TargetPredicate()).setBaseMaxDistance(this.getFollowRange()).setPredicate(targetPredicate);
    }

    public boolean canStart() {
        if (this.reciprocalChance > 0 && this.mob.getRandom().nextInt(this.reciprocalChance) != 0) {
            return false;
        } else {
            this.findClosestTarget();
            return this.targetEntity != null;
        }
    }

    protected Box getSearchBox(double distance) {
        return this.mob.getBoundingBox().expand(distance, 4.0D, distance);
    }

    protected void findClosestTarget() {
        if (this.targetClass != PlayerEntity.class && this.targetClass != ServerPlayerEntity.class) {
            this.targetEntity = this.mob.world.getClosestEntityIncludingUngeneratedChunks(this.targetClass, this.targetPredicate, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ(), this.getSearchBox(this.getFollowRange()));
        } else {
            this.targetEntity = this.mob.world.getClosestPlayer(this.targetPredicate, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
        }

    }

    public void start() {
        this.mob.setTarget(this.targetEntity);
        super.start();
    }
}
