package net.lelanto.sgaddon.shuriken.init;

import net.lelanto.sgaddon.shuriken.SgShurikenAddonMod;
import net.lelanto.sgaddon.shuriken.crafting.ShurikenBladeCompoundRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.lib.crafting.recipe.ExtendedShapelessRecipe;

public final class SgAddonRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, SgShurikenAddonMod.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ShurikenBladeCompoundRecipe>> SHURIKEN_BLADE_COMPOUND =
            RECIPE_SERIALIZERS.register(
                    "shuriken_blade_compound",
                    () -> ExtendedShapelessRecipe.basicSerializer(ShurikenBladeCompoundRecipe::new)
            );

    private SgAddonRecipes() {}
}
