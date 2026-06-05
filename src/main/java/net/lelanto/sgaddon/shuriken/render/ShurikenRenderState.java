package net.lelanto.sgaddon.shuriken.render;

import net.lelanto.sgaddon.shuriken.entity.SgShurikenEntity;

public final class ShurikenRenderState {

    public static final ThreadLocal<SgShurikenEntity> CURRENT_ENTITY = new ThreadLocal<>();

    private ShurikenRenderState() {}
}
