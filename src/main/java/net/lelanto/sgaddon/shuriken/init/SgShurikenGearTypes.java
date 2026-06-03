package net.lelanto.sgaddon.shuriken.init;

import net.lelanto.sgaddon.shuriken.SgShurikenAddonMod;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.property.GearPropertyGroups;
import net.silentchaos512.gear.setup.gear.GearTypes;

import static net.silentchaos512.gear.setup.gear.GearTypes.ALL;

public final class SgShurikenGearTypes {

    private static final ResourceKey<Registry<GearType>> GEAR_TYPE_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("silentgear", "gear_type"));

    public static final DeferredRegister<GearType> GEAR_TYPES =
            DeferredRegister.create(GEAR_TYPE_REGISTRY_KEY, SgShurikenAddonMod.MOD_ID);

    public static final DeferredHolder<GearType, GearType> SHURIKEN =
            GEAR_TYPES.register("shuriken", () -> GearType.Builder.of(GearTypes.ALL)
                    .relevantPropertyGroups(
                            GearPropertyGroups.SPECIAL,
                            GearPropertyGroups.GENERAL,
                            GearPropertyGroups.PROJECTILE
                    )
                    .build()
            );

    private SgShurikenGearTypes() {}
}