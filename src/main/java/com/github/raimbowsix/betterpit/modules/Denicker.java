package com.github.raimbowsix.betterpit.modules;

import com.github.raimbowsix.betterpit.BetterPit;
import com.github.raimbowsix.betterpit.config.ConfigOneConfig;
import com.github.raimbowsix.betterpit.util.CacheManager;
import com.github.raimbowsix.betterpit.util.GetEnchants;
import com.github.raimbowsix.betterpit.util.GetNonces;
import com.github.raimbowsix.betterpit.util.Lobby;
import com.github.raimbowsix.betterpit.util.PitApi;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Denicker {
    private static final Set<String> resolvingNicks = ConcurrentHashMap.newKeySet();
    public static Set<String> lastNickedSet = new HashSet<>();

    public static boolean isNicked(UUID playerUUID) {
        return playerUUID.version() == 1;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!Lobby.shouldRun()) return;
        detect();
    }

    public static void detect() {
        Set<String> currentNickedSet = new HashSet<>();
        for (NetworkPlayerInfo info : Lobby.playerInfos()) {
            UUID playerUUID = info.getGameProfile().getId();
            if (!isNicked(playerUUID)) continue;
            String nick = info.getGameProfile().getName();
            currentNickedSet.add(nick);
            if (!CacheManager.nickInCache(nick) && !resolvingNicks.contains(nick)) {
                resolvingNicks.add(nick);
                resolveAsync(nick);
            }
        }
        for (String name : currentNickedSet) {
            if (ConfigOneConfig.nickedChatNotification && !lastNickedSet.contains(name)) {
                BetterPit.sendMessage("§7[§6BetterPit§7] §fNicked Player §8» §b" + name);
            }
        }
        lastNickedSet.clear();
        lastNickedSet.addAll(currentNickedSet);
    }

    private static void resolveAsync(String nick) {
        new Thread(() -> {
            long millisStarted = System.currentTimeMillis();
            String realName = null;
            try {
                realName = tryToResolveNick(nick);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                resolvingNicks.remove(nick);
            }
            long time = System.currentTimeMillis() - millisStarted;
            if (realName != null) {
                synchronized (CacheManager.class) {
                    if (!CacheManager.nickInCache(nick)) {
                        CacheManager.addToCache(nick, realName);
                        if (ConfigOneConfig.nickedChatNotification) {
                            BetterPit.sendMessage("§7[§6BetterPit§7] §aDenicked §8» §b" + nick + " §7→ §r §e" + realName + " §7(§f" + time + "ms§7)");
                            BetterPit.sendMessage("§8[§a+§8] §7[§6BetterPit§7] §aAdded §e" + realName + "§a to denicked players cache.");
                        }
                    }
                }
            }
        }).start();
    }

    public static String tryToResolveNick(String nickedName) throws IOException {
        EntityPlayer player = Lobby.byName(nickedName);
        if (player == null) return null;

        Set<String> uuids = new HashSet<>();
        for (int nonce : GetNonces.getNoncesFromPlayer(player)) {
            String uuid = PitApi.uuidFromNonce(nonce);
            if (uuid != null) {
                uuids.add(uuid);
            }
        }
        if (uuids.size() == 1) {
            for (String uuid : uuids) {
                String realName = PitApi.nameFromUUID(uuid);
                if (realName != null && PitApi.canNick(uuid)) {
                    BetterPit.sendMessage("[Denicker] found from nonce: " + realName);
                    return realName;
                }
            }
        }

        Set<String> rageUuids = PitApi.ownerUuidsFromPant(GetEnchants.getPantFromName(nickedName));
        if (rageUuids != null) {
            Set<String> nickableUuids = new HashSet<>();
            for (String uuid : rageUuids) {
                if (PitApi.canNick(uuid)) {
                    nickableUuids.add(uuid);
                }
            }
            if (nickableUuids.size() == 1) {
                for (String uuid : nickableUuids) {
                    String realName = PitApi.nameFromUUID(uuid);
                    if (realName != null) {
                        BetterPit.sendMessage("[Denicker] found from rage: " + realName);
                        return realName;
                    }
                }
            }
        }
        return null;
    }
}
