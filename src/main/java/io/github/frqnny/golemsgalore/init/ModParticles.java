package io.github.frqnny.golemsgalore.init;

import io.github.frqnny.golemsgalore.GolemsGalore;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.registry.Registry;


public class ModParticles {
    public static final DefaultParticleType LASER = FabricParticleTypes.simple();


    public static void register() {
        Registry.register(Registry.PARTICLE_TYPE, GolemsGalore.id("laser_particle"), LASER);
    }
}
