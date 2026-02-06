package com.github.raimbowsix.betterpit.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class GetEnchants {
    public static class Pant {
        public Set<Enchant> enchants;
        public boolean gem;
        public int maxLive;
        public int nonce;
        public Pant(Set<Enchant> enchants, boolean gem, int maxLive, int nonce){
            this.enchants = enchants;
            this.gem = gem;
            this.maxLive = maxLive;
            this.nonce = nonce;
        }
    }
    public static class Enchant {
        public String key;
        public int level;

        public Enchant(String key, int level) {
            this.key = key;
            this.level = level;
        }
    }
    private static NBTTagCompound getLeggingsExtraAttributes(String playerName) {
        if (Minecraft.getMinecraft().theWorld == null) return null;

        EntityPlayer player = Minecraft.getMinecraft().theWorld.getPlayerEntityByName(playerName);
        if (player == null) return null;

        ItemStack leggings = player.inventory.armorItemInSlot(1);
        if (leggings == null || !leggings.hasTagCompound()) return null;

        return leggings.getTagCompound().getCompoundTag("ExtraAttributes");
    }
    public static Pant getPantFromName(String name){
        Set<Enchant> enchants = getEnchantsFromName(name);
        boolean gem = getGemFromName(name);
        int maxLive = getMaxLivesFromName(name);
        ItemStack pants = Minecraft.getMinecraft().theWorld.getPlayerEntityByName(name).inventory.armorItemInSlot(1);
        int nonce = GetNonces.getNonce(pants);
        return new Pant(enchants, gem, maxLive, nonce);
    }
    public static boolean hasEnchant(ItemStack item, String enchantKey){
        Set<Enchant> enchants = new HashSet<>();
        NBTTagCompound tag = item.getTagCompound();
        if (tag == null) return false;
        NBTTagCompound extraAttributes = tag.getCompoundTag("ExtraAttributes");
        if (extraAttributes!=null){
            NBTTagList customEnchants = extraAttributes.getTagList("CustomEnchants", 10);
            if (customEnchants!=null){
                for (int i = 0; i < customEnchants.tagCount(); i++) {
                    NBTTagCompound enchantTag = customEnchants.getCompoundTagAt(i);
                    enchants.add(new Enchant(
                            enchantTag.getString("Key"),
                            enchantTag.getInteger("Level")
                    ));
                }
                for (GetEnchants.Enchant enchant : enchants){
                    if (Objects.equals(enchant.key, enchantKey)){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public static Set<Enchant> getEnchantsFromName(String name) {
        Set<Enchant> enchants = new HashSet<>();
        NBTTagCompound extraAttributes = getLeggingsExtraAttributes(name);
        if (extraAttributes == null || !extraAttributes.hasKey("CustomEnchants")) return enchants;

        NBTTagList customEnchants = extraAttributes.getTagList("CustomEnchants", 10);
        for (int i = 0; i < customEnchants.tagCount(); i++) {
            NBTTagCompound enchantTag = customEnchants.getCompoundTagAt(i);
            enchants.add(new Enchant(
                    enchantTag.getString("Key"),
                    enchantTag.getInteger("Level")
            ));
        }
        return enchants;
    }
    public static boolean getGemFromName(String name) {
        NBTTagCompound extraAttributes = getLeggingsExtraAttributes(name);
        return extraAttributes != null && extraAttributes.hasKey("UpgradeGemsUses");
    }
    public static int getMaxLivesFromName(String name) {
        NBTTagCompound extraAttributes = getLeggingsExtraAttributes(name);
        return (extraAttributes != null) ? extraAttributes.getInteger("MaxLives") : 0;
    }
    public static String getCompoundEnchants(GetEnchants.Pant pant){
        StringBuilder compoundEnchants= new StringBuilder();
        for (GetEnchants.Enchant enchant: pant.enchants){
            if (compoundEnchants.length()>0) {
                compoundEnchants.append(",");
            }
            compoundEnchants.append(enchant.key).append(enchant.level);
        }
        return compoundEnchants.toString();
    }

    //Darks
    public static Set<String> getEnchantKeysFromName (String name){
        Set<String> enchantKeys = new HashSet<>();
        NBTTagCompound extraAttributes = getLeggingsExtraAttributes(name);
        if (extraAttributes== null || !extraAttributes.hasKey("CustomEnchants")) return enchantKeys;
        NBTTagList customEnchants = extraAttributes.getTagList("CustomEnchants", 10);
        for (int i = 0; i < customEnchants.tagCount(); i++){
            NBTTagCompound enchant = customEnchants.getCompoundTagAt(i);
            String key = enchant.getString("Key");
            enchantKeys.add(key);
        }
        return enchantKeys;
    }
    public static String getDarkPantsEnchantFromName (String name){
        Set<String> enchantKeys = getEnchantKeysFromName(name);
        String fallback = null;
        for (String key : enchantKeys) {
            EPitEnchants enchant = EPitEnchants.fromString(key);
            if (enchant == null) continue;
            if (!enchant.toString().toLowerCase().contains("somber")) {
                return enchant.toString();
            }
            fallback = enchant.toString();
        }
        return fallback != null ? fallback : "FRESH";
    }
    public static String getDarkPantsEnchantFromName(EntityPlayer player){
        return getDarkPantsEnchantFromName(player.getName());
    }
}