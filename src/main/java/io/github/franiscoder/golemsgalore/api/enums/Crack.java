package io.github.franiscoder.golemsgalore.api.enums;

import com.google.common.collect.ImmutableList;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public enum Crack {
    NONE(1.0F),
    LOW(0.75F),
    MEDIUM(0.5F),
    HIGH(0.25F);

    private static final List<Crack> VALUES = Stream.of(values()).sorted(Comparator.comparingDouble((crack) -> (double) crack.maxHealthFraction)).collect(ImmutableList.toImmutableList());
    private final float maxHealthFraction;

    Crack(float maxHealthFraction) {
        this.maxHealthFraction = maxHealthFraction;
    }

    public static Crack from(float healthFraction) {
        for (Crack crack : VALUES) {
            if (healthFraction < crack.maxHealthFraction) {
                return crack;
            }
        }

        return NONE;
    }
}
