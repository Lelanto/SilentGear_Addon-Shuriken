package net.lelanto.sgaddon.shuriken.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class SgShurikenConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    static {
        BUILDER.comment("Shuriken throw damage").push("damage");
    }

    /**
     * Minimum throw damage after summing Silent Gear ranged_damage parts.
     */
    public static final ModConfigSpec.DoubleValue BASE_DAMAGE = BUILDER
            .comment("Minimum throw damage clamp")
            .defineInRange("001_base_damage", 0.25D, 0.0D, 100.0D);

    /**
     * Power bonus unit. Total bonus = (powerLevel + 1) * this value (vanilla Power uses 0.5).
     */
    public static final ModConfigSpec.DoubleValue POWER_DAMAGE = BUILDER
            .comment(
                    "Power enchantment bonus unit",
                    "Total bonus = (powerLevel + 1) * this value (default: 0.25)")
            .defineInRange("002_power_damage", 0.25D, 0.0D, 100.0D);

    static {
        BUILDER.pop();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();

    private SgShurikenConfig() {}
}


