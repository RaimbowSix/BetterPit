package com.github.raimbowsix.betterpit.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;

public final class InventoryUtil {
    private InventoryUtil() {
    }

    public static void openPlayerInventory() {
        Minecraft mc = Minecraft.getMinecraft();
        mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
        mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
    }

    public static void closePlayerInventory() {
        Minecraft mc = Minecraft.getMinecraft();
        mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(0));
        mc.displayGuiScreen(null);
    }

    public static boolean isPlayerInventoryOpen() {
        return Minecraft.getMinecraft().currentScreen instanceof GuiInventory;
    }

    public static void click(int slot, int button, int mode) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.playerController.windowClick(0, slot, button, mode, mc.thePlayer);
    }

    public static boolean hasEnchantInInventory(String enchantKey) {
        return slotWithEnchant(enchantKey) != -1;
    }

    public static int slotWithEnchant(String enchantKey) {
        for (int slot = 0; slot <= 35; slot++) {
            ItemStack item = player().inventory.getStackInSlot(slot);
            if (item != null && GetEnchants.hasEnchant(item, enchantKey)) {
                return slot;
            }
        }
        return -1;
    }

    public static boolean hasItemInInventory(Item item) {
        return slotWithItem(item) != -1;
    }

    public static int slotWithItem(Item item) {
        for (int slot = 0; slot <= 35; slot++) {
            ItemStack stack = player().inventory.getStackInSlot(slot);
            if (stack != null && stack.getItem() == item) {
                return slot;
            }
        }
        return -1;
    }

    private static EntityPlayer player() {
        return Minecraft.getMinecraft().thePlayer;
    }
}
