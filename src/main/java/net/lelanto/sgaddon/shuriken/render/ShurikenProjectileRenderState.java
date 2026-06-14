package net.lelanto.sgaddon.shuriken.render;

import net.lelanto.sgaddon.shuriken.entity.SgShurikenEntity;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ShurikenProjectileRenderState extends EntityRenderState {

    private static final float SPIN_SPEED = 45.0f;

    public final ShurikenItemStackRenderState item = new ShurikenItemStackRenderState();
    public float spinDegrees;
    public final int[] bladeColors = new int[4];

    public void extractShurikenRenderState(
            SgShurikenEntity entity,
            ItemStack stack,
            ItemModelResolver itemModelResolver,
            float partialTicks) {

        itemModelResolver.updateForNonLiving(this.item, stack, ItemDisplayContext.GROUND, entity);

        for (int i = 0; i < 4; i++) {
            this.bladeColors[i] = entity.getBladeColor(i);
        }

        this.item.applyBladeTints(this.bladeColors);
        this.spinDegrees = (entity.tickCount + partialTicks) * SPIN_SPEED;
    }
}
