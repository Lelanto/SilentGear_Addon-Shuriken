package net.lelanto.sgaddon.shuriken.item;

import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

/**
 * Throw damage is the sum of each main blade's {@code ranged_damage}, not Silent Gear's gear-level average.
 */
public final class ShurikenDamage {

    public static final float MIN_DAMAGE = 0.5f;
    public static final float MAX_DAMAGE = 20.0f;

    private ShurikenDamage() {}

    /**
     * Sum {@link GearProperties#RANGED_DAMAGE} from every main part on the stack.
     */
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
