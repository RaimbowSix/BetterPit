package com.github.raimbowsix.betterpit.modules.Automation;

import com.github.raimbowsix.betterpit.config.ConfigOneConfig;
import com.github.raimbowsix.betterpit.modules.DarkPants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import static com.github.raimbowsix.betterpit.config.ConfigOneConfig.swapBack;

public class DiamondPantSwap {
    private enum StateSwapIfVenomed { IDLE, OPEN_INV, SWAP1, SWAP2, SWAP3, SWAP4, CLOSE_INV}
    public static boolean alreadyDidSwap = false;
    private static StateSwapIfVenomed stateSwapIfVenomed = StateSwapIfVenomed.IDLE;
    private static int tickDelay = 0;
    int oldInvSlot = -1;

    static Minecraft mc = Minecraft.getMinecraft();

    public static boolean hasDiamondPantsInInv(){
        for (int i = 0; i <= 35; i++) {
            ItemStack item = mc.thePlayer.inventory.getStackInSlot(i);
            if (item != null && item.getItem() == Items.diamond_leggings) {
                return true;
            }
        }
        return false;
    }

    public static void tryToSwapIfVenomed(){
        if (!ConfigOneConfig.autoSwapIfVenomed || mc.currentScreen != null) return;
        EntityPlayer player = mc.thePlayer;
        boolean poison = player.getActivePotionEffect(Potion.poison) != null;
        if (player.inventory.armorItemInSlot(1) == null || DarkPants.hasDarks(player) || player.inventory.armorItemInSlot(1).getItem() != Items.leather_leggings) return;
        if (swapBack && alreadyDidSwap && !poison){
            DiamondPantSwap.start();
        }
        if (poison && mc.currentScreen==null && hasDiamondPantsInInv() && !alreadyDidSwap){
            alreadyDidSwap = true;
            DiamondPantSwap.start();
        }
    }
    public static void start() {
        if (stateSwapIfVenomed != StateSwapIfVenomed.IDLE) return;
        tickDelay = 0;
        stateSwapIfVenomed = StateSwapIfVenomed.OPEN_INV;
    }
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || mc.thePlayer == null) return;
        if (stateSwapIfVenomed != StateSwapIfVenomed.IDLE){
            tickDelay++;
            switch (stateSwapIfVenomed){
                case OPEN_INV:
                    if (tickDelay >= 0){
                        mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                        mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
                        if (swapBack && alreadyDidSwap && mc.thePlayer.getActivePotionEffect(Potion.poison)==null) {
                            stateSwapIfVenomed = StateSwapIfVenomed.SWAP3;
                            tickDelay = 0;
                            break;
                        }
                        stateSwapIfVenomed = StateSwapIfVenomed.SWAP1;
                        tickDelay = 0;
                    }
                    break;
                case SWAP1:
                    if (tickDelay >= 2) {
                        if (mc.thePlayer.openContainer==null){
                            stateSwapIfVenomed = StateSwapIfVenomed.IDLE;
                            break;
                        }
                        if (mc.currentScreen instanceof GuiInventory) {
                            int diamPantsSlot = -1;
                            int armorSlot = 7;
                            for (int i = 0; i <= 35; i++) {
                                ItemStack item = mc.thePlayer.inventory.getStackInSlot(i);
                                if (item != null && item.getItem() == Items.diamond_leggings) {
                                    diamPantsSlot=i;
                                    break;
                                }
                            }
                            if (diamPantsSlot==-1){
                                stateSwapIfVenomed=StateSwapIfVenomed.CLOSE_INV;
                                break;
                            }
                            if (diamPantsSlot<=8) {
                                mc.playerController.windowClick(0, armorSlot, diamPantsSlot, 2, mc.thePlayer);
                                oldInvSlot=diamPantsSlot;
                                tickDelay = 0;
                                stateSwapIfVenomed = StateSwapIfVenomed.CLOSE_INV;
                                break;
                            }
                            mc.playerController.windowClick(0, diamPantsSlot, 5, 2, mc.thePlayer);
                            oldInvSlot=diamPantsSlot;
                            tickDelay = 0;
                            stateSwapIfVenomed = StateSwapIfVenomed.SWAP2;
                        }
                    }
                    break;
                case SWAP2:
                    if (tickDelay >= 2) {
                        if (mc.thePlayer.openContainer==null){
                            stateSwapIfVenomed = StateSwapIfVenomed.IDLE;
                            break;
                        }
                        if (mc.currentScreen instanceof GuiInventory) {
                            int leggingsSlot = 7;
                            int diamPantsSlot = 5;
                            mc.playerController.windowClick(0, leggingsSlot, diamPantsSlot,2, mc.thePlayer);
                            tickDelay = 0;
                            stateSwapIfVenomed = StateSwapIfVenomed.CLOSE_INV;
                        }
                    }
                    break;
                case SWAP3:
                    if (tickDelay >=2) {
                        if (mc.thePlayer.openContainer==null){
                            stateSwapIfVenomed = StateSwapIfVenomed.IDLE;
                            break;
                        }
                        if (mc.currentScreen instanceof GuiInventory) {
                            int leggingsSlot = 7;
                            if (oldInvSlot<=8) {
                                mc.playerController.windowClick(0, leggingsSlot, oldInvSlot,2, mc.thePlayer);
                                stateSwapIfVenomed = StateSwapIfVenomed.CLOSE_INV;
                                alreadyDidSwap=false;
                                tickDelay = 0;
                                break;
                            }
                            mc.playerController.windowClick(0, leggingsSlot, 5,2, mc.thePlayer);
                            stateSwapIfVenomed = StateSwapIfVenomed.SWAP4;
                            tickDelay = 0;
                        }
                    }
                    break;
                case SWAP4:
                    if (tickDelay >=2) {
                        if (mc.thePlayer.openContainer==null){
                            stateSwapIfVenomed = StateSwapIfVenomed.IDLE;
                            break;
                        }
                        if (mc.currentScreen instanceof GuiInventory) {
                            mc.playerController.windowClick(0, oldInvSlot, 5,2, mc.thePlayer);
                            stateSwapIfVenomed = StateSwapIfVenomed.CLOSE_INV;
                            alreadyDidSwap=false;
                            tickDelay = 0;
                        }
                    }
                    break;
                case CLOSE_INV:
                    if (tickDelay >= 1) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(0));
                        mc.displayGuiScreen(null);
                        tickDelay = 0;
                        if (!swapBack){
                            stateSwapIfVenomed = StateSwapIfVenomed.IDLE;
                            break;
                        }
                    }
                    break;
            }
        }
    }
}