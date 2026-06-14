package net.lelanto.sgaddon.shuriken.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.lelanto.sgaddon.shuriken.SgShurikenAddonMod;
import net.lelanto.sgaddon.shuriken.client.color.ShurikenBladeLayerTint;
import net.lelanto.sgaddon.shuriken.entity.SgShurikenEntity;
import net.lelanto.sgaddon.shuriken.init.SgShurikenEntityTypes;
import net.lelanto.sgaddon.shuriken.init.SgShurikenItems;
import net.lelanto.sgaddon.shuriken.render.ShurikenProjectileRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = SgShurikenAddonMod.MOD_ID, value = Dist.CLIENT)
public final class ClientModEvents {

    private ClientModEvents() {}

    @SubscribeEvent
    public static void registerItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(
                Identifier.fromNamespaceAndPath(SgShurikenAddonMod.MOD_ID, "shuriken_blade_layer"),
                ShurikenBladeLayerTint.CODEC
        );
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(
                SgShurikenEntityTypes.SHURIKEN_PROJECTILE.get(),
                ShurikenRenderer::new
        );
    }

    public static class ShurikenRenderer extends EntityRenderer<SgShurikenEntity, ShurikenProjectileRenderState> {

        private final ItemModelResolver itemModelResolver;

        public ShurikenRenderer(EntityRendererProvider.Context context) {
            super(context);
            this.itemModelResolver = context.getItemModelResolver();
            this.shadowRadius = 0.0F;
        }

        @Override
        public ShurikenProjectileRenderState createRenderState() {
            return new ShurikenProjectileRenderState();
        }

        @Override
        public void extractRenderState(SgShurikenEntity entity, ShurikenProjectileRenderState state, float partialTicks) {
            super.extractRenderState(entity, state, partialTicks);

            ItemStack renderStack = entity.getGearStackForRender();
            if (renderStack.isEmpty()) {
                renderStack = new ItemStack(SgShurikenItems.SHURIKEN.get());
            }

            state.extractShurikenRenderState(entity, renderStack, this.itemModelResolver, partialTicks);
        }

        @Override
        public void submit(
                ShurikenProjectileRenderState state,
                PoseStack poseStack,
                SubmitNodeCollector submitNodeCollector,
                CameraRenderState camera) {

            if (!state.item.isEmpty()) {
                poseStack.pushPose();
                poseStack.mulPose(Axis.ZP.rotationDegrees(state.spinDegrees));
                poseStack.scale(0.5f, 0.5f, 0.5f);
                state.item.submit(
                        poseStack,
                        submitNodeCollector,
                        state.lightCoords,
                        OverlayTexture.NO_OVERLAY,
                        state.outlineColor
                );
                poseStack.popPose();
            }

            super.submit(state, poseStack, submitNodeCollector, camera);
        }
    }
}
