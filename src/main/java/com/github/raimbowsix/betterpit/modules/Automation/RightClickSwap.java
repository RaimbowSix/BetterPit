package com.github.raimbowsix.betterpit.modules.Automation;

import com.github.raimbowsix.betterpit.config.ConfigOneConfig;
import com.github.raimbowsix.betterpit.util.InventoryUtil;
import com.github.raimbowsix.betterpit.util.Lobby;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class RightClickSwap {
    private enum State {
        IDLE,
        OPEN_INVENTORY,
        EQUIP_ARMOR,
        CLOSE_INVENTORY
    }

    private static State state = State.IDLE;
    private static int tickDelay = 0;

    @SubscribeEvent
    public void onMouseClick(MouseEvent event) {
        if (!ConfigOneConfig.rightClickPantSwap || event.button != 1 || !event.buttonstate) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.currentScreen != null) return;
        EntityPlayer player = mc.thePlayer;
        if (player.inventory.armorItemInSlot(1) == null) return;
        ItemStack heldItem = player.inventory.getStackInSlot(player.inventory.currentItem);
        if (heldItem == null || !(heldItem.getItem() instanceof ItemArmor)) return;
        start();
    }

    public static void start() {
        if (state != State.IDLE) return;
        transition(State.OPEN_INVENTORY);
    }

    private static void transition(State next) {
        state = next;
        tickDelay = 0;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || !Lobby.isReady()) return;
        if (state == State.IDLE) return;
        Minecraft mc = Minecraft.getMinecraft();
        tickDelay++;
        switch (state) {
            case OPEN_INVENTORY:
                if (tickDelay >= 0) {
                    InventoryUtil.openPlayerInventory();
                    transition(State.EQUIP_ARMOR);
                }
                break;

            case EQUIP_ARMOR:
                if (tickDelay >= 1) {
                    if (mc.thePlayer.openContainer == null) {
                        transition(State.IDLE);
                        break;
                    }
                    if (!InventoryUtil.isPlayerInventoryOpen()) break;
                    ItemStack heldItem = mc.thePlayer.inventory.getStackInSlot(mc.thePlayer.inventory.currentItem);
                    if (heldItem == null || !(heldItem.getItem() instanceof ItemArmor)) {
                        transition(State.CLOSE_INVENTORY);
                        break;
                    }
                    ItemArmor armor = (ItemArmor) heldItem.getItem();
                    InventoryUtil.click(armorSlot(armor.armorType), mc.thePlayer.inventory.currentItem, 2);
                    transition(State.CLOSE_INVENTORY);
                }
                break;

            case CLOSE_INVENTORY:
                if (tickDelay >= 1) {
                    InventoryUtil.closePlayerInventory();
                    transition(State.IDLE);
                }
                break;
        }
    }

    private static int armorSlot(int armorType) {
        switch (armorType) {
            case 0:
                return 5;
            case 1:
                return 6;
            case 3:
                return 8;
            default:
                return 7;
        }
    }
}
