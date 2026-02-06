package com.github.raimbowsix.betterpit.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;

public class GetNonces {
    public static ArrayList<Integer> getNoncesFromPlayer(EntityPlayer player){
        ArrayList<ItemStack> items = new ArrayList<>();
            items.add(player.getHeldItem());
            items.add(player.inventory.armorItemInSlot(0));
            items.add(player.inventory.armorItemInSlot(1));
            items.add(player.inventory.armorItemInSlot(2));
            items.add(player.inventory.armorItemInSlot(3));
        ArrayList<Integer> nonces = new ArrayList<>();
        for (ItemStack item:items){
            if (item!=null){
                int nonce = getNonce(item);
                if (nonce!=0 && nonce!=9 && nonce!=6 && nonce!=5){
                    nonces.add(nonce);
                }
            }
        }
        return nonces;
    }
    public static int getNonce(ItemStack item){
        if (item == null || item.getTagCompound() == null) {
            return 0;
        }
        NBTTagCompound extraAttrib = item.getTagCompound().getCompoundTag("ExtraAttributes");
        if (extraAttrib!=null) {
            return extraAttrib.getInteger("Nonce");
        }return 0;
    }
}