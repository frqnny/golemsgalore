package io.github.frqnny.golemsgalore.mixin;

import io.github.frqnny.golemsgalore.entity.ModGolemEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.mob.*;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//fuck?
@SuppressWarnings("unused")
@Mixin({
        AbstractSkeletonEntity.class,
        SpiderEntity.class,
        ZombieEntity.class,
        EvokerEntity.class,
        IllusionerEntity.class,
        SlimeEntity.class,
        PillagerEntity.class,
        RavagerEntity.class,
        VindicatorEntity.class,

})
public class MixinHostiles extends MobEntity {

    protected MixinHostiles(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initGoals", at = @At("HEAD"))
    protected void initAttackGolemGoal(CallbackInfo info) {
        this.targetSelector.add(3, new FollowTargetGoal<>(this, ModGolemEntity.class, true));
    }

}
