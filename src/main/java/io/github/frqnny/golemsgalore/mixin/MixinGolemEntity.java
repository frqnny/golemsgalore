package io.github.frqnny.golemsgalore.mixin;

import io.github.frqnny.golemsgalore.init.ModItems;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings({"unused", "ConstantConditions"})
@Mixin(IronGolemEntity.class)
public class MixinGolemEntity {


    @Inject(at = @At("HEAD"), method = "interactMob", cancellable = true)
    protected void interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> info) {
        Item item = player.getStackInHand(hand).getItem();

        if (item == Items.GLASS_BOTTLE) {
            IronGolemEntity entity = ((IronGolemEntity) (Object) this);
            entity.setHealth(entity.getHealth() - (entity.getMaxHealth() / 3));

            player.getStackInHand(hand).decrement(1);
            player.giveItemStack(new ItemStack(ModItems.GOLEM_SOUL));
            info.setReturnValue(ActionResult.SUCCESS);
        }
    }
}
