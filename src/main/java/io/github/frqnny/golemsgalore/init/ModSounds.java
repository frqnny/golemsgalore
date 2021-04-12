package io.github.frqnny.golemsgalore.init;

import io.github.frqnny.golemsgalore.GolemsGalore;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.registry.Registry;

public class ModSounds {
    public static final SoundEvent LASER = new SoundEvent(GolemsGalore.id("laser"));

    public static void init() {
        Registry.register(Registry.SOUND_EVENT, LASER.getId(), LASER);
    }
}
