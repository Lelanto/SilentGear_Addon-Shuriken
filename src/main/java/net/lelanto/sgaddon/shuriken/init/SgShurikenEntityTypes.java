package net.lelanto.sgaddon.shuriken.init;

import net.lelanto.sgaddon.shuriken.SgShurikenAddonMod;
import net.lelanto.sgaddon.shuriken.entity.SgShurikenEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SgShurikenEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, SgShurikenAddonMod.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<SgShurikenEntity>> SHURIKEN_PROJECTILE =
            ENTITY_TYPES.register("shuriken_projectile", () ->
                    EntityType.Builder.<SgShurikenEntity>of(SgShurikenEntity::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .clientTrackingRange(4)
                            .updateInterval(20)
                            .build(ResourceKey.create(
                                    Registries.ENTITY_TYPE,
                                    Identifier.fromNamespaceAndPath(SgShurikenAddonMod.MOD_ID, "shuriken_projectile")
                            ))
            );

    private SgShurikenEntityTypes() {}
}
