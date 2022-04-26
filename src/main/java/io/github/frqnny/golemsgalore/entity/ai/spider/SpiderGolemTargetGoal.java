package io.github.frqnny.golemsgalore.entity.ai.spider;

import io.github.frqnny.golemsgalore.entity.SpiderGolemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class SpiderGolemTargetGoal<T extends LivingEntity> extends ActiveTargetGoal<T> {

    public SpiderGolemTargetGoal(SpiderGolemEntity golem, Class<T> targetEntityClass, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate, @Nullable Predicate<LivingEntity> targetPredicate) {
        super(golem, targetEntityClass, reciprocalChance, checkVisibility, checkCanNavigate, targetPredicate);
    }

    @Override
    public boolean canStart() {
        float f = this.mob.getBrightnessAtEyes();
        if (f >= 0.5f) {
            return false;
        }
        return super.canStart();
    }
}