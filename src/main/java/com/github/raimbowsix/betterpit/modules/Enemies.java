package com.github.raimbowsix.betterpit.modules;

import com.github.raimbowsix.betterpit.BetterPit;
import com.github.raimbowsix.betterpit.config.ConfigOneConfig;
import com.github.raimbowsix.betterpit.util.WatchlistManager;
import net.minecraft.client.network.NetworkPlayerInfo;
import java.util.HashSet;
import java.util.Set;

public class Enemies {
    public static Set<String> lastEnemySet = new HashSet<>();
    public static void detectIfEnemiesInLobby() {
        Set<String> currentEnemySet = new HashSet<>();
        for (NetworkPlayerInfo info : BetterPit.players) {
            String name = info.getGameProfile().getName();
            if (WatchlistManager.isEnemy(name)) {
                currentEnemySet.add(name.toLowerCase());
            }
        }
        if (ConfigOneConfig.enemyChatNotification){
            for (String name : currentEnemySet) {
            if (!lastEnemySet.contains(name)) {
                BetterPit.sendMessage("§8[§a+§8] §7[§6BetterPit§7] §4Enemy §8» §c" + name + " §fhas joined the lobby!");
            }
        }
            for (String name : lastEnemySet) {
                if (!currentEnemySet.contains(name)) {
                    BetterPit.sendMessage("§8[§c-§8] §7[§6BetterPit§7] §4Enemy §8» §c" + name + " §fhas left the lobby!");
                }
            }
        }
        lastEnemySet.clear();
        lastEnemySet.addAll(currentEnemySet);
    }
}
