package com.github.raimbowsix.betterpit.util;

import com.github.raimbowsix.betterpit.util.GetEnchants.Pant;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PitApi {
    private static final String PITMART_PLAYER = "https://pitmart.net/api/player/";
    private static final String PITPANDA_USERNAME = "https://pitpanda.rocks/api/username/";
    private static final Pattern PANDA_CLEAN_NAME = Pattern.compile("§a(\\w+)\\s*$");
    private static final Pattern PANDA_ANY_NAME = Pattern.compile("\\s§.(\\w+)");

    private PitApi() {
    }

    public static String fetchJson(String urlString) throws IOException {
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

    public static String nameFromUUID(String uuid) throws IOException {
        String mojangResponse = fetchJson("https://api.mojang.com/user/profile/" + uuid);
        if (mojangResponse != null) {
            return json(mojangResponse).get("name").getAsString();
        }

        String pandaResponse = fetchJson(PITPANDA_USERNAME + uuid);
        if (pandaResponse != null) {
            Matcher m = PANDA_ANY_NAME.matcher(json(pandaResponse).get("name").getAsString());
            if (m.find()) return m.group(1);
        }

        String pitMartResponse = fetchJson(PITMART_PLAYER + uuid);
        if (pitMartResponse != null) {
            return json(pitMartResponse).getAsJsonObject("player").get("username").getAsString();
        }
        return null;
    }

    public static boolean canNick(String uuid) throws IOException {
        String pitMartResponse = fetchJson(PITMART_PLAYER + uuid);
        if (pitMartResponse != null) {
            JsonObject player = json(pitMartResponse).getAsJsonObject("player");
            return "SUPERSTAR".equals(player.get("rank").getAsString());
        }

        String pandaResponse = fetchJson(PITPANDA_USERNAME + uuid);
        if (pandaResponse != null) {
            Matcher m = PANDA_CLEAN_NAME.matcher(json(pandaResponse).get("name").getAsString());
            return m.find();
        }
        return false;
    }

    public static String uuidFromNonce(int nonce) throws IOException {
        String response = fetchJson("https://pitmart.net/api/searchitems?nonce" + nonce);
        if (response == null) return null;
        JsonArray docs = json(response).getAsJsonArray("docs");
        if (docs.size() > 0) {
            JsonElement owner = docs.get(0).getAsJsonObject().get("ownerUuid");
            if (owner != null) return owner.getAsString();
        }
        return null;
    }

    public static Set<String> ownerUuidsFromPant(Pant pant) throws IOException {
        if (pant.nonce != 9 || pant.enchants.size() < 2) return null;

        String response = fetchJson("https://pitmart.net/api/searchitems?" + GetEnchants.getCompoundEnchants(pant));
        if (response == null) return null;
        JsonArray docs = json(response).getAsJsonArray("docs");

        Set<String> ownerUuids = new HashSet<>();
        for (JsonElement docElement : docs) {
            JsonObject doc = docElement.getAsJsonObject();
            JsonObject item = doc.getAsJsonObject("item");

            boolean isGemmedMatch = !pant.gem || item.get("gemmed").getAsBoolean();
            boolean isMaxLivesMatch = item.get("maxLives").getAsInt() == pant.maxLive;

            if (isGemmedMatch && isMaxLivesMatch) {
                JsonElement owner = doc.has("ownerUuid") ? doc.get("ownerUuid") : item.get("ownerUuid");
                if (owner != null) {
                    ownerUuids.add(owner.getAsString());
                }
            }
        }
        return ownerUuids.isEmpty() ? null : ownerUuids;
    }

    private static JsonObject json(String response) {
        return new JsonParser().parse(response).getAsJsonObject();
    }
}
