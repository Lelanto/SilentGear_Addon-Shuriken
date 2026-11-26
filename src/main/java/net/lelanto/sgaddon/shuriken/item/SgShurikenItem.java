package net.lelanto.sgaddon.shuriken.item;

import net.lelanto.sgaddon.shuriken.render.ShurikenRenderState;
import net.lelanto.sgaddon.shuriken.entity.SgShurikenEntity;
import net.lelanto.sgaddon.shuriken.init.SgShurikenEntityTypes;
import net.lelanto.sgaddon.shuriken.init.SgShurikenGearTypes;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nonnull;
import java.util.List;

public class SgShurikenItem extends Item implements GearItem {

    private static final int THROW_COOLDOWN = 10;
    private static final int MIN_THROW_COOLDOWN = 2;
    private static final int QUICK_CHARGE_TICKS_PER_LEVEL = 2;
    private static final float BASE_VELOCITY = 2.5f;
    private static final float BASE_INACCURACY = 0.3f;

    public SgShurikenItem(Item.Properties properties) {
        super(properties.stacksTo(1));
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(
            @Nonnull Level level,
            @Nonnull Player player,
            @Nonnull InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);
        int cooldown = computeThrowCooldown(stack, level);

        if (GearHelper.isBroken(stack)) {
            player.getCooldowns().addCooldown(this.asItem(), cooldown);
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            float finalDamage = ShurikenDamage.computeThrowDamageWithEnchants(
                    stack, getGearType(), serverLevel.registryAccess());
            int projectileCount = EnchantmentHelper.processProjectileCount(serverLevel, stack, player, 1);
            float maxSpread = EnchantmentHelper.processProjectileSpread(serverLevel, stack, player, 0.0F);
            float angleStep = projectileCount == 1 ? 0.0F : 2.0F * maxSpread / (projectileCount - 1);
            float angleOffset = (projectileCount - 1) % 2 * angleStep / 2.0F;
            float direction = 1.0F;

            for (int i = 0; i < projectileCount; i++) {
                float angle = angleOffset + direction * ((i + 1) / 2) * angleStep;
                direction = -direction;

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
                        player.getYRot() + angle,
                        0.0f,
                        BASE_VELOCITY,
                        BASE_INACCURACY
                );

                level.addFreshEntity(projectile);
            }

            level.playSound(null,
                    player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SNOWBALL_THROW,
                    net.minecraft.sounds.SoundSource.PLAYERS,
                    0.5f,
                    0.4f / (level.getRandom().nextFloat() * 0.4f + 0.8f)
            );

            if (!hasInfinity(stack, level) && !player.hasInfiniteMaterials()) {
                GearHelper.damageItem(stack, 1, player, slot -> {});
            }
        }

        player.awardStat(Stats.ITEM_USED.get(this.asItem()));
        player.getCooldowns().addCooldown(this.asItem(), cooldown);

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    private static int computeThrowCooldown(ItemStack stack, Level level) {
        int quickCharge = enchantmentLevel(stack, level, Enchantments.QUICK_CHARGE);
        return Math.max(MIN_THROW_COOLDOWN, THROW_COOLDOWN - quickCharge * QUICK_CHARGE_TICKS_PER_LEVEL);
    }

    private static boolean hasInfinity(ItemStack stack, Level level) {
        return enchantmentLevel(stack, level, Enchantments.INFINITY) > 0;
    }

    private static int enchantmentLevel(ItemStack stack, Level level, net.minecraft.resources.ResourceKey<Enchantment> key) {
        Holder<Enchantment> holder = level.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(key);
        return EnchantmentHelper.getItemEnchantmentLevel(holder, stack);
    }

    @Override
    public boolean isEnchantable(@Nonnull ItemStack stack) {
        return stack.getCount() == 1;
    }

    @Override
    public int getEnchantmentValue(@Nonnull ItemStack stack) {
        if (!GearHelper.isGear(stack)) {
            return 1;
        }
        return Math.max(1, GearHelper.getEnchantmentValue(stack));
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        if (GearHelper.isGear(stack)) {
            GearClientHelper.addInformation(stack, context, tooltip, flag);
            var registries = context.registries();
            float throwDamage = registries != null
                    ? ShurikenDamage.computeThrowDamageWithEnchants(stack, getGearType(), registries)
                    : ShurikenDamage.computeThrowDamage(stack, getGearType());
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
