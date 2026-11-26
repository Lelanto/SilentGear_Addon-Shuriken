package net.lelanto.sgaddon.shuriken.init;

import net.lelanto.sgaddon.shuriken.SgShurikenAddonMod;
import net.lelanto.sgaddon.shuriken.item.SgShurikenBladeItem;
import net.lelanto.sgaddon.shuriken.item.SgShurikenItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.item.blueprint.BlueprintType;
import net.silentchaos512.gear.item.blueprint.GearBlueprintItem;

public final class SgShurikenItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(SgShurikenAddonMod.MOD_ID);

    public static final DeferredItem<SgShurikenItem> SHURIKEN =
            ITEMS.registerItem("shuriken", SgShurikenItem::new, props -> props.stacksTo(1).enchantable(1));

    public static final DeferredItem<GearBlueprintItem> SHURIKEN_BLUEPRINT = ITEMS.registerItem(
            "shuriken_blueprint",
            props -> new GearBlueprintItem(SgShurikenGearTypes.SHURIKEN::value, BlueprintType.BLUEPRINT, props)
    );

    public static final DeferredItem<GearBlueprintItem> SHURIKEN_TEMPLATE = ITEMS.registerItem(
            "shuriken_template",
            props -> new GearBlueprintItem(SgShurikenGearTypes.SHURIKEN::value, BlueprintType.TEMPLATE, props)
    );

    public static final DeferredItem<SgShurikenBladeItem> SHURIKEN_BLADE = ITEMS.registerItem(
            "shuriken_blade",
            props -> new SgShurikenBladeItem(SgShurikenGearTypes.SHURIKEN::value, props)
    );

    private SgShurikenItems() {}
}
