package io.github.frqnny.golemsgalore.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.frqnny.golemsgalore.entity.ModGolemEntity;
import io.github.frqnny.golemsgalore.init.ModEntities;
import io.github.frqnny.golemsgalore.util.Type;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.SummonCommand;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(SummonCommand.class)
public class MixinSummonCommand {
    private static final SimpleCommandExceptionType FAILED_EXCEPTIONN = new SimpleCommandExceptionType(Text.translatable("commands.summon.failed"));
    private static final SimpleCommandExceptionType INVALID_POSITION_EXCEPTIONN = new SimpleCommandExceptionType(Text.translatable("commands.summon.invalidPosition"));

    @Inject(method = "execute", at = @At(value = "HEAD"), cancellable = true)
    private static void executeSpecialGolems(ServerCommandSource source, Identifier entity, Vec3d pos, NbtCompound nbt, boolean initialize, CallbackInfoReturnable<Integer> info) throws CommandSyntaxException {
        BlockPos blockPos = new BlockPos(pos);
        if (!World.isValid(blockPos)) {
            throw INVALID_POSITION_EXCEPTIONN.create();
        } else {
            NbtCompound compoundTag = nbt.copy();
            compoundTag.putString("id", entity.toString());
            if (EntityType.getId(ModEntities.DIAMOND_GOLEM).equals(entity)) {
                spawnGolem(source, source.getWorld(), Type.DIAMOND, blockPos);
                info.setReturnValue(1);
            } else if (EntityType.getId(ModEntities.NETHERITE_GOLEM).equals(entity)) {
                spawnGolem(source, source.getWorld(), Type.NETHERITE, blockPos);
                info.setReturnValue(1);
            } else if (EntityType.getId(ModEntities.GOLDEN_GOLEM).equals(entity)) {
                spawnGolem(source, source.getWorld(), Type.GOLD, blockPos);
                info.setReturnValue(1);
            } else if (EntityType.getId(ModEntities.QUARTZ_GOLEM).equals(entity)) {
                spawnGolem(source, source.getWorld(), Type.QUARTZ, blockPos);
                info.setReturnValue(1);
            } else if (EntityType.getId(ModEntities.OBSIDIAN_GOLEM).equals(entity)) {
                spawnGolem(source, source.getWorld(), Type.OBSIDIAN, blockPos);
                info.setReturnValue(1);
            } else if (EntityType.getId(ModEntities.HAY_GOLEM).equals(entity)) {
                spawnGolem(source, source.getWorld(), Type.HAY, blockPos);
                info.setReturnValue(1);
            }
        }
    }

    private static void spawnGolem(ServerCommandSource source, ServerWorld world, Type type, BlockPos pos) {
        ModGolemEntity golem = ModEntities.typeMap.get(type).create(world);
        golem.setPlayerCreated(false);
        golem.setGolemType(type);
        golem.refreshPositionAndAngles((double) pos.getX() + 0.5D, (double) pos.getY() + 0.05D, (double) pos.getZ() + 0.5D, 0.0F, 0.0F);
        golem.initialize(source.getWorld(), source.getWorld().getLocalDifficulty(golem.getBlockPos()), SpawnReason.COMMAND, null, null);
        world.spawnEntity(golem);
        source.sendFeedback(Text.translatable("commands.summon.success", golem.getDisplayName()), true);

    }
}
