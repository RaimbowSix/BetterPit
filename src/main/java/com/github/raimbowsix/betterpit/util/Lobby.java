package com.github.raimbowsix.betterpit.util;

import com.github.raimbowsix.betterpit.BetterPit;
import com.github.raimbowsix.betterpit.config.ConfigOneConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class Lobby {
    private Lobby() {
    }

    public static boolean isReady() {
        Minecraft mc = Minecraft.getMinecraft();
        return mc.thePlayer != null && mc.theWorld != null;
    }

    public static boolean shouldRun() {
        return isReady() && (!ConfigOneConfig.whileInPit || BetterPit.isInPit());
    }

    public static Collection<NetworkPlayerInfo> playerInfos() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.getNetHandler() == null) return Collections.emptyList();
        return mc.getNetHandler().getPlayerInfoMap();
    }

    public static List<EntityPlayer> playerEntities() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null) return Collections.emptyList();
        return mc.theWorld.playerEntities;
    }

    public static EntityPlayer byName(String name) {
        for (EntityPlayer player : playerEntities()) {
            if (player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }
        return null;
    }
}
