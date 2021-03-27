package io.github.frqnny.golemsgalore.config;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;

@Config(name = "golemsgalore")
public class GolemsGaloreConfig implements ConfigData {
    @Comment("Diamond Golem")
    public int healthDiamond = 230;
    public double speedDiamond = 0.2D;
    public double knockbackResistanceDiamond = 1D;
    public double attackDamageDiamond = 20D;
    @Comment("Netherite Golem")
    public int healthNetherite = 450;
    public double speedNetherite = 0.18D;
    public double knockbackResistanceNetherite = 1.1D;
    public double attackDamageNetherite = 30D;
    @Comment("Golden Golem")
    public int healthGolden = 50;
    public double speedGolden = 0.35D;
    public double knockbackResistanceGolden = 1D;
    public double attackDamageGolden = 19.9D;
    @Comment("Obsidian Golem")
    public int healthObsidian = 190;
    public double speedObsidian = 0.15D;
    public double knockbackResistanceObsidian = 2D;
    public double attackDamageObsidian = 15D;
    @Comment("Quartz Golem")
    public int healthQuartz = 50;
    public double speedQuartz = 0.25D;
    public double knockbackResistanceQuartz = 0.5D;
    public double attackDamageQuartz = 5;
    @Comment("Hay Golem")
    public int healthHay = 25;
    public double speedHay = 0.3D;
    public double knockbackResistanceHay = 0.1D;
    public double attackDamageHay = 5;
    @Comment("Laser Golem")
    public int healthLaser = 75;
    public double speedLaser = 0.25D;
    public double knockbackResistanceLaser = 1D;
    public double attackDamageLaser = 10;
    @Comment("Diamond Laser Golem")
    public int healthDiamondLaser = 200;
    public double speedDiamondLaser = 0.22D;
    public double knockbackResistanceDiamondLaser = 1D;
    public double attackDamageDiamondLaser = 20;
    @Comment("Anti-Creeper Golem")
    public int healthCreeper = 75;
    public double speedCreeper = 0.30D;
    public double knockbackResistanceCreeper = 1D;
    public double attackDamageCreeper = 20;
    @Comment("Obama Prism Golem")
    public int healthObama = 420;
    public double speedObama = 0.25D;
    public double knockbackResistanceObama = 1D;
    public double attackDamageObama = 20.1;
    @Comment("Performance")
    public boolean renderLaserFlames = true;
    @Comment("Generate Obamium Ore?")
    public boolean generateObamiumOre = true;

}
