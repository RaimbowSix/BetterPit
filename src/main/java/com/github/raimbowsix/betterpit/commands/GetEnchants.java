package com.github.raimbowsix.betterpit.commands;

import com.github.raimbowsix.betterpit.BetterPit;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import java.util.Arrays;
import java.util.List;

import static com.github.raimbowsix.betterpit.util.GetEnchants.getPantFromName;

public class GetEnchants extends CommandBase {
    @Override
    public String getCommandName() {
        return "getpantsenchants";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/getpantsenchants <player>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        com.github.raimbowsix.betterpit.util.GetEnchants.Pant pant = getPantFromName(args[0]);
        for (com.github.raimbowsix.betterpit.util.GetEnchants.Enchant currentEnchant:pant.enchants){
            String key = currentEnchant.key;
            int level = currentEnchant.level;
            BetterPit.sendMessage("Enchant Keys: "+key+" "+level);
        }
        BetterPit.sendMessage("MaxLives: "+pant.maxLive);
        BetterPit.sendMessage("Gem: "+pant.gem);
        BetterPit.sendMessage("Nonce: "+pant.nonce);
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("gpe");
    }
    public Minecraft mc = Minecraft.getMinecraft();
}
