package net.lelanto.sgaddon.shuriken.item;

import net.lelanto.sgaddon.shuriken.render.ShurikenRenderState;
import net.lelanto.sgaddon.shuriken.entity.SgShurikenEntity;
import net.lelanto.sgaddon.shuriken.init.SgShurikenEntityTypes;
import net.lelanto.sgaddon.shuriken.init.SgShurikenGearTypes;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nonnull;
import java.util.List;

public class SgShurikenItem extends Item implements GearItem {

    private static final int THROW_COOLDOWN = 10;
    private static final float BASE_VELOCITY = 2.5f;
    private static final float BASE_INACCURACY = 0.3f;

    public SgShurikenItem(Item.Properties properties) {
        super(properties);
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(
            @Nonnull Level level,
            @Nonnull Player player,
            @Nonnull InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);

        if (GearHelper.isBroken(stack)) {
            player.getCooldowns().addCooldown(this.asItem(), THROW_COOLDOWN);
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide()) {
            float finalDamage = ShurikenDamage.computeThrowDamage(stack, getGearType());
            float finalVelocity = BASE_VELOCITY;

            SgShurikenEntity projectile = new SgShurikenEntity(
                    SgShurikenEntityTypes.SHURIKEN_PROJECTILE.get(),
                    level,
                    player,
                    stack,
                    finalDamage
            );

            projectile.shootFromRotation(
                    player,
                    player.getXRot(),
                    player.getYRot(),
                    0.0f,
                    finalVelocity,
                    BASE_INACCURACY
            );

            level.addFreshEntity(projectile);

            level.playSound(null,
                    player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SNOWBALL_THROW,
                    net.minecraft.sounds.SoundSource.PLAYERS,
                    0.5f,
                    0.4f / (level.getRandom().nextFloat() * 0.4f + 0.8f)
            );

            GearHelper.damageItem(stack, 1, player, slot -> {});
        }

        player.awardStat(Stats.ITEM_USED.get(this.asItem()));
        player.getCooldowns().addCooldown(this.asItem(), THROW_COOLDOWN);

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        if (GearHelper.isGear(stack)) {
            GearClientHelper.addInformation(stack, context, tooltip, flag);
            float throwDamage = ShurikenDamage.computeThrowDamage(stack, getGearType());
            tooltip.add(Component.translatable(
                    "item.sg_addon_shuriken.shuriken.throw_damage",
                    String.format("%.1f", throwDamage)
            ));
        }
    }

    @Override
    public GearType getGearType() {
        return SgShurikenGearTypes.SHURIKEN.get();
    }

    @Override
    public ItemColor getItemColors() {
        return (stack, tintIndex) -> {
            SgShurikenEntity entity = ShurikenRenderState.CURRENT_ENTITY.get();
            if (entity != null) {
                return entity.getBladeColor(tintIndex);
            }
            return ShurikenColors.getBladeColor(stack, tintIndex);
        };
    }
}