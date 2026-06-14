package net.lelanto.sgaddon.shuriken.render;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class ShurikenItemStackRenderState extends ItemStackRenderState {

    private LayerRenderState primaryLayer;

    @Override
    public LayerRenderState newLayer() {
        LayerRenderState layer = super.newLayer();
        if (this.primaryLayer == null) {
            this.primaryLayer = layer;
        }
        return layer;
    }

    @Override
    public void clear() {
        this.primaryLayer = null;
        super.clear();
    }

    public void applyBladeTints(int[] colors) {
        if (this.primaryLayer == null || colors == null) {
            return;
        }

        IntList tints = this.primaryLayer.tintLayers();
        tints.clear();
        for (int color : colors) {
            tints.add(color);
        }
    }
}
