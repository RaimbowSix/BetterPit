package com.github.raimbowsix.betterpit.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class CacheManager {
    private static final String CACHE_FILE = "config/denicked.json";
    private static final Gson gson = new Gson();
    private static Map<String, String> cache = new HashMap<>();

    public static void loadCache() {
        try (Reader reader = new FileReader(CACHE_FILE)) {
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            cache = gson.fromJson(reader, type);
            if (cache == null) cache = new HashMap<>();
        } catch (FileNotFoundException e) {
            cache = new HashMap<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveCache() {
        try (Writer writer = new FileWriter(CACHE_FILE)) {
            gson.toJson(cache, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addToCache(String nick, String realName) {
        cache.put(nick, realName);
        saveCache();
    }

    public static boolean nickInCache(String nick) {
        return cache.containsKey(nick);
    }

    public static String getFromCache(String nick) {
        return cache.get(nick);
    }

    public static Map<String, String> getCache() {
        return cache;
    }
}
