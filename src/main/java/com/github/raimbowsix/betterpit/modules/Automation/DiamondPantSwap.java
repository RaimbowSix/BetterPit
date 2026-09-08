package com.github.raimbowsix.betterpit.modules.Automation;

import com.github.raimbowsix.betterpit.BetterPit;
import com.github.raimbowsix.betterpit.config.ConfigOneConfig;
import com.github.raimbowsix.betterpit.modules.DarkPants;
import com.github.raimbowsix.betterpit.util.InventoryUtil;
import com.github.raimbowsix.betterpit.util.Lobby;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class DiamondPantSwap {
    private static final int LEG_SLOT = 7;
    private static final int DIAMOND_SLOT_BUTTON = 5;

    private enum State {
        IDLE,
        OPEN_INVENTORY,
        SWAP_DIAMONDS_FIRST_CLICK,
        SWAP_DIAMONDS_SECOND_CLICK,
        SWAP_BACK_FIRST_CLICK,
        SWAP_BACK_SECOND_CLICK,
        CLOSE_INVENTORY
    }

    private static State state = State.IDLE;
    private static boolean alreadyDidSwap = false;
    private static int tickDelay = 0;
    private static int oldInvSlot = -1;

    public static void start() {
        if (state != State.IDLE) return;
        transition(State.OPEN_INVENTORY);
    }

    private static void transition(State next) {
        state = next;
        tickDelay = 0;
    }

    private static void reset() {
        state = State.IDLE;
        tickDelay = 0;
        oldInvSlot = -1;
    }

    private static void tryToSwap() {
        Minecraft mc = Minecraft.getMinecraft();
        if (!ConfigOneConfig.autoSwapIfVenomed || mc.currentScreen != null) return;
        if (ConfigOneConfig.whileInPit && !BetterPit.isInPit()) return;
        EntityPlayer player = mc.thePlayer;
        if (player == null) return;
        ItemStack leggings = player.inventory.armorItemInSlot(1);
        boolean poison = player.getActivePotionEffect(Potion.poison) != null;
        if (leggings == null || DarkPants.hasDarks(player) || leggings.getItem() != Items.leather_leggings) return;
        if (ConfigOneConfig.swapBack && alreadyDidSwap && !poison) {
            start();
            return;
        }
        if (poison && !alreadyDidSwap && InventoryUtil.hasItemInInventory(Items.diamond_leggings)) {
            alreadyDidSwap = true;
            start();
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || !Lobby.isReady()) return;
        tryToSwap();
        if (state == State.IDLE) return;
        Minecraft mc = Minecraft.getMinecraft();
        tickDelay++;
        switch (state) {
            case OPEN_INVENTORY:
                if (tickDelay >= 0) {
                    InventoryUtil.openPlayerInventory();
                    if (ConfigOneConfig.swapBack && alreadyDidSwap && mc.thePlayer.getActivePotionEffect(Potion.poison) == null) {
                        transition(State.SWAP_BACK_FIRST_CLICK);
                    } else {
                        transition(State.SWAP_DIAMONDS_FIRST_CLICK);
                    }
                }
                break;

            case SWAP_DIAMONDS_FIRST_CLICK:
                if (tickDelay >= 2) {
                    if (mc.thePlayer.openContainer == null) {
                        reset();
                        break;
                    }
                    if (!InventoryUtil.isPlayerInventoryOpen()) break;
                    int diamPantsSlot = InventoryUtil.slotWithItem(Items.diamond_leggings);
                    oldInvSlot = diamPantsSlot;
                    if (diamPantsSlot == -1) {
                        transition(State.CLOSE_INVENTORY);
                        break;
                    }
                    if (diamPantsSlot <= 8) {
                        InventoryUtil.click(LEG_SLOT, diamPantsSlot, 2);
                        transition(State.CLOSE_INVENTORY);
                        break;
                    }
                    InventoryUtil.click(diamPantsSlot, DIAMOND_SLOT_BUTTON, 2);
                    transition(State.SWAP_DIAMONDS_SECOND_CLICK);
                }
                break;

            case SWAP_DIAMONDS_SECOND_CLICK:
                if (tickDelay >= 2) {
                    if (mc.thePlayer.openContainer == null) {
                        reset();
                        break;
                    }
                    if (!InventoryUtil.isPlayerInventoryOpen()) break;
                    InventoryUtil.click(LEG_SLOT, DIAMOND_SLOT_BUTTON, 2);
                    transition(State.CLOSE_INVENTORY);
                }
                break;

            case SWAP_BACK_FIRST_CLICK:
                if (tickDelay >= 2) {
                    if (mc.thePlayer.openContainer == null) {
                        reset();
                        break;
                    }
                    if (!InventoryUtil.isPlayerInventoryOpen()) break;
                    if (oldInvSlot <= 8) {
                        InventoryUtil.click(LEG_SLOT, oldInvSlot, 2);
                        alreadyDidSwap = false;
                        transition(State.CLOSE_INVENTORY);
                        break;
                    }
                    InventoryUtil.click(LEG_SLOT, DIAMOND_SLOT_BUTTON, 2);
                    transition(State.SWAP_BACK_SECOND_CLICK);
                }
                break;

            case SWAP_BACK_SECOND_CLICK:
                if (tickDelay >= 2) {
                    if (mc.thePlayer.openContainer == null) {
                        reset();
                        break;
                    }
                    if (!InventoryUtil.isPlayerInventoryOpen()) break;
                    InventoryUtil.click(oldInvSlot, DIAMOND_SLOT_BUTTON, 2);
                    alreadyDidSwap = false;
                    transition(State.CLOSE_INVENTORY);
                }
                break;

            case CLOSE_INVENTORY:
                if (tickDelay >= 1) {
                    InventoryUtil.closePlayerInventory();
                    reset();
                }
                break;
        }
    }
}
