package com.github.raimbowsix.betterpit.modules.Automation;

import com.github.raimbowsix.betterpit.config.ConfigOneConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class RightClickSwap {
    //basic static
    static Minecraft mc = Minecraft.getMinecraft();

    //enum RightClickSwap
    private enum StateRightClickSwap { IDLE, OPEN_INV, SWAP, CLOSE_INV };
    private static RightClickSwap.StateRightClickSwap stateRightClickSwap = RightClickSwap.StateRightClickSwap.IDLE;

    //tickDelay
    private static int tickDelay = 0;

    public static void tryToSwapItemInHand(MouseEvent event) {
        if (!ConfigOneConfig.rightClickPantSwap || mc.currentScreen != null || event.button != 1 || !event.buttonstate) return;
        EntityPlayer player = mc.thePlayer;
        if (player == null || player.inventory.armorItemInSlot(1) == null) return;
        ItemStack heldItem = player.inventory.getStackInSlot(player.inventory.currentItem);
        if (heldItem == null || !(heldItem.getItem() instanceof ItemArmor)) return;
        ItemArmor armor = (ItemArmor) heldItem.getItem();
        RightClickSwap.start();
    }
    public static void start() {
        if (stateRightClickSwap != StateRightClickSwap.IDLE) return;
        tickDelay = 0;
        stateRightClickSwap = StateRightClickSwap.OPEN_INV;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || mc.thePlayer == null) return;
        if (stateRightClickSwap == RightClickSwap.StateRightClickSwap.IDLE) return;
        tickDelay++;
        switch (stateRightClickSwap) {
        case OPEN_INV:
            if (tickDelay >= 0) {
                mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
                stateRightClickSwap = RightClickSwap.StateRightClickSwap.SWAP;
                tickDelay = 0;
            }
            break;
        case SWAP:
            if (tickDelay >= 1) {
                if (mc.thePlayer.openContainer==null){
                    stateRightClickSwap = RightClickSwap.StateRightClickSwap.IDLE;
                    break;
                }
                if (mc.currentScreen instanceof GuiInventory) {
                    int armorSlot;
                    ItemArmor armor = (ItemArmor) mc.thePlayer.inventory.getStackInSlot(mc.thePlayer.inventory.currentItem).getItem();
                    switch (armor.armorType){
                        case 0:
                            armorSlot = 5;
                            break;
                        case 1:
                            armorSlot = 6;
                            break;
                        case 3:
                            armorSlot = 8;
                            break;
                        default:
                            armorSlot = 7;
                    }
                    int heldItemSlot = mc.thePlayer.inventory.currentItem;
                    mc.playerController.windowClick(0, armorSlot, heldItemSlot,2, mc.thePlayer);
                    tickDelay = 0;
                    stateRightClickSwap = RightClickSwap.StateRightClickSwap.CLOSE_INV;
                }
            }
            break;
        case CLOSE_INV:
            if (tickDelay >= 1) {
                mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(0));
                mc.displayGuiScreen(null);
                tickDelay = 0;
                stateRightClickSwap = RightClickSwap.StateRightClickSwap.IDLE;
            }
            break;
        }
    }
}
