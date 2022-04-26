package io.github.frqnny.golemsgalore.init;

import io.github.frqnny.golemsgalore.GolemsGalore;
import io.github.frqnny.golemsgalore.config.GolemsGaloreConfig;
import io.github.frqnny.golemsgalore.entity.*;
import io.github.frqnny.golemsgalore.entity.projectile.PoisonWebEntity;
import io.github.frqnny.golemsgalore.entity.projectile.PumpkinProjectileEntity;
import io.github.frqnny.golemsgalore.util.Type;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

public class ModEntities {
    public static final Map<Type, EntityType<? extends ModGolemEntity>> typeMap = new HashMap<>(7);
    public static final Map<Type, DefaultAttributeContainer.Builder> attributeContainerMap = new HashMap<>(7);
    public static final TagKey<EntityType<?>> GOLEM_BEEHIVE_INHABITORS = TagKey.of(Registry.ENTITY_TYPE_KEY, GolemsGalore.id("golem_beehive_inhabitors"));
    public static EntityType<ModGolemEntity> DIAMOND_GOLEM;
    public static EntityType<ModGolemEntity> NETHERITE_GOLEM;
    public static EntityType<ModGolemEntity> GOLDEN_GOLEM;
    public static EntityType<ModGolemEntity> QUARTZ_GOLEM;
    public static EntityType<ModGolemEntity> OBSIDIAN_GOLEM;
    public static EntityType<ModGolemEntity> HAY_GOLEM;
    public static EntityType<AmethystGolemEntity> AMETHYST_GOLEM;
    public static EntityType<LaserGolemEntity> LASER_GOLEM;
    public static EntityType<DiamondLaserGolemEntity> DIAMOND_LASER_GOLEM;
    public static EntityType<ObamaPyramidGolemEntity> OBAMA_PRISM_GOLEM;
    public static EntityType<AntiCreeperGolemEntity> ANTI_CREEPER_GOLEM;
    public static EntityType<GhastlyGolemEntity> GHASTLY_GOLEM;
    public static EntityType<BeeGolemEntity> BEE_GOLEM;
    public static EntityType<SpiderGolemEntity> SPIDER_GOLEM;
    public static EntityType<PumpkinProjectileEntity> PUMPKIN_PROJECTILE;
    public static EntityType<PoisonWebEntity> POISON_WEB_PROJECTILE;

    public static void init() {
        GolemsGaloreConfig config = GolemsGalore.getConfig();
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
        attributeContainerMap.put(Type.AMETHYST,
                createDefaultGolemAttributes(config.healthAmethyst, config.speedAmethyst, config.knockbackResistanceAmethyst, config.attackDamageAmethyst)
        );

        DIAMOND_GOLEM = register("diamond_golem",
                FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, ModGolemEntity::new).dimensions(EntityDimensions.fixed(1.4F, 2.7F)).build()
        );

        NETHERITE_GOLEM = register("netherite_golem",
                FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, ModGolemEntity::new).dimensions(EntityDimensions.fixed(1.4F, 2.7F)).build()
        );

        GOLDEN_GOLEM = register("golden_golem",
                FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, ModGolemEntity::new).dimensions(EntityDimensions.fixed(1.4F, 2.7F)).build()
        );

        QUARTZ_GOLEM = register("quartz_golem",
                FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, ModGolemEntity::new).dimensions(EntityDimensions.fixed(1.4F, 2.7F)).build()
        );

        OBSIDIAN_GOLEM = register("obsidian_golem",
                FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, ModGolemEntity::new).dimensions(EntityDimensions.fixed(1.4F, 2.7F)).build()
        );

        HAY_GOLEM = register("hay_golem",
                FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, ModGolemEntity::new).dimensions(EntityDimensions.fixed(1.4F, 2.7F)).build()
        );

        AMETHYST_GOLEM = register("amethyst_golem",
                FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, AmethystGolemEntity::new).dimensions(EntityDimensions.fixed(1.4F, 2.7F)).build()
        );

        typeMap.put(Type.DIAMOND, DIAMOND_GOLEM);
        typeMap.put(Type.NETHERITE, NETHERITE_GOLEM);
        typeMap.put(Type.GOLD, GOLDEN_GOLEM);
        typeMap.put(Type.QUARTZ, QUARTZ_GOLEM);
        typeMap.put(Type.OBSIDIAN, OBSIDIAN_GOLEM);
        typeMap.put(Type.HAY, HAY_GOLEM);
        typeMap.put(Type.AMETHYST, AMETHYST_GOLEM);

        for (Map.Entry<Type, DefaultAttributeContainer.Builder> entry : attributeContainerMap.entrySet()) {
            Type type = entry.getKey();
            EntityType<? extends ModGolemEntity> entityType = typeMap.get(type);
            FabricDefaultAttributeRegistry.register(entityType, attributeContainerMap.get(type));
        }

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
                FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, DiamondLaserGolemEntity::new).dimensions(EntityDimensions.fixed(1.4F, 2.7F)).build()
        );
        FabricDefaultAttributeRegistry.register(DIAMOND_LASER_GOLEM,
                createDefaultGolemAttributes(config.healthDiamondLaser, config.speedDiamondLaser, config.knockbackResistanceDiamondLaser, config.attackDamageDiamondLaser)
        );

        OBAMA_PRISM_GOLEM = register("obama_prism_golem",
                FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, ObamaPyramidGolemEntity::new).dimensions(EntityDimensions.fixed(3F, 3.2F)).build()
        );
        FabricDefaultAttributeRegistry.register(OBAMA_PRISM_GOLEM,
                createDefaultGolemAttributes(config.healthObama, config.speedObama, config.knockbackResistanceObama, config.attackDamageObama)
        );

        GHASTLY_GOLEM = register("ghastly_golem",
                FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, GhastlyGolemEntity::new).dimensions(EntityDimensions.fixed(1.4F, 2.7F)).fireImmune().build()
        );
        FabricDefaultAttributeRegistry.register(GHASTLY_GOLEM,
                createDefaultGolemAttributes(config.healthGhostly, config.speedGhostly, config.knockbackResistanceGhostly, config.attackDamageGhostly)
        );

        BEE_GOLEM = register("bee_golem",
                FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, BeeGolemEntity::new).dimensions(EntityDimensions.fixed(0.7f, 0.6f)).trackRangeChunks(8).build()
        );
        FabricDefaultAttributeRegistry.register(BEE_GOLEM,
                BeeGolemEntity.createBeeAttributes()
        );

        SPIDER_GOLEM = register("spider_golem",
                FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, SpiderGolemEntity::new).dimensions(EntityDimensions.fixed(1.4f, 0.9f)).trackRangeChunks(8).build()
        );
        FabricDefaultAttributeRegistry.register(SPIDER_GOLEM,
                createDefaultGolemAttributes(config.healthSpider, config.speedSpider, config.knockbackResistanceSpider, config.attackDamageSpider)
        );

        PUMPKIN_PROJECTILE = register("pumpkin_projectile",
                FabricEntityTypeBuilder.<PumpkinProjectileEntity>create(SpawnGroup.MISC, PumpkinProjectileEntity::new).dimensions(EntityDimensions.fixed(1F, 1F)).trackRangeBlocks(10).build()
        );

        POISON_WEB_PROJECTILE = register("poison_web_projectile",
                FabricEntityTypeBuilder.<PoisonWebEntity>create(SpawnGroup.MISC, PoisonWebEntity::new).dimensions(EntityDimensions.fixed(0.5F, 0.5F)).trackRangeBlocks(10).build()
        );


    }

    private static <T extends Entity> EntityType<T> register(String name, EntityType<T> builder) {
        return Registry.register(Registry.ENTITY_TYPE, GolemsGalore.id(name), builder);
    }

    private static DefaultAttributeContainer.Builder createDefaultGolemAttributes(int health, double speed, double knockbackResistance, double attackDamage) {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, health).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, speed).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, knockbackResistance).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, attackDamage);
    }

}