package net.lelanto.sgaddon.shuriken;

import net.lelanto.sgaddon.shuriken.config.SgShurikenConfig;
import net.lelanto.sgaddon.shuriken.init.SgAddonRecipes;
import net.lelanto.sgaddon.shuriken.init.SgShurikenEntityTypes;
import net.lelanto.sgaddon.shuriken.init.SgShurikenGearTypes;
import net.lelanto.sgaddon.shuriken.init.SgShurikenItems;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;


@Mod(SgShurikenAddonMod.MOD_ID)
public class SgShurikenAddonMod {

    public static final String MOD_ID = "sg_addon_shuriken";

    public SgShurikenAddonMod(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, SgShurikenConfig.SPEC);
        SgShurikenGearTypes.GEAR_TYPES.register(modEventBus);
        SgShurikenItems.ITEMS.register(modEventBus);
        SgShurikenEntityTypes.ENTITY_TYPES.register(modEventBus);
        SgAddonRecipes.RECIPE_SERIALIZERS.register(modEventBus);
        modEventBus.addListener(this::addCreativeTabs);
    }
    private void addCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(SgShurikenItems.SHURIKEN.get());
        }

        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(SgShurikenItems.SHURIKEN_BLUEPRINT.get());
            event.accept(SgShurikenItems.SHURIKEN_TEMPLATE.get());
        }
    }
}
