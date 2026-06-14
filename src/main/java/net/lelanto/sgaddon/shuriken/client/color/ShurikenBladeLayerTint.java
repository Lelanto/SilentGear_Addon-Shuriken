package net.lelanto.sgaddon.shuriken.client.color;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.lelanto.sgaddon.shuriken.item.ShurikenColors;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record ShurikenBladeLayerTint(int layer) implements ItemTintSource {

    public static final MapCodec<ShurikenBladeLayerTint> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ExtraCodecs.NON_NEGATIVE_INT.fieldOf("layer").forGetter(ShurikenBladeLayerTint::layer)
            ).apply(instance, ShurikenBladeLayerTint::new)
    );

    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
        return ShurikenColors.getBladeColor(stack, this.layer);
    }

    @Override
    public MapCodec<? extends ItemTintSource> type() {
        return CODEC;
    }
}
