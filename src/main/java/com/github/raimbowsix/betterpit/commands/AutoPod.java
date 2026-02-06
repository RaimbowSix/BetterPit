package com.github.raimbowsix.betterpit.commands;

import com.github.raimbowsix.betterpit.modules.AutoPantSwap;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class AutoPod extends CommandBase {
    @Override
    public String getCommandName() {
        return "autopod";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/autopod";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        AutoPantSwap.alreadyDidPod=false;
        AutoPantSwap.start(2);
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
