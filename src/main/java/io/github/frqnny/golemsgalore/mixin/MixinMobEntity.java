package io.github.frqnny.golemsgalore.mixin;

import io.github.frqnny.golemsgalore.entity.ModGolemEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public class MixinMobEntity {
    @Shadow
    @Final
    protected GoalSelector targetSelector;


    @Inject(method = "<init>", at = @At("TAIL"))
    protected void addGolemsToTarget(EntityType<? extends MobEntity> entityType, World world, CallbackInfo info) {
        if (this instanceof Monster && !(((MobEntity) (Object) this) instanceof CreeperEntity)) {
            this.targetSelector.add(3, new ActiveTargetGoal<>(((MobEntity) (Object) this), ModGolemEntity.class, true));
        }
    }
}
