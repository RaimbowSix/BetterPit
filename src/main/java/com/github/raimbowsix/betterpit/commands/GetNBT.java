package com.github.raimbowsix.betterpit.commands;

import com.github.raimbowsix.betterpit.BetterPit;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;

import java.util.Arrays;
import java.util.List;

public class GetNBT extends CommandBase {
    @Override
    public String getCommandName() {
        return "getnbt";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/getnbt shows nbt data of item you are holding in your hands.";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length==0){EntityPlayer player = (EntityPlayer) sender;
            ItemStack heldItem = player.getHeldItem();
            if (heldItem==null){
                BetterPit.sendMessage("§7[§6BetterPit§7] §fYou're not holding an item.");
            }
            NBTTagCompound tagCompound = heldItem.getTagCompound();
            if (tagCompound==null){
                BetterPit.sendMessage("§7[§6BetterPit§7] §fNo NBT data.");
            }else {
                BetterPit.sendMessage("§7[§6BetterPit§7] " + tagCompound.toString()); //I can't fucking get the plain raw text fucking niggers.
            }
        } else if (args.length!=2) {
            BetterPit.sendMessage("correct usage: /getnbt <player> <equippment> (equippment must be : helditem/pants/chestplate/boots/helmet)");
        }else {
            String playerName = args[0];
            String equippmentPart = args[1];
            try {
                EntityPlayer player = mc.theWorld.getPlayerEntityByName(playerName);
                ItemStack playerItem = null;
                switch (equippmentPart.toLowerCase()){
                    case "helditem": playerItem = player.getHeldItem();
                    break;
                    case "boots": playerItem = player.inventory.armorItemInSlot(0);
                    break;
                    case "pants": playerItem = player.inventory.armorItemInSlot(1);
                    break;
                    case "chestplate": playerItem = player.inventory.armorItemInSlot(2);
                    break;
                    case "helmet": playerItem = player.inventory.armorItemInSlot(3);
                    break;
                }
                NBTTagCompound playerTagCompound = null;
                if (playerItem!=null)playerTagCompound = playerItem.getTagCompound();
                if (playerTagCompound!=null) mc.thePlayer.addChatMessage(new ChatComponentText("§7[§6BetterPit§7] " + playerItem.getTagCompound().toString()));
            } catch (Exception e) {
                BetterPit.sendMessage(playerName+" doesn't exist in lobby or doesn't have such item equipped.");
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("nbt");
    }
    public Minecraft mc = Minecraft.getMinecraft();
}
