package net.lelanto.sgaddon.shuriken.event;

import net.lelanto.sgaddon.shuriken.SgShurikenAddonMod;
import net.lelanto.sgaddon.shuriken.item.SgShurikenBladeItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EventBusSubscriber(modid = SgShurikenAddonMod.MOD_ID)
public final class ShurikenCraftingEvents {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShurikenCraftingEvents.class);

    private ShurikenCraftingEvents() {}

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        ItemStack result = event.getCrafting();

        if (result.getItem() instanceof SgShurikenBladeItem && !SgShurikenBladeItem.hasValidMaterial(result)) {
            LOGGER.debug("Blocked invalid shuriken_blade craft for {}", event.getEntity().getName().getString());
            result.setCount(0);
        }
    }
}
