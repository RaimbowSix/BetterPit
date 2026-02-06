package com.github.raimbowsix.betterpit.commands;

import com.github.raimbowsix.betterpit.util.WatchlistManager;
import com.github.raimbowsix.betterpit.BetterPit;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Watchlist extends CommandBase {

    @Override
    public String getCommandName() {
        return "watchlist";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/watchlist <arg> <player> use /watchlist help if you don't know what you are doing.";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        File file = new File("config/watchlist.txt");
        if(!file.exists()) {
            BetterPit.sendMessage("§7[§6BetterPit§7] §awatchlist.txt file not found in config.");
            return;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("add")) {
            String playerName = args[1];
            try {
                FileWriter writer = new FileWriter("config/watchlist.txt", true);
                writer.append(playerName+System.lineSeparator());
                writer.close();
                LogManager.getLogger("watchlist").info("Managing Enemy List: adding to watchlist.txt");
                LogManager.getLogger("watchlist").info("Adding " + playerName + " to watchlist.txt");
                WatchlistManager.loadWatchlist();
                BetterPit.sendMessage("§8[§a+§8] §7[§6BetterPit§7] §aAdded "+playerName +" to watchlist.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            String playerName = args[1];
            List<String> lines = null;
            try {
                lines = Files.readAllLines(file.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            List<String> updatedLines = new ArrayList<>();
            boolean removed = false;

            for (String line : lines) {
                if (!line.trim().equalsIgnoreCase(playerName)) {
                    updatedLines.add(line);
                } else {
                    removed = true;
                }
            }

            if (removed) {
                try {
                    Files.write(file.toPath(), updatedLines); // overwrite
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                WatchlistManager.loadWatchlist();
                sender.addChatMessage(new ChatComponentText("§8[§c-§8] §7[§6BetterPit§7] §aRemoved " + playerName + " from watchlist."));
            } else {
                sender.addChatMessage(new ChatComponentText("§8[§a+§8] §7[§6BetterPit§7] §cPlayer " + playerName + " not found in watchlist."));
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("load")){
            WatchlistManager.loadWatchlist();
            BetterPit.sendMessage("§7[§6BetterPit§7] Watchlist successfully loaded.");
        }
        else {
            sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("wl");
    }
}