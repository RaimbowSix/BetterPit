package com.github.raimbowsix.betterpit.util;

import net.minecraft.client.Minecraft;

public class PlayerLocation {
    public static Minecraft mc = Minecraft.getMinecraft();
    public static String getPlayerDistance(String name, boolean abc){
        if (abc){
            int distanceToEntity = (int) mc.theWorld.getPlayerEntityByName(name).getDistanceToEntity(mc.thePlayer);
            return " §7[§r"+distanceToEntity+"m§7]";
        }else return "";
    }
}
