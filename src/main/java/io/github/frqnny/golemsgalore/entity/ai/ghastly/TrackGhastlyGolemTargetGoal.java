package io.github.frqnny.golemsgalore.entity.ai.ghastly;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

public class TrackGhastlyGolemTargetGoal extends TrackTargetGoal {
    private final IronGolemEntity golem;
    private final TargetPredicate targetPredicate = TargetPredicate.createAttackable().setBaseMaxDistance(125.0D);
    @Nullable
    private LivingEntity target;

    public TrackGhastlyGolemTargetGoal(IronGolemEntity golem) {
        super(golem, false, true);
        this.golem = golem;
        this.setControls(EnumSet.of(Control.TARGET));
    }

    public boolean canStart() {
        Box box = this.golem.getBoundingBox().expand(10.0D, 8.0D, 10.0D);
        List<? extends LivingEntity> list = this.golem.world.getTargets(VillagerEntity.class, this.targetPredicate, this.golem, box);
        List<PlayerEntity> list2 = this.golem.world.getPlayers(this.targetPredicate, this.golem, box);

        for (LivingEntity livingEntity : list) {
            VillagerEntity villagerEntity = (VillagerEntity) livingEntity;

            for (PlayerEntity playerEntity : list2) {
                int i = villagerEntity.getReputation(playerEntity);
                if (i <= -100) {
                    this.target = playerEntity;
                }
            }
        }

        if (this.target == null) {
            return false;
        } else
            return !(this.target instanceof PlayerEntity) || !this.target.isSpectator() && !((PlayerEntity) this.target).isCreative();
    }

    public void start() {
        this.golem.setTarget(this.target);
        super.start();
    }
}
