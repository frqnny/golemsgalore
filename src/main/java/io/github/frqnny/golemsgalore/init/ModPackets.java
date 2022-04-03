package io.github.frqnny.golemsgalore.init;

import io.github.frqnny.golemsgalore.entity.projectile.PumpkinProjectileEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.world.ClientWorld;

import java.util.UUID;

public class ModPackets {


    public static void clientInit() {
        ClientPlayNetworking.registerGlobalReceiver(PumpkinProjectileEntity.PUMPKING_PROJECTILE_SPAWN, (client, handler, buf, responseSender) -> {
            final ClientWorld world = client.world;
            int id = buf.readVarInt();
            UUID uuid = buf.readUuid();
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            byte pitch = buf.readByte();
            byte yaw = buf.readByte();
            double velocityX = buf.readDouble();
            double velocityY = buf.readDouble();
            double velocityZ = buf.readDouble();
            UUID targetUUID = buf.readUuid();
            client.execute(() -> {
                PumpkinProjectileEntity entity = new PumpkinProjectileEntity(world, x, y, z, velocityX, velocityY, velocityZ);
                entity.setYaw(yaw);
                entity.setPitch(pitch);
                entity.setUuid(uuid);
                entity.setId(id);
                entity.setTargetUUID(targetUUID);
                world.addEntity(id, entity);
            });
        });
    }
}
