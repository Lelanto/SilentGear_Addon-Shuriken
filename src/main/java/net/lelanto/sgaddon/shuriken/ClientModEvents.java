package net.lelanto.sgaddon.shuriken;

import com.mojang.blaze3d.vertex.PoseStack;
import net.lelanto.sgaddon.shuriken.entity.SgShurikenEntity;
import net.lelanto.sgaddon.shuriken.init.SgShurikenEntityTypes;
import net.lelanto.sgaddon.shuriken.init.SgShurikenItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.util.GearHelper;

@EventBusSubscriber(modid = SgShurikenAddonMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(
                SgShurikenEntityTypes.SHURIKEN_PROJECTILE.get(),
                ShurikenRenderer::new
        );
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(ClientModEvents::tintCompoundBlade, SgShurikenItems.SHURIKEN_BLADE.get());
    }

    /** Use vanilla item model (4 layers); SG gear renderer tints all blades with one MAIN color. */
    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(IClientItemExtensions.DEFAULT, SgShurikenItems.SHURIKEN.get());
    }

    /** Single blade part before assembly. */
    private static int tintCompoundBlade(ItemStack stack, int tintIndex) {
        if (tintIndex != 0) {
            return -1;
        }

        if (stack.getItem() instanceof CompoundPartItem partItem) {
            int color = partItem.getColor(stack, 0);
            return color != 0 ? color : 0xFFFFFF;
        }

        PartInstance part = PartInstance.from(stack);
        if (part != null) {
            return colorFromPart(stack, part);
        }

        return 0xFFFFFF;
    }

    private static int colorFromPart(ItemStack context, PartInstance part) {
        int color = part.getColor(context);
        return color != 0 ? (color | 0xFF000000) : 0xFFFFFFFF;
    }

    public static class ShurikenRenderer
            extends net.minecraft.client.renderer.entity.EntityRenderer<SgShurikenEntity> {

        private static final float SPIN_SPEED = 45.0f;

        private final net.minecraft.client.renderer.entity.ItemRenderer itemRenderer;

        public ShurikenRenderer(EntityRendererProvider.Context context) {
            super(context);
            this.itemRenderer = context.getItemRenderer();
        }

        @Override
        public void render(SgShurikenEntity entity, float entityYaw,
                           float partialTick, PoseStack poseStack,
                           MultiBufferSource bufferSource, int packedLight) {

            ItemStack renderStack = entity.getGearStackForRender();
            if (renderStack.isEmpty()) {
                renderStack = new ItemStack(SgShurikenItems.SHURIKEN.get());
            }

            poseStack.pushPose();

            float spin = (entity.tickCount + partialTick) * SPIN_SPEED;
            poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(spin));
            poseStack.scale(0.5f, 0.5f, 0.5f);

            CURRENT_ENTITY.set(entity);

            itemRenderer.renderStatic(
                    renderStack,
                    net.minecraft.world.item.ItemDisplayContext.GROUND,
                    packedLight,
                    net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,
                    poseStack,
                    bufferSource,
                    entity.level(),
                    entity.getId()
            );

            CURRENT_ENTITY.remove();

            poseStack.popPose();

            super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        }

        @Override
        public ResourceLocation getTextureLocation(SgShurikenEntity entity) {
            return ResourceLocation.fromNamespaceAndPath(
                    SgShurikenAddonMod.MOD_ID, "textures/entity/shuriken.png"
            );
        }
    }

    public static final ThreadLocal<SgShurikenEntity> CURRENT_ENTITY = new ThreadLocal<>();
}
