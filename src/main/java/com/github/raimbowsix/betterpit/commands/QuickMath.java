package com.github.raimbowsix.betterpit.commands;


import com.github.raimbowsix.betterpit.BetterPit;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import java.util.Arrays;
import java.util.List;

public class QuickMath extends CommandBase {
    @Override
    public String getCommandName() {
        return "quickmath";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/quickmath";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        BetterPit.sendMessage("§d§lQUICK MATHS! §7Solve: §e(2+8)x5");
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("automath");
    }
    public Minecraft mc = Minecraft.getMinecraft();
}
