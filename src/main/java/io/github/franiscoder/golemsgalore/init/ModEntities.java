package io.github.franiscoder.golemsgalore.init;

import io.github.franiscoder.golemsgalore.GolemsGalore;
import io.github.franiscoder.golemsgalore.api.enums.Type;
import io.github.franiscoder.golemsgalore.config.GolemsGaloreConfig;
import io.github.franiscoder.golemsgalore.entity.AntiCreeperGolemEntity;
import io.github.franiscoder.golemsgalore.entity.LaserGolemEntity;
import io.github.franiscoder.golemsgalore.entity.ModGolemEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
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
    public static EntityType<ModGolemEntity> HAY_GOLEM;

    public static EntityType<LaserGolemEntity> LASER_GOLEM;
    public static EntityType<LaserGolemEntity> DIAMOND_LASER_GOLEM;
    public static EntityType<LaserGolemEntity> OBAMA_PRISM_GOLEM;
    public static EntityType<AntiCreeperGolemEntity> ANTI_CREEPER_GOLEM;


    public static EnumMap<Type, EntityType<ModGolemEntity>> typeMap;
    public static EnumMap<Type, DefaultAttributeContainer.Builder> attributeContainerMap;

    public static void init() {
        GolemsGaloreConfig config = GolemsGalore.getConfig();
        attributeContainerMap = new EnumMap<>(Type.class);
        // base is 100 hp, speed .25d, knockback resistance 1,  attack damage 15
        attributeContainerMap.put(Type.DIAMOND,
                createDefaultGolemAttributes(config.healthDiamond, config.speedDiamond, config.knockbackResistanceDiamond, config.attackDamageDiamond)
        );
        attributeContainerMap.put(Type.NETHERITE,
                createDefaultGolemAttributes(config.healthNetherite, config.speedNetherite, config.knockbackResistanceNetherite, config.attackDamageNetherite)
        );
        attributeContainerMap.put(Type.GOLD,
                createDefaultGolemAttributes(config.healthGolden, config.speedGolden, config.knockbackResistanceGolden, config.attackDamageGolden)
        );
        attributeContainerMap.put(Type.QUARTZ,
                createDefaultGolemAttributes(config.healthQuartz, config.speedQuartz, config.knockbackResistanceQuartz, config.attackDamageQuartz).add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 3.0D)
        );
        attributeContainerMap.put(Type.OBSIDIAN,
                createDefaultGolemAttributes(config.healthObsidian, config.speedObsidian, config.knockbackResistanceObsidian, config.attackDamageObsidian)
        );
        attributeContainerMap.put(Type.HAY,
                createDefaultGolemAttributes(config.healthHay, config.speedHay, config.knockbackResistanceHay, config.attackDamageHay)
        );


        DIAMOND_GOLEM = register("diamond_golem",
                FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, ModGolemEntity::new).dimensions(EntityDimensions.fixed(1.4F, 2.7F)).build());
        FabricDefaultAttributeRegistry.register(DIAMOND_GOLEM, attributeContainerMap.get(Type.DIAMOND));

        NETHERITE_GOLEM = register("netherite_golem",
                FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, ModGolemEntity::new).dimensions(EntityDimensions.fixed(1.4F, 2.7F)).build()
        );
        FabricDefaultAttributeRegistry.register(NETHERITE_GOLEM, attributeContainerMap.get(Type.NETHERITE));

        GOLDEN_GOLEM = register("golden_golem",
                FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, ModGolemEntity::new).dimensions(EntityDimensions.fixed(1.4F, 2.7F)).build()
        );
        FabricDefaultAttributeRegistry.register(GOLDEN_GOLEM, attributeContainerMap.get(Type.GOLD));

        QUARTZ_GOLEM = register("quartz_golem",
                FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, ModGolemEntity::new).dimensions(EntityDimensions.fixed(1.4F, 2.7F)).build()
        );
        FabricDefaultAttributeRegistry.register(QUARTZ_GOLEM, attributeContainerMap.get(Type.QUARTZ));

        OBSIDIAN_GOLEM = register("obsidian_golem",
                FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, ModGolemEntity::new).dimensions(EntityDimensions.fixed(1.4F, 2.7F)).build()
        );
        FabricDefaultAttributeRegistry.register(OBSIDIAN_GOLEM, attributeContainerMap.get(Type.OBSIDIAN));

        HAY_GOLEM = register("hay_golem",
                FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, ModGolemEntity::new).dimensions(EntityDimensions.fixed(1.4F, 2.7F)).build()
        );
        FabricDefaultAttributeRegistry.register(HAY_GOLEM, attributeContainerMap.get(Type.HAY));

        LASER_GOLEM = register("laser_golem",
                FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, LaserGolemEntity::new).dimensions(EntityDimensions.fixed(1.4F, 2.7F)).build()
        );
        FabricDefaultAttributeRegistry.register(LASER_GOLEM,
                createDefaultGolemAttributes(config.healthLaser, config.speedLaser, config.knockbackResistanceLaser, config.attackDamageLaser)
        );

        ANTI_CREEPER_GOLEM = register("anti_creeper_golem",
                FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, AntiCreeperGolemEntity::new).dimensions(EntityDimensions.fixed(1.4F, 2.7F)).build()
        );
        FabricDefaultAttributeRegistry.register(ANTI_CREEPER_GOLEM,
                createDefaultGolemAttributes(config.healthCreeper, config.speedCreeper, config.knockbackResistanceCreeper, config.attackDamageCreeper).add(EntityAttributes.GENERIC_ARMOR, 2D)
        );

        DIAMOND_LASER_GOLEM = register("diamond_laser_golem",
                FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, LaserGolemEntity::new).dimensions(EntityDimensions.fixed(1.4F, 2.7F)).build()
        );
        FabricDefaultAttributeRegistry.register(DIAMOND_LASER_GOLEM,
                createDefaultGolemAttributes(config.healthDiamondLaser, config.speedDiamondLaser, config.knockbackResistanceDiamondLaser, config.attackDamageDiamondLaser)
        );

        OBAMA_PRISM_GOLEM = register("obama_prism_golem",
                FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, LaserGolemEntity::new).dimensions(EntityDimensions.fixed(2F, 3F)).build()
        );
        FabricDefaultAttributeRegistry.register(OBAMA_PRISM_GOLEM,
                createDefaultGolemAttributes(config.healthObama, config.speedObama, config.knockbackResistanceObama, config.attackDamageObama)
        );

        typeMap = new EnumMap<>(Type.class);
        typeMap.put(Type.DIAMOND, DIAMOND_GOLEM);
        typeMap.put(Type.NETHERITE, NETHERITE_GOLEM);
        typeMap.put(Type.GOLD, GOLDEN_GOLEM);
        typeMap.put(Type.QUARTZ, QUARTZ_GOLEM);
        typeMap.put(Type.OBSIDIAN, OBSIDIAN_GOLEM);
        typeMap.put(Type.HAY, HAY_GOLEM);
    }

    private static <T extends Entity> EntityType<T> register(String name, EntityType<T> builder) {
        return Registry.register(Registry.ENTITY_TYPE, GolemsGalore.id(name), builder);
    }

    private static DefaultAttributeContainer.Builder createDefaultGolemAttributes(int health, double speed, double knockbackResistance, double attackDamage) {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, health).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, speed).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, knockbackResistance).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, attackDamage);
    }
}