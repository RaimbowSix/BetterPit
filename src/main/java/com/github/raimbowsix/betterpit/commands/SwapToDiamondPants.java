package com.github.raimbowsix.betterpit.commands;

import com.github.raimbowsix.betterpit.modules.AutoPantSwap;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;


public class SwapToDiamondPants extends CommandBase {
    @Override
    public String getCommandName() {
        return "swaptodiamond";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/swaptodiamond";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        AutoPantSwap.start(3);
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
