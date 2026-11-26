package net.lelanto.sgaddon.shuriken.item;

import net.lelanto.sgaddon.shuriken.config.SgShurikenConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

public final class ShurikenDamage {

    public static final float MAX_DAMAGE = 20.0f;

    private ShurikenDamage() {}

    public static float minDamage() {
        return SgShurikenConfig.BASE_DAMAGE.get().floatValue();
    }

    public static float computeThrowDamage(ItemStack stack, GearType gearType) {
        float min = minDamage();
        if (!GearHelper.isGear(stack)) {
            return min;
        }

        var construction = GearData.getConstruction(stack);
        var mains = construction.parts().getMains();
        if (mains.isEmpty()) {
            return min;
        }

        var key = PropertyKey.of(GearProperties.RANGED_DAMAGE.get(), gearType);
        float total = 0f;
        for (PartInstance main : mains) {
            total += main.getProperty(PartTypes.MAIN.get(), key);
        }

        return Math.min(MAX_DAMAGE, Math.max(min, total));
    }

    /**
     * Power bonus: (powerLevel + 1) * config 002_power_damage (vanilla-equivalent at 0.5).
     */
    public static float applyPowerBonus(float baseDamage, int powerLevel) {
        if (powerLevel <= 0) {
            return baseDamage;
        }
        float unit = SgShurikenConfig.POWER_DAMAGE.get().floatValue();
        return baseDamage + (powerLevel + 1) * unit;
    }

    public static int powerLevel(ItemStack stack, HolderLookup.Provider registries) {
        return EnchantmentHelper.getItemEnchantmentLevel(
                registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.POWER),
                stack
        );
    }

    public static float computeThrowDamageWithEnchants(
            ItemStack stack,
            GearType gearType,
            HolderLookup.Provider registries) {
        return applyPowerBonus(computeThrowDamage(stack, gearType), powerLevel(stack, registries));
    }
}
