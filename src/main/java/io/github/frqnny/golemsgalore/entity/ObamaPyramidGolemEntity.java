package io.github.frqnny.golemsgalore.entity;

import io.github.frqnny.golemsgalore.init.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class ObamaPyramidGolemEntity extends LaserGolemEntity {
    public ObamaPyramidGolemEntity(EntityType<? extends IronGolemEntity> entityType, World world) {
        super(entityType, world);
    }


    @Override
    protected Item getHealItem() {
        return ModItems.OBAMIUM_INGOT;
    }
}
