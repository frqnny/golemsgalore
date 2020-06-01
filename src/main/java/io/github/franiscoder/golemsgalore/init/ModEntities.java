package io.github.franiscoder.golemsgalore.init;

import io.github.franiscoder.golemsgalore.GolemsGalore;
import io.github.franiscoder.golemsgalore.api.enums.Type;
import io.github.franiscoder.golemsgalore.entity.LaserGolemEntity;
import io.github.franiscoder.golemsgalore.entity.ModGolemEntity;
import net.fabricmc.fabric.api.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.registry.Registry;

import java.util.EnumMap;

public class ModEntities {
    public static EntityType<ModGolemEntity> DIAMOND_GOLEM;
    public static EntityType<ModGolemEntity> NETHERITE_GOLEM;
    public static EntityType<ModGolemEntity> GOLDEN_GOLEM;
    public static EntityType<ModGolemEntity> QUARTZ_GOLEM;
    public static EntityType<ModGolemEntity> OBSIDIAN_GOLEM;

    public static EntityType<LaserGolemEntity> LASER_GOLEM;

    public static EnumMap<Type, EntityType<ModGolemEntity>> typeMap;
    public static EnumMap<Type, DefaultAttributeContainer.Builder> attributeContainerMap;

    public static void init() {
        attributeContainerMap = new EnumMap<>(Type.class);
        // base is 100 hp, speed .25d, knockback resistance 1,  attack damage 15
        attributeContainerMap.put(Type.DIAMOND,
                createDefaultGolemAttributes(230, 0.2D, 1D, 20)
        );
        attributeContainerMap.put(Type.NETHERITE,
                createDefaultGolemAttributes(450, 0.18D, 1.1D, 30)
        );
        attributeContainerMap.put(Type.GOLD,
                createDefaultGolemAttributes(50, 0.35D, 1D, 19.9)
        );
        attributeContainerMap.put(Type.QUARTZ,
                createDefaultGolemAttributes(50, 0.25D, 0.5D, 5).add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 3.0D)
        );
        attributeContainerMap.put(Type.OBSIDIAN,
                createDefaultGolemAttributes(200, 0.15D, 2D, 15)
        );


        DIAMOND_GOLEM = register("diamond_golem",
                FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, ModGolemEntity::new).size(EntityDimensions.fixed(1.4F, 2.7F)).build());
        FabricDefaultAttributeRegistry.register(DIAMOND_GOLEM, attributeContainerMap.get(Type.DIAMOND));

        NETHERITE_GOLEM = register("netherite_golem",
                FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, ModGolemEntity::new).size(EntityDimensions.fixed(1.4F, 2.7F)).build()
        );
        FabricDefaultAttributeRegistry.register(NETHERITE_GOLEM, attributeContainerMap.get(Type.NETHERITE));

        GOLDEN_GOLEM = register("golden_golem",
                FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, ModGolemEntity::new).size(EntityDimensions.fixed(1.4F, 2.7F)).build()
        );
        FabricDefaultAttributeRegistry.register(GOLDEN_GOLEM, attributeContainerMap.get(Type.GOLD));

        QUARTZ_GOLEM = register("quartz_golem",
                FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, ModGolemEntity::new).size(EntityDimensions.fixed(1.4F, 2.7F)).build()
        );
        FabricDefaultAttributeRegistry.register(QUARTZ_GOLEM, attributeContainerMap.get(Type.QUARTZ));

        OBSIDIAN_GOLEM = register("obsidian_golem",
                FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, ModGolemEntity::new).size(EntityDimensions.fixed(1.4F, 2.7F)).build()
        );
        FabricDefaultAttributeRegistry.register(OBSIDIAN_GOLEM, attributeContainerMap.get(Type.OBSIDIAN));


        LASER_GOLEM = register("laser_golem",
                FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, LaserGolemEntity::new).size(EntityDimensions.fixed(1.4F, 2.7F)).build()
        );
        FabricDefaultAttributeRegistry.register(LASER_GOLEM,
                createDefaultGolemAttributes(100, .25D, 1, 10)
        );

        typeMap = new EnumMap<>(Type.class);
        typeMap.put(Type.DIAMOND, DIAMOND_GOLEM);
        typeMap.put(Type.NETHERITE, NETHERITE_GOLEM);
        typeMap.put(Type.GOLD, GOLDEN_GOLEM);
        typeMap.put(Type.QUARTZ, QUARTZ_GOLEM);
        typeMap.put(Type.OBSIDIAN, OBSIDIAN_GOLEM);
    }

    private static <T extends Entity> EntityType<T> register(String name, EntityType<T> builder) {
        return Registry.register(Registry.ENTITY_TYPE, GolemsGalore.id(name), builder);
    }

    private static DefaultAttributeContainer.Builder createDefaultGolemAttributes(int health, double speed, double knockbackResistance, double attackDamage) {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, health).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, speed).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, knockbackResistance).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, attackDamage);
    }
}