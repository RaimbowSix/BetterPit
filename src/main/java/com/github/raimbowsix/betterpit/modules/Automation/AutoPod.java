package com.github.raimbowsix.betterpit.modules.Automation;

import com.github.raimbowsix.betterpit.BetterPit;
import com.github.raimbowsix.betterpit.config.ConfigOneConfig;
import com.github.raimbowsix.betterpit.util.GetEnchants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import static com.github.raimbowsix.betterpit.BetterPit.isInPit;

public class AutoPod {

    public enum StateAutoPod { IDLE, OPEN_INV1, SWAP1, SWAP2, CLOSE_INV1, OPEN_INV2, SWAP3, SWAP4, CLOSE_INV2 }
    public static boolean alreadyDidPod = false;
    public static AutoPod.StateAutoPod stateAutoPod = AutoPod.StateAutoPod.IDLE;
    private static int tickDelay = 0;

    private static int oldInvSlot = -1;
    private static int podSlot = -1;

    static Minecraft mc = Minecraft.getMinecraft();
    static int leggingSlot = 7;

    public static void resetAutoPod() {
        AutoPod.stateAutoPod = StateAutoPod.IDLE;
        BetterPit.inputBlock = false;
    }

    public static boolean hasPodInInv(){
        for (int i = 0; i <= 35; i++) {
            ItemStack item = mc.thePlayer.inventory.getStackInSlot(i);
            if (item != null && GetEnchants.hasEnchant(item,"escape_pod")) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasPodRegenEffect(EntityPlayer player){
        for (int i = 1; i<=3; i++) {
            if (mc.thePlayer!=null && player.getActivePotionEffect(Potion.regeneration) != null) {
                if (player.getActivePotionEffect(Potion.regeneration).getAmplifier() == i && player.getActivePotionEffect(Potion.regeneration).getDuration()>19) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void tryToEscapePod(){
        if (!ConfigOneConfig.autoPod || mc.currentScreen != null) return;
        if (ConfigOneConfig.whileInPit && !isInPit()) return;
        EntityPlayer player = mc.thePlayer;
        if (player == null || player.inventory.armorItemInSlot(1)==null || GetEnchants.hasEnchant(player.inventory.armorItemInSlot(1), "escape_pod")) return;
        if (mc.thePlayer.getHealth()<ConfigOneConfig.defaultHealthValuePod && !alreadyDidPod && mc.currentScreen==null && hasPodInInv() && player.getActivePotionEffect(Potion.poison)==null){
            alreadyDidPod = true;
            AutoPod.start();
        }
    }

    public static void start() {
        if (stateAutoPod != AutoPod.StateAutoPod.IDLE) return;
        tickDelay = 0;
        stateAutoPod = AutoPod.StateAutoPod.OPEN_INV1;
    }
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (stateAutoPod != StateAutoPod.IDLE) {
            tickDelay++;
            switch (stateAutoPod) {

                case OPEN_INV1:
                    if (tickDelay >= 0) {
                        if (mc.currentScreen != null){
                            if(mc.currentScreen instanceof GuiInventory){
                                stateAutoPod = StateAutoPod.SWAP1;
                                tickDelay = 0;
                                break;
                            }
                            resetAutoPod();
                            alreadyDidPod = false;
                            break;
                        }
                        BetterPit.inputBlock = true;
                        mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                        mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
                        stateAutoPod = StateAutoPod.SWAP1;
                        tickDelay = 0;
                    }
                    break;

                case SWAP1:
                    if (tickDelay >= 2) {
                        if (!(mc.currentScreen instanceof GuiInventory)) {
                            resetAutoPod();
                            alreadyDidPod = false;
                            break;
                        }
                        oldInvSlot = -1;
                        podSlot = -1;
                        for (int i = 0; i <= 35; i++) {
                            ItemStack item = mc.thePlayer.inventory.getStackInSlot(i);
                            if (item != null && GetEnchants.hasEnchant(item, "escape_pod")) {
                                podSlot = i;
                                oldInvSlot = podSlot;
                                break;
                            }
                        }
                        if (podSlot == -1) {
                            tickDelay = 0;
                            stateAutoPod = StateAutoPod.CLOSE_INV2;
                            break;
                        }
                        if (podSlot <= 8) {
                            mc.playerController.windowClick(0, leggingSlot, podSlot, 2, mc.thePlayer);
                            tickDelay = 0;
                            stateAutoPod = StateAutoPod.CLOSE_INV1;
                            break;
                        }
                        mc.playerController.windowClick(0, podSlot, 4, 2, mc.thePlayer);
                        tickDelay = 0;
                        stateAutoPod = StateAutoPod.SWAP2;

                    }
                    break;

                case SWAP2:
                    if (tickDelay >= 2) {
                        if (!(mc.currentScreen instanceof GuiInventory)) {
                            resetAutoPod();
                            alreadyDidPod = false;
                            break;
                        }
                        mc.playerController.windowClick(0, leggingSlot, 4, 2, mc.thePlayer); //7 is the pants slot id
                        tickDelay = 0;
                        stateAutoPod = StateAutoPod.CLOSE_INV1;
                    }
                    break;

                case CLOSE_INV1:
                    if (tickDelay >= 1) {
                        if (mc.currentScreen == null){
                            stateAutoPod = StateAutoPod.OPEN_INV2;
                            tickDelay = 0;
                            break;
                        }
                        mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(0));
                        mc.displayGuiScreen(null);
                        tickDelay = 0;
                        stateAutoPod = StateAutoPod.OPEN_INV2;
                    }
                    break;

                case OPEN_INV2:
                    if(tickDelay >= 1) {
                        BetterPit.inputBlock = false;
                        if (hasPodRegenEffect(mc.thePlayer)) {
                            if ((mc.currentScreen != null)||(mc.thePlayer.inventory.armorItemInSlot(1) == null)) {
                                if (mc.currentScreen instanceof GuiInventory && (mc.thePlayer.inventory.armorItemInSlot(1) != null)) {
                                    stateAutoPod = StateAutoPod.SWAP3;
                                    tickDelay = 0;
                                    break;
                                }
                                resetAutoPod();
                                alreadyDidPod = true;
                                break;
                            }
                            BetterPit.inputBlock = true;
                            mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                            mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
                            stateAutoPod = StateAutoPod.SWAP3;
                            tickDelay = 0;
                        }
                        break;
                    }

                case SWAP3:
                    if (tickDelay >= 2) {
                        if (!(mc.currentScreen instanceof GuiInventory)) {
                            resetAutoPod();
                            alreadyDidPod = true;
                            break;
                        }
                        if (oldInvSlot <= 8) {
                            mc.playerController.windowClick(0, leggingSlot, oldInvSlot, 2, mc.thePlayer);
                            tickDelay = 0;
                            stateAutoPod = StateAutoPod.CLOSE_INV2;
                            break;
                        }
                        mc.playerController.windowClick(0, leggingSlot, 4, 2, mc.thePlayer);
                        tickDelay = 0;
                        stateAutoPod = StateAutoPod.SWAP4;
                    }
                    break;

                case SWAP4:
                    if (tickDelay >= 2) {
                        if (!(mc.currentScreen instanceof GuiInventory)) {
                            resetAutoPod();
                            alreadyDidPod = true;
                            break;
                        }
                        mc.playerController.windowClick(0, oldInvSlot, 4, 2, mc.thePlayer);
                        tickDelay = 0;
                        stateAutoPod = StateAutoPod.CLOSE_INV2;
                    }
                    break;

                case CLOSE_INV2:
                    if (tickDelay >= 1) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(0));
                        mc.displayGuiScreen(null);
                        BetterPit.inputBlock = false;
                        resetAutoPod();
                    }
                    break;
            }
        }
    }
}
