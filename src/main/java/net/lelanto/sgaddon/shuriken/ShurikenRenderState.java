package net.lelanto.sgaddon.shuriken;

import net.lelanto.sgaddon.shuriken.entity.SgShurikenEntity;

/**
 * Thread-local context for tinting the shuriken item model while rendering the projectile (client-only usage).
 */
public final class ShurikenRenderState {

    public static final ThreadLocal<SgShurikenEntity> CURRENT_ENTITY = new ThreadLocal<>();

    private ShurikenRenderState() {}
}
