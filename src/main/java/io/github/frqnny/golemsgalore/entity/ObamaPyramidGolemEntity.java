package io.github.frqnny.golemsgalore.entity;

import io.github.frqnny.golemsgalore.init.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ObamaPyramidGolemEntity extends LaserGolemEntity {
    public ObamaPyramidGolemEntity(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
    }


    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        Item handItem = player.getStackInHand(hand).getItem();

        if (handItem != ModItems.OBAMIUM_INGOT) {
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
}
