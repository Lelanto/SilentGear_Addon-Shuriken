package net.lelanto.sgaddon.shuriken.item;

import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.item.MainPartItem;
import net.silentchaos512.gear.util.Const;

import java.util.List;
import java.util.function.Supplier;

public class SgShurikenBladeItem extends MainPartItem {

    public SgShurikenBladeItem(Supplier<GearType> gearType, Properties properties) {
        super(gearType, properties.stacksTo(1));
    }

    public static boolean hasValidMaterial(ItemStack stack) {
        if (!(stack.getItem() instanceof SgShurikenBladeItem)) {
            return false;
        }

        List<MaterialInstance> materials = CompoundPartItem.getMaterials(stack);
        if (materials.isEmpty()) {
            return false;
        }

        return materials.stream().noneMatch(m -> m.getId().equals(Const.Materials.EXAMPLE.getId()));
    }

    public static boolean isCraftedBlade(ItemStack stack) {
        return stack.getItem() instanceof SgShurikenBladeItem && hasValidMaterial(stack);
    }

    @Override
    public ItemStack create(List<MaterialInstance> materials) {
        if (materials.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return super.create(materials);
    }
}
