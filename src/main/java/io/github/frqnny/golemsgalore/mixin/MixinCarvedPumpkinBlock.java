package io.github.frqnny.golemsgalore.mixin;

import io.github.frqnny.golemsgalore.api.GolemSpawningUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CarvedPumpkinBlock.class)
public abstract class MixinCarvedPumpkinBlock extends Block {

    public MixinCarvedPumpkinBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return GolemSpawningUtil.golemHook(world, pos, player, hand);
    }
}