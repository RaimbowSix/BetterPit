package com.github.raimbowsix.betterpit.modules;

import com.github.raimbowsix.betterpit.BetterPit;
import com.github.raimbowsix.betterpit.config.ConfigOneConfig;
import com.github.raimbowsix.betterpit.util.CacheManager;
import com.github.raimbowsix.betterpit.util.GetEnchants;
import com.github.raimbowsix.betterpit.util.GetNonces;
import com.google.gson.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Denicker {
    private static final Set<String> resolvingNicks = ConcurrentHashMap.newKeySet();
    public static Minecraft mc = Minecraft.getMinecraft();
    public static Set<String> lastNickedSet = new HashSet<>();
    public static boolean isNicked(UUID playerUUID){
        return playerUUID.version() == 1;
    }

    public static void detectIfPlayerIsNicked(){
        Set<String> currentNickedSet = new HashSet<>();
        for(NetworkPlayerInfo info : BetterPit.players){
            UUID playerUUID = info.getGameProfile().getId();
            if (isNicked(playerUUID)){
                String nick = info.getGameProfile().getName();
                currentNickedSet.add(nick);
                if (!CacheManager.nickInCache(nick) && !resolvingNicks.contains(nick)) {
                    resolvingNicks.add(nick);
                    new Thread(() -> {
                        String realName = null;
                        long millisStarted = System.currentTimeMillis();
                        try {
                            realName = tryToResolveNick(nick);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } finally {
                            resolvingNicks.remove(nick);
                        }
                        long millisEnded = System.currentTimeMillis();
                        long time = millisEnded - millisStarted;
                        if (realName != null) {
                            synchronized (CacheManager.class) {
                                if (!CacheManager.nickInCache(nick)) {
                                    CacheManager.addToCache(nick, realName);
                                    if (ConfigOneConfig.nickedChatNotification) {
                                        BetterPit.sendMessage("§7[§6BetterPit§7] §aDenicked §8» §b" + nick + " §7→ §r §e" + realName +" §7(§f"+time+"ms§7)");
                                        BetterPit.sendMessage("§8[§a+§8] §7[§6BetterPit§7] §aAdded §e" + realName + "§a to denicked players cache.");
                                    }
                                }
                            }
                        }
                    }).start();
                }
            }
        }
        for (String name : currentNickedSet) {
            if (ConfigOneConfig.nickedChatNotification){
                if (!lastNickedSet.contains(name)) {
                    BetterPit.sendMessage("§7[§6BetterPit§7] §fNicked Player §8» §b"+name);
                }
            }
        }
        lastNickedSet.clear();
        lastNickedSet.addAll(currentNickedSet);
    }
    public static String tryToResolveNick(String nickedName) throws IOException {
        EntityPlayer player =  mc.theWorld.getPlayerEntityByName(nickedName);
        if (player!=null){
            ArrayList<Integer> nonceList = GetNonces.getNoncesFromPlayer(player);
            Set<String> UUIDS = new HashSet<>();
            String UUID;
            for (int nonce : nonceList){
                UUID = getUUIDFromNonce(nonce);
                if (UUID!=null){
                    UUIDS.add(UUID);
                }
            }
            if (UUIDS.size()==1) {
                for (String uuid : UUIDS) {
                    String realName = getNameFromUUID(uuid);
                    if (realName != null && isPlayerAbleToNick(uuid)) {
                        BetterPit.sendMessage("[Denicker] found from nonce: "+realName);
                        return realName;
                    }
                }
            }
            Set<String> rageUUIDS = getUUIDSFromEnchant(GetEnchants.getPantFromName(nickedName));
            if (rageUUIDS!=null) {
                Set<String> uuids = new HashSet<>();
                for (String uuid : rageUUIDS) {
                    if (isPlayerAbleToNick(uuid)){
                        uuids.add(uuid);
                    }
                }
                if (uuids.size()==1){
                    for (String realUuid:uuids) {
                        String realName = getNameFromUUID(realUuid);
                        if (realName!=null) {
                            BetterPit.sendMessage("[Denicker] found from rage: "+realName);
                            return realName;
                        }
                    }
                }
            }
        }
        return null;
    }
    public static boolean isPlayerAbleToNick(String UUID) {
        try {
            String pitMartResponse = fetchJson("https://pitmart.net/api/player/" + UUID);
            if (pitMartResponse != null) {
                JsonObject json = getJsonObject(pitMartResponse);
                JsonObject player = json.getAsJsonObject("player");
                return "SUPERSTAR".equals(player.get("rank").getAsString());
            }
            String pandaResponse = fetchJson("https://pitpanda.rocks/api/username/" + UUID);
            if (pandaResponse != null) {
                JsonObject json = getJsonObject(pandaResponse);
                String notCleanName = json.get("name").getAsString();
                Matcher m = Pattern.compile("§a(\\w+)\\s*$").matcher(notCleanName);
                return m.find();
            }

        } catch (IOException | JsonSyntaxException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
    public static String getNameFromUUID(String UUID) {
        try {
            String mojangUrl = "https://api.mojang.com/user/profile/" + UUID;
            String mojangResponse = fetchJson(mojangUrl);
            if (mojangResponse != null) {
                JsonObject json = getJsonObject(mojangResponse);
                return json.get("name").getAsString();
            }

            String pandaUrl = "https://pitpanda.rocks/api/username/" + UUID;
            String pandaResponse = fetchJson(pandaUrl);
            if (pandaResponse != null) {
                JsonObject json = getJsonObject(pandaResponse);
                String notCleanName = json.get("name").getAsString();
                Matcher m = Pattern.compile("\\s§.(\\w+)").matcher(notCleanName);
                if (m.find()) return m.group(1);
            }
            String pitMartUrl = "https://pitmart.net/api/player/" + UUID;
            String pitMartResponse = fetchJson(pitMartUrl);
            if (pitMartResponse != null) {
                JsonObject json = getJsonObject(pitMartResponse);
                return json.getAsJsonObject("player").get("username").getAsString();
            }
        } catch (IOException | JsonSyntaxException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private static JsonObject getJsonObject(String response) {
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(response);
        return element.getAsJsonObject();
    }
    private static String fetchJson(String urlString) throws IOException {
        HttpURLConnection con = null;
        try {
            URL url = new URL(urlString);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");

            int status = con.getResponseCode();
            if (status != 200) return null;

            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                StringBuilder content = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                return content.toString();
            }
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }
    public static Set<String> getUUIDSFromEnchant(GetEnchants.Pant pant) {
        if (pant.nonce != 9 || pant.enchants.size() < 2) return null;

        try {
            String apiUrl = "https://pitmart.net/api/searchitems?" + GetEnchants.getCompoundEnchants(pant);
            String response = fetchJson(apiUrl);
            if (response != null) {
                JsonObject json = getJsonObject(response);
                JsonArray docs = json.getAsJsonArray("docs");

                Set<String> ownerUUIDs = new HashSet<>();
                for (JsonElement docElement : docs) {
                    JsonObject doc = docElement.getAsJsonObject();
                    JsonObject item = doc.getAsJsonObject("item");

                    boolean isGemmedMatch = !pant.gem || item.get("gemmed").getAsBoolean();
                    boolean isMaxLivesMatch = item.get("maxLives").getAsInt() == pant.maxLive;

                    if (isGemmedMatch && isMaxLivesMatch) {
                        JsonElement owner = doc.has("ownerUuid") ? doc.get("ownerUuid") : item.get("ownerUuid");
                        if (owner != null) {
                            ownerUUIDs.add(owner.getAsString());
                        }
                    }
                }
                return ownerUUIDs.isEmpty() ? null : ownerUUIDs;
            }
        } catch (IOException | JsonSyntaxException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
    public static String getUUIDFromNonce(int nonce) {
        try {
            String apiURL = "https://pitmart.net/api/searchitems?nonce" + nonce;
            String response = fetchJson(apiURL);
            if (response != null) {
                JsonObject json = getJsonObject(response);
                JsonArray docs = json.getAsJsonArray("docs");
                if (docs.size() > 0) {
                    JsonObject firstDoc = docs.get(0).getAsJsonObject();
                    JsonElement ownerUUID = firstDoc.get("ownerUuid");
                    if (ownerUUID != null) {
                        return ownerUUID.getAsString();
                    }
                }
            }
        } catch (IOException | JsonSyntaxException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}