package com.github.raimbowsix.betterpit.modules.Automation;

import com.github.raimbowsix.betterpit.BetterPit;
import com.github.raimbowsix.betterpit.config.ConfigOneConfig;
import com.github.raimbowsix.betterpit.util.GetEnchants;
import com.github.raimbowsix.betterpit.util.InventoryUtil;
import com.github.raimbowsix.betterpit.util.InputBlocker;
import com.github.raimbowsix.betterpit.util.Lobby;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class AutoPod {
    private static final String POD_ENCHANT = "escape_pod";
    // 7 is the pants slot id in the inventory GUI
    private static final int LEG_SLOT = 7;

    private enum State {
        IDLE,
        OPEN_INVENTORY,
        EQUIP_POD_FIRST_CLICK,
        EQUIP_POD_SECOND_CLICK,
        CLOSE_INVENTORY,
        WAIT_FOR_REGEN,
        UNEQUIP_FIRST_CLICK,
        UNEQUIP_SECOND_CLICK,
        CLOSE_FINAL
    }

    private static State state = State.IDLE;
    private static boolean alreadyDidPod = false;
    private static int tickDelay = 0;
    private static int oldInvSlot = -1;
    private static int podSlot = -1;

    public static void rearm() {
        alreadyDidPod = false;
    }

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
        podSlot = -1;
        InputBlocker.blocked = false;
    }

    private static void abort(boolean podUsed) {
        reset();
        if (!podUsed) alreadyDidPod = false;
    }

    private static boolean hasPodRegenEffect(EntityPlayer player) {
        for (int i = 1; i <= 3; i++) {
            if (player != null && player.getActivePotionEffect(Potion.regeneration) != null
                    && player.getActivePotionEffect(Potion.regeneration).getAmplifier() == i
                    && player.getActivePotionEffect(Potion.regeneration).getDuration() > 19) {
                return true;
            }
        }
        return false;
    }

    private static void tryToStart() {
        Minecraft mc = Minecraft.getMinecraft();
        if (!ConfigOneConfig.autoPod || mc.currentScreen != null) return;
        if (ConfigOneConfig.whileInPit && !BetterPit.isInPit()) return;
        EntityPlayer player = mc.thePlayer;
        if (player == null || player.inventory.armorItemInSlot(1) == null
                || GetEnchants.hasEnchant(player.inventory.armorItemInSlot(1), POD_ENCHANT)) return;
        boolean podAvailable = player.getHealth() < ConfigOneConfig.defaultHealthValuePod
                && !alreadyDidPod
                && InventoryUtil.hasEnchantInInventory(POD_ENCHANT)
                && player.getActivePotionEffect(Potion.poison) == null;
        if (podAvailable) {
            alreadyDidPod = true;
            start();
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || !Lobby.isReady()) return;
        tryToStart();
        if (state == State.IDLE) return;
        Minecraft mc = Minecraft.getMinecraft();
        tickDelay++;
        switch (state) {
            case OPEN_INVENTORY:
                if (tickDelay >= 0) {
                    if (mc.currentScreen != null) {
                        if (mc.currentScreen instanceof GuiInventory) {
                            transition(State.EQUIP_POD_FIRST_CLICK);
                        } else {
                            abort(false);
                        }
                        break;
                    }
                    InputBlocker.blocked = true;
                    InventoryUtil.openPlayerInventory();
                    transition(State.EQUIP_POD_FIRST_CLICK);
                }
                break;

            case EQUIP_POD_FIRST_CLICK:
                if (tickDelay >= 2) {
                    if (!InventoryUtil.isPlayerInventoryOpen()) {
                        abort(false);
                        break;
                    }
                    podSlot = InventoryUtil.slotWithEnchant(POD_ENCHANT);
                    oldInvSlot = podSlot;
                    if (podSlot == -1) {
                        transition(State.CLOSE_FINAL);
                        break;
                    }
                    if (podSlot <= 8) {
                        InventoryUtil.click(LEG_SLOT, podSlot, 2);
                        transition(State.CLOSE_INVENTORY);
                        break;
                    }
                    InventoryUtil.click(podSlot, 4, 2);
                    transition(State.EQUIP_POD_SECOND_CLICK);
                }
                break;

            case EQUIP_POD_SECOND_CLICK:
                if (tickDelay >= 2) {
                    if (!InventoryUtil.isPlayerInventoryOpen()) {
                        abort(false);
                        break;
                    }
                    InventoryUtil.click(LEG_SLOT, 4, 2);
                    transition(State.CLOSE_INVENTORY);
                }
                break;

            case CLOSE_INVENTORY:
                if (tickDelay >= 1) {
                    if (mc.currentScreen != null) {
                        InventoryUtil.closePlayerInventory();
                    }
                    transition(State.WAIT_FOR_REGEN);
                }
                break;

            case WAIT_FOR_REGEN:
                if (tickDelay >= 1) {
                    InputBlocker.blocked = false;
                    if (!hasPodRegenEffect(mc.thePlayer)) break;
                    if (mc.currentScreen != null || mc.thePlayer.inventory.armorItemInSlot(1) == null) {
                        if (mc.currentScreen instanceof GuiInventory && mc.thePlayer.inventory.armorItemInSlot(1) != null) {
                            transition(State.UNEQUIP_FIRST_CLICK);
                        } else {
                            abort(true);
                        }
                        break;
                    }
                    InputBlocker.blocked = true;
                    InventoryUtil.openPlayerInventory();
                    transition(State.UNEQUIP_FIRST_CLICK);
                }
                break;

            case UNEQUIP_FIRST_CLICK:
                if (tickDelay >= 2) {
                    if (!InventoryUtil.isPlayerInventoryOpen()) {
                        abort(true);
                        break;
                    }
                    if (oldInvSlot <= 8) {
                        InventoryUtil.click(LEG_SLOT, oldInvSlot, 2);
                        transition(State.CLOSE_FINAL);
                        break;
                    }
                    InventoryUtil.click(LEG_SLOT, 4, 2);
                    transition(State.UNEQUIP_SECOND_CLICK);
                }
                break;

            case UNEQUIP_SECOND_CLICK:
                if (tickDelay >= 2) {
                    if (!InventoryUtil.isPlayerInventoryOpen()) {
                        abort(true);
                        break;
                    }
                    InventoryUtil.click(oldInvSlot, 4, 2);
                    transition(State.CLOSE_FINAL);
                }
                break;

            case CLOSE_FINAL:
                if (tickDelay >= 1) {
                    InventoryUtil.closePlayerInventory();
                    reset();
                }
                break;
        }
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        reset();
    }

    @SubscribeEvent
    public void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (event.player == mc.thePlayer) {
            rearm();
            reset();
            BetterPit.sendMessage("[AutoPod] AutoPod conditional reset onRespawn");
        }
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (event.entityLiving instanceof EntityPlayer && mc.thePlayer == event.entityLiving) {
            rearm();
            reset();
            BetterPit.sendMessage("[AutoPod] AutoPod conditional reset onDeath");
        }
    }
}
