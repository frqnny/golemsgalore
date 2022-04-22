package io.github.frqnny.golemsgalore.entity;

import io.github.frqnny.golemsgalore.api.Type;
import io.github.frqnny.golemsgalore.init.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ModGolemEntity extends IronGolemEntity {
    private Type type;

    public ModGolemEntity(EntityType<? extends IronGolemEntity> entityType, World world) {
        super(entityType, world);
    }


    public Type getGolemType() {
        if (type == null) {
            this.type = Type.getTypeForEntityType(this.getType());
        }
        return type;
    }

    public void setGolemType(Type golemType) {
        this.type = golemType;
    }


    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        Item handItem = player.getStackInHand(hand).getItem();

        if (handItem.equals(this.getHealItem())) {
            float f = this.getHealth();
            this.heal(25.0F);
            if (this.getHealth() == f) {
                return ActionResult.PASS;
            } else {
                float g = 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
                this.playSound(this.getHealSound(), 1.0F, g);
                if (!player.isCreative()) {
                    player.getStackInHand(hand).decrement(1);
                }

                return ActionResult.success(this.world.isClient);
            }

        } else if (handItem == Items.GLASS_BOTTLE) {
            this.damage(DamageSource.player(player), this.getMaxHealth());
            player.getStackInHand(hand).decrement(1);
            player.giveItemStack(new ItemStack(ModItems.GOLEM_SOUL));
            return ActionResult.PASS;
        } else {
            return ActionResult.PASS;
        }
    }

    protected Item getHealItem() {
        return this.getGolemType().item();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_IRON_GOLEM_HURT;
    }

    protected SoundEvent getHealSound() {
        return SoundEvents.ENTITY_IRON_GOLEM_REPAIR;
    }
}
