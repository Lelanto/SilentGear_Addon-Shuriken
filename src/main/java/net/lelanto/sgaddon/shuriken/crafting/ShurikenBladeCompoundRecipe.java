package net.lelanto.sgaddon.shuriken.crafting;

import net.lelanto.sgaddon.shuriken.init.SgAddonRecipes;
import net.lelanto.sgaddon.shuriken.item.SgShurikenBladeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.crafting.recipe.ShapelessCompoundPartRecipe;
import net.silentchaos512.gear.gear.material.MaterialInstance;

import java.util.ArrayList;
import java.util.List;

public class ShurikenBladeCompoundRecipe extends ShapelessCompoundPartRecipe {

    public ShurikenBladeCompoundRecipe(
            String group,
            CraftingBookCategory category,
            ItemStack result,
            net.minecraft.core.NonNullList<Ingredient> ingredients) {
        super(group, category, result, ingredients);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SgAddonRecipes.SHURIKEN_BLADE_COMPOUND.get();
    }

    @Override
    public boolean matches(CraftingInput inv, Level level) {
        if (!super.matches(inv, level)) {
            return false;
        }

        boolean hasRealMaterial = false;

        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }

            if (SgShurikenBladeItem.isCraftedBlade(stack)) {
                return false;
            }

            if (MaterialInstance.from(stack) != null) {
                hasRealMaterial = true;
            }
        }

        return hasRealMaterial && !collectMaterials(inv).isEmpty();
    }

    private static List<MaterialInstance> collectMaterials(CraftingInput inv) {
        var materials = new ArrayList<MaterialInstance>();

        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                MaterialInstance material = MaterialInstance.from(stack.copy().split(1));
                if (material != null) {
                    materials.add(material);
                }
            }
        }

        return materials;
    }
}
