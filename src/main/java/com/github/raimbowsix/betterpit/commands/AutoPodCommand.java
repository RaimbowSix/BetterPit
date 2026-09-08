package com.github.raimbowsix.betterpit.commands;

import com.github.raimbowsix.betterpit.modules.Automation.AutoPod;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class AutoPodCommand extends CommandBase {
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
        AutoPod.rearm();
        AutoPod.start();
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
