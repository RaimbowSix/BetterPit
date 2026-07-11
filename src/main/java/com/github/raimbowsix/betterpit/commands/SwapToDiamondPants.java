package com.github.raimbowsix.betterpit.commands;

import com.github.raimbowsix.betterpit.modules.Automation.DiamondPantSwap;
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
        DiamondPantSwap.start();
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
