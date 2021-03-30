package io.github.frqnny.golemsgalore.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class DiamondLaserGolemEntity extends LaserGolemEntity {
    public DiamondLaserGolemEntity(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
    }


    @Override
    protected Item getHealItem() {
        return Items.DIAMOND;
    }
}
