package io.github.frqnny.golemsgalore.entity.ai;

import io.github.frqnny.golemsgalore.entity.ModGolemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

import java.util.EnumSet;
import java.util.List;

public class TrackGolemTargetGoal extends TrackTargetGoal {
    private final ModGolemEntity golem;
    private final TargetPredicate targetPredicate = (new TargetPredicate()).setBaseMaxDistance(64.0D);
    private LivingEntity target;

    public TrackGolemTargetGoal(ModGolemEntity golem) {
        super(golem, false, true);
        this.golem = golem;
        this.setControls(EnumSet.of(Goal.Control.TARGET));
    }

    public boolean canStart() {
        Box box = this.golem.getBoundingBox().expand(10.0D, 8.0D, 10.0D);
        List<LivingEntity> villagers = this.golem.world.getTargets(VillagerEntity.class, this.targetPredicate, this.golem, box);
        List<PlayerEntity> players = this.golem.world.getPlayers(this.targetPredicate, this.golem, box);

        for (LivingEntity livingEntity : villagers) {
            VillagerEntity villagerEntity = (VillagerEntity) livingEntity;

            for (PlayerEntity playerEntity : players) {
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