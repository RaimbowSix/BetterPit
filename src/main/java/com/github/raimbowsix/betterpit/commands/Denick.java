package com.github.raimbowsix.betterpit.commands;

import com.github.raimbowsix.betterpit.BetterPit;
import com.github.raimbowsix.betterpit.modules.Denicker;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import java.io.IOException;

public class Denick extends CommandBase {
    @Override
    public String getCommandName() {
        return "denick";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/getdisplayname player";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length==1) {
            new Thread(() -> {
                String realname = null;
                try {
                    realname = Denicker.tryToResolveNick(args[0]);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if(realname!=null){
                    System.out.println(realname);
                    BetterPit.sendMessage(realname);
                }else System.out.println("realname has been returned as null");

            }).start();
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
    public Minecraft mc = Minecraft.getMinecraft();
}
