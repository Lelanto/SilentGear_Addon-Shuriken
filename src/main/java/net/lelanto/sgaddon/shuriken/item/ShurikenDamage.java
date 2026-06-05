package net.lelanto.sgaddon.shuriken.item;

import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

public final class ShurikenDamage {

    public static final float MIN_DAMAGE = 0.5f;
    public static final float MAX_DAMAGE = 20.0f;

    private ShurikenDamage() {}


    public static float computeThrowDamage(ItemStack stack, GearType gearType) {
        if (!GearHelper.isGear(stack)) {
            return MIN_DAMAGE;
        }

        var construction = GearData.getConstruction(stack);
        var mains = construction.parts().getMains();
        if (mains.isEmpty()) {
            return MIN_DAMAGE;
        }

        var key = PropertyKey.of(GearProperties.RANGED_DAMAGE.get(), gearType);
        float total = 0f;
        for (PartInstance main : mains) {
            total += main.getProperty(PartTypes.MAIN.get(), key);
        }

        return Math.min(MAX_DAMAGE, Math.max(MIN_DAMAGE, total));
    }
}
