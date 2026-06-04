package net.lelanto.sgaddon.shuriken.item;

import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

/**
 * Shared per-blade tint logic for shuriken item and projectile.
 */
public final class ShurikenColors {

    private static final int FALLBACK = 0xFFFFFFFF;

    private ShurikenColors() {}

    public static int getBladeColor(ItemStack stack, int layer) {
        if (layer < 0 || layer > 3 || !GearHelper.isGear(stack)) {
            return layer < 0 ? -1 : FALLBACK;
        }

        var construction = GearData.getConstruction(stack);
        if (construction == null) {
            return FALLBACK;
        }

        var mainParts = construction.parts().getPartsOfType(PartTypes.MAIN.get());
        if (layer >= mainParts.size()) {
            return FALLBACK;
        }

        int color = mainParts.get(layer).getColor(stack, layer, 0);
        return color != 0 ? (color | 0xFF000000) : FALLBACK;
    }
}
