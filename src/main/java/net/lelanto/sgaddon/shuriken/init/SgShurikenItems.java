package net.lelanto.sgaddon.shuriken.init;

import net.lelanto.sgaddon.shuriken.SgShurikenAddonMod;
import net.lelanto.sgaddon.shuriken.item.SgShurikenItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.item.MainPartItem;
import net.silentchaos512.gear.item.blueprint.BlueprintType;
import net.silentchaos512.gear.item.blueprint.GearBlueprintItem;

public final class SgShurikenItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(SgShurikenAddonMod.MOD_ID);

    public static final DeferredItem<SgShurikenItem> SHURIKEN =
            ITEMS.registerItem("shuriken", SgShurikenItem::new);

    public static final DeferredItem<GearBlueprintItem> SHURIKEN_BLUEPRINT = ITEMS.registerItem(
            "shuriken_blueprint",
            props -> new GearBlueprintItem(SgShurikenGearTypes.SHURIKEN::value, BlueprintType.BLUEPRINT, props)
    );

    public static final DeferredItem<GearBlueprintItem> SHURIKEN_TEMPLATE = ITEMS.registerItem(
            "shuriken_template",
            props -> new GearBlueprintItem(SgShurikenGearTypes.SHURIKEN::value, BlueprintType.TEMPLATE, props)
    );

    /** One blade part; craft four (possibly with different materials) for final shuriken assembly. */
    public static final DeferredItem<MainPartItem> SHURIKEN_BLADE = ITEMS.registerItem(
            "shuriken_blade",
            props -> new MainPartItem(SgShurikenGearTypes.SHURIKEN::value, props.stacksTo(64))
    );

    private SgShurikenItems() {}
}
