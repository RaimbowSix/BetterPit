package com.github.raimbowsix.betterpit.modules;

import com.github.raimbowsix.betterpit.util.Lobby;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashSet;
import java.util.Set;

public class DarkPants {
    public static Set<String> lastDarkSet = new HashSet<>();

    public static boolean hasDarks(EntityPlayer player) {
        if (player == null) return false;
        ItemStack leggings = player.inventory.armorItemInSlot(1);
        if (leggings == null || !leggings.hasDisplayName()) return false;
        return leggings.getDisplayName().toLowerCase().contains("dark pants");
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!Lobby.shouldRun()) return;
        detect();
    }

    public static void detect() {
        Set<String> currentDarkSet = new HashSet<>();
        for (EntityPlayer player : Lobby.playerEntities()) {
            if (hasDarks(player)) {
                currentDarkSet.add(player.getName().toLowerCase());
            }
        }
        lastDarkSet.clear();
        lastDarkSet.addAll(currentDarkSet);
    }
}
