package com.github.raimbowsix.betterpit.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerLocation {
    public static Minecraft mc = Minecraft.getMinecraft();
    public static String getPlayerDistance(String name, boolean abc){
        if (!abc) return "";
        if (mc.theWorld == null || mc.thePlayer == null) return "";
        EntityPlayer player = mc.theWorld.getPlayerEntityByName(name);
        if (player == null) return "";
        int distanceToEntity = (int) player.getDistanceToEntity(mc.thePlayer);
        return " §7[§r"+distanceToEntity+"m§7]";
    }
}
