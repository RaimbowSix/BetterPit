package com.github.raimbowsix.betterpit.util;

import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public final class InputBlocker {
    public static boolean blocked = false;

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        if (blocked) event.setCanceled(true);
    }

    @SubscribeEvent
    public void onMouse(InputEvent.MouseInputEvent event) {
        if (blocked) event.setCanceled(true);
    }

    @SubscribeEvent
    public void onGuiKey(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if (blocked) event.setCanceled(true);
    }

    @SubscribeEvent
    public void onGuiMouse(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (blocked) event.setCanceled(true);
    }
}
