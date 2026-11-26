package net.lelanto.sgaddon.shuriken.entity;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.lelanto.sgaddon.shuriken.item.ShurikenColors;
import net.minecraft.core.UUIDUtil;
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
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.gear.trait.Trait;
import net.silentchaos512.gear.util.TraitHelper;
import org.jetbrains.annotations.Nullable;

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
    private static final EntityDataAccessor<ItemStack> GEAR_STACK =
            SynchedEntityData.defineId(SgShurikenEntity.class, EntityDataSerializers.ITEM_STACK);

    @SuppressWarnings("unchecked")
    private static final EntityDataAccessor<Integer>[] COLORS =
            new EntityDataAccessor[]{COLOR_0, COLOR_1, COLOR_2, COLOR_3};

    private ItemStack originalGearStack = ItemStack.EMPTY;
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

        ItemStack stackCopy = gearStack.copy();
        this.originalGearStack = stackCopy;
        this.entityData.set(GEAR_STACK, stackCopy);
        this.baseDamage = rangedDamage;
        this.shooterUUID = shooter.getUUID();
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
        ItemStack stack = this.entityData.get(GEAR_STACK);
        return stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(COLOR_0, 0xFFFFFFFF);
        builder.define(COLOR_1, 0xFFFFFFFF);
        builder.define(COLOR_2, 0xFFFFFFFF);
        builder.define(COLOR_3, 0xFFFFFFFF);
        builder.define(GEAR_STACK, ItemStack.EMPTY);
    }

    @Override
    protected double getDefaultGravity() {
        return 0.025;
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        Entity target = hitResult.getEntity();

        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

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
            if (ownerEntity instanceof Player player) {
                shooter = player;
            }
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
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        this.playSound(SoundEvents.ARROW_HIT, 0.7f, 1.5f);
        this.discard();
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return ItemStack.EMPTY;
    }

    private static final String TAG_GEAR_STACK = "SgGearStack";
    private static final String TAG_BASE_DAMAGE = "SgBaseDamage";
    private static final String TAG_SHOOTER_UUID = "SgShooterUUID";

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);

        ItemStack gearStack = this.entityData.get(GEAR_STACK);
        if (!gearStack.isEmpty()) {
            output.store(TAG_GEAR_STACK, ItemStack.CODEC, gearStack);
        }
        output.putFloat(TAG_BASE_DAMAGE, baseDamage);
        output.storeNullable(TAG_SHOOTER_UUID, UUIDUtil.CODEC, shooterUUID);

        for (int i = 0; i < 4; i++) {
            output.putInt("BladeColor" + i, this.entityData.get(COLORS[i]));
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);

        this.originalGearStack = input.read(TAG_GEAR_STACK, ItemStack.CODEC).orElse(ItemStack.EMPTY);
        this.entityData.set(GEAR_STACK, this.originalGearStack.isEmpty() ? ItemStack.EMPTY : this.originalGearStack.copy());
        this.baseDamage = input.getFloatOr(TAG_BASE_DAMAGE, 0.0f);
        this.shooterUUID = input.read(TAG_SHOOTER_UUID, UUIDUtil.CODEC).orElse(null);

        for (int i = 0; i < 4; i++) {
            this.entityData.set(COLORS[i], input.getIntOr("BladeColor" + i, 0xFFFFFFFF));
        }
    }
}
