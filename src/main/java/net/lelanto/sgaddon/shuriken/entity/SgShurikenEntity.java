package net.lelanto.sgaddon.shuriken.entity;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.lelanto.sgaddon.shuriken.item.ShurikenColors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.gear.trait.Trait;
import net.silentchaos512.gear.util.TraitHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class SgShurikenEntity extends AbstractArrow {


    private static final EntityDataAccessor<Integer> COLOR_0 =
            SynchedEntityData.defineId(SgShurikenEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COLOR_1 =
            SynchedEntityData.defineId(SgShurikenEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COLOR_2 =
            SynchedEntityData.defineId(SgShurikenEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COLOR_3 =
            SynchedEntityData.defineId(SgShurikenEntity.class, EntityDataSerializers.INT);

    @SuppressWarnings("unchecked")
    private static final EntityDataAccessor<Integer>[] COLORS =
            new EntityDataAccessor[]{COLOR_0, COLOR_1, COLOR_2, COLOR_3};

    private ItemStack originalGearStack = ItemStack.EMPTY;
    /** Sum of per-blade ranged_damage from {@link net.lelanto.sgaddon.shuriken.item.ShurikenDamage}, fixed at throw time. */
    private float baseDamage;
    @Nullable private UUID shooterUUID;
    @Nullable private IntOpenHashSet piercedEntityIds;

    public SgShurikenEntity(EntityType<? extends SgShurikenEntity> type, Level level) {
        super(type, level);
    }

    public SgShurikenEntity(
            EntityType<? extends SgShurikenEntity> type,
            Level level,
            Player shooter,
            ItemStack gearStack,
            float rangedDamage) {

        super(type, shooter, level, gearStack.copy(), gearStack);

        this.originalGearStack = gearStack.copy();
        this.baseDamage = rangedDamage;
        this.shooterUUID = shooter.getUUID();

        this.setPos(shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
        this.setBaseDamage(rangedDamage);

        extractAndSyncColors(gearStack);
    }

    private void extractAndSyncColors(ItemStack gearStack) {
        for (int i = 0; i < 4; i++) {
            this.entityData.set(COLORS[i], ShurikenColors.getBladeColor(gearStack, i));
        }
    }

    public int getBladeColor(int layer) {
        if (layer < 0 || layer > 3) {
            return 0xFFFFFFFF;
        }
        return this.entityData.get(COLORS[layer]);
    }


    public ItemStack getGearStackForRender() {
        return originalGearStack.isEmpty() ? ItemStack.EMPTY : originalGearStack.copy();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(COLOR_0, 0xFFFFFFFF);
        builder.define(COLOR_1, 0xFFFFFFFF);
        builder.define(COLOR_2, 0xFFFFFFFF);
        builder.define(COLOR_3, 0xFFFFFFFF);
    }

    @Override
    protected double getDefaultGravity() {
        return 0.025;
    }

    @Override
    protected void onHitEntity(@Nonnull EntityHitResult hitResult) {
        Entity target = hitResult.getEntity();

        if (!(this.level() instanceof ServerLevel serverLevel)) return;

        if (this.piercedEntityIds != null && this.piercedEntityIds.contains(target.getId())) {
            return;
        }

        if (this.getPierceLevel() > 0) {
            if (this.piercedEntityIds == null) {
                this.piercedEntityIds = new IntOpenHashSet(5);
            }
            if (this.piercedEntityIds.size() >= this.getPierceLevel() + 1) {
                this.discard();
                return;
            }
            this.piercedEntityIds.add(target.getId());
        }

        Player shooter = null;
        if (shooterUUID != null) {
            Entity ownerEntity = serverLevel.getEntity(shooterUUID);
            if (ownerEntity instanceof Player p) shooter = p;
        }

        if (target instanceof LivingEntity livingTarget) {
            DamageSource source = shooter != null
                    ? serverLevel.damageSources().playerAttack(shooter)
                    : serverLevel.damageSources().magic();

            float damage = this.baseDamage;
            ItemStack weapon = this.getWeaponItem();
            if (weapon != null) {
                damage = EnchantmentHelper.modifyDamage(serverLevel, weapon, livingTarget, source, damage);
            }

            if (this.isOnFire()) {
                livingTarget.igniteForSeconds(5.0F);
            }

            livingTarget.hurt(source, damage);
            this.doKnockback(livingTarget, source);

            if (weapon != null) {
                EnchantmentHelper.doPostAttackEffectsWithItemSource(serverLevel, livingTarget, source, weapon);
            }

            if (!this.originalGearStack.isEmpty() && shooter != null) {
                applyTraitsOnHit(this.originalGearStack, shooter, livingTarget, damage);
            }
        }

        this.playSound(SoundEvents.ARROW_HIT, 1.0f, 1.2f);
        if (this.getPierceLevel() <= 0) {
            this.discard();
        }
    }

    private void applyTraitsOnHit(
            ItemStack gearStack,
            Player shooter,
            LivingEntity target,
            float damage) {

        List<TraitInstance> traits = TraitHelper.getTraits(gearStack);

        for (TraitInstance instance : traits) {
            Trait trait = instance.getTrait();
            TraitActionContext context = new TraitActionContext(shooter, instance, gearStack);
            trait.onAttackEntity(context, target, damage);
        }
    }

    @Override
    protected void onHitBlock(@Nonnull BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        this.playSound(SoundEvents.ARROW_HIT, 0.7f, 1.5f);
        this.discard();
    }

    @Nonnull
    @Override
    protected ItemStack getDefaultPickupItem() {
        return ItemStack.EMPTY;
    }

    private static final String TAG_GEAR_STACK   = "SgGearStack";
    private static final String TAG_BASE_DAMAGE  = "SgBaseDamage";
    private static final String TAG_SHOOTER_UUID = "SgShooterUUID";

    @Override
    public void addAdditionalSaveData(@Nonnull CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        if (!originalGearStack.isEmpty()) {
            tag.put(TAG_GEAR_STACK, originalGearStack.save(this.registryAccess()));
        }
        tag.putFloat(TAG_BASE_DAMAGE, baseDamage);
        if (shooterUUID != null) tag.putUUID(TAG_SHOOTER_UUID, shooterUUID);

        for (int i = 0; i < 4; i++) {
            tag.putInt("BladeColor" + i, this.entityData.get(COLORS[i]));
        }
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains(TAG_GEAR_STACK)) {
            this.originalGearStack = ItemStack.parse(this.registryAccess(), tag.getCompound(TAG_GEAR_STACK))
                    .orElse(ItemStack.EMPTY);
        }
        if (tag.contains(TAG_BASE_DAMAGE))  baseDamage  = tag.getFloat(TAG_BASE_DAMAGE);
        if (tag.hasUUID(TAG_SHOOTER_UUID))  shooterUUID = tag.getUUID(TAG_SHOOTER_UUID);

        for (int i = 0; i < 4; i++) {
            if (tag.contains("BladeColor" + i)) {
                this.entityData.set(COLORS[i], tag.getInt("BladeColor" + i));
            }
        }
    }
}
