package com.github.raimbowsix.betterpit.util;

import java.io.*;
import java.util.*;

public class WatchlistManager
{
    private static final File file;
    private static final HashSet<String> enemies;

    public static void loadWatchlist() {
        WatchlistManager.enemies.clear();
        if (!WatchlistManager.file.exists()) {
            try {
                WatchlistManager.file.getParentFile().mkdirs();
                WatchlistManager.file.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            final BufferedReader reader = new BufferedReader(new FileReader(WatchlistManager.file));
            String line;
            while ((line = reader.readLine()) != null) {
                WatchlistManager.enemies.add(line.trim().toLowerCase());
            }
            reader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static boolean isEnemy(final String name) {
        return WatchlistManager.enemies.contains(name.toLowerCase());
    }

    static {
        file = new File("config/watchlist.txt");
        enemies = new HashSet<String>();
    }
}
