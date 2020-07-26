package io.github.franiscoder.golemsgalore.entity;

import io.github.franiscoder.golemsgalore.entity.ai.GolemLookGoal;
import io.github.franiscoder.golemsgalore.entity.ai.TrackGolemTargetGoal;
import io.github.franiscoder.golemsgalore.init.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class AntiCreeperGolemEntity extends ModGolemEntity {
    public AntiCreeperGolemEntity(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.add(2, new WanderNearTargetGoal(this, 0.9D, 32.0F));
        this.goalSelector.add(2, new WanderAroundPointOfInterestGoal(this, 0.6D, false));
        this.goalSelector.add(3, new MoveThroughVillageGoal(this, 0.6D, false, 4, () -> false));
        this.goalSelector.add(5, new GolemLookGoal(this));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.6D));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackGolemTargetGoal(this));
        this.targetSelector.add(2, new RevengeGoal(this));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, MobEntity.class, 5, false, false,
                (livingEntity) -> livingEntity instanceof CreeperEntity));
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        Item handItem = player.getStackInHand(hand).getItem();

        if (handItem != Items.IRON_INGOT) {
            return ActionResult.PASS;
        } else if (handItem == Items.GLASS_BOTTLE) {
            this.damage(DamageSource.player(player), this.getMaxHealth());
            player.setStackInHand(hand, new ItemStack(ModItems.GOLEM_SOUL));
            return ActionResult.PASS;
        } else {
            float f = this.getHealth();
            this.heal(25.0F);
            if (this.getHealth() == f) {
                return ActionResult.PASS;
            } else {
                float g = 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
                this.playSound(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 1.0F, g);
                if (!player.abilities.creativeMode) {
                    player.getStackInHand(hand).decrement(1);
                }

                return ActionResult.success(this.world.isClient);
            }
        }
    }

    public boolean canTarget(EntityType<?> type) {
        if (this.isPlayerCreated() && type == EntityType.PLAYER) {
            return false;
        } else {
            return type == EntityType.CREEPER;
        }
    }
}
