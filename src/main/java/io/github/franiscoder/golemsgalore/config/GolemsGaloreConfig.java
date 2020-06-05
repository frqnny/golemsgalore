package io.github.franiscoder.golemsgalore.config;

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
    @Comment("Laser Golem")
    public int healthLaser = 75;
    public double speedLaser = 0.25D;
    public double knockbackResistanceLaser = 1D;
    public double attackDamageLaser = 10;
    @Comment("Anti-Creeper Golem")
    public int healthCreeper = 75;
    public double speedCreeper = 0.30D;
    public double knockbackResistanceCreeper = 1D;
    public double attackDamageCreeper = 20;
    @Comment("Performance")
    public boolean renderLaserFlames = true;

}
