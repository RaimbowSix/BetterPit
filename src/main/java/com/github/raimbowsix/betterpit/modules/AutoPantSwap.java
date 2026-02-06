package com.github.raimbowsix.betterpit.modules;

import com.github.raimbowsix.betterpit.config.ConfigOneConfig;
import com.github.raimbowsix.betterpit.util.GetEnchants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.potion.Potion;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import static com.github.raimbowsix.betterpit.BetterPit.isInPit;
import static com.github.raimbowsix.betterpit.config.ConfigOneConfig.swapBack;

public class AutoPantSwap {
    private enum State { IDLE, OPEN_INV, SWAP, CLOSE_INV }
    private enum StateAutoPod { IDLE, OPEN_INV, SWAP1, SWAP2, SWAP3, SWAP4, CLOSE_INV }
    private enum StateSwapIfVenomed { IDLE, OPEN_INV, SWAP1, SWAP2, SWAP3, SWAP4, CLOSE_INV}
    public static boolean alreadyDidPod = false;
    public static boolean alreadyDidSwap = false;
    private static StateSwapIfVenomed stateSwapIfVenomed = StateSwapIfVenomed.IDLE;
    private static StateAutoPod stateAutoPod = StateAutoPod.IDLE;
    private static State state = State.IDLE;
    private static int tickDelay = 0;
    private static int tickDelay2 = 0;
    private static int tickDelay3 = 0;
    int oldInvSlot = -1;

    static Minecraft mc = Minecraft.getMinecraft();

    public static boolean hasPodInInv(){
        for (int i = 0; i <= 35; i++) {
            ItemStack item = mc.thePlayer.inventory.getStackInSlot(i);
            if (item != null && GetEnchants.hasEnchant(item,"escape_pod")) {
                return true;
            }
        }
        return false;
    }
    public static boolean hadDiamondPantsInInv(){
        for (int i = 0; i <= 35; i++) {
            ItemStack item = mc.thePlayer.inventory.getStackInSlot(i);
            if (item != null && item.getItem() == Items.diamond_leggings) {
                return true;
            }
        }
        return false;
    }
    public static boolean hasPodRegenEffect(EntityPlayer player){
        for (int i = 1; i<=3; i++) {
            if (player.getActivePotionEffect(Potion.regeneration) != null) {
                if (player.getActivePotionEffect(Potion.regeneration).getAmplifier() == i) {
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
        if (player == null || player.inventory.armorItemInSlot(1) == null) return;
        if (mc.thePlayer.getHealth()<ConfigOneConfig.defaultHealthValuePod && !alreadyDidPod && mc.currentScreen==null && hasPodInInv() && player.getActivePotionEffect(Potion.poison)==null){
            alreadyDidPod = true;
            AutoPantSwap.start(2);
        }
    }
    public static void tryToSwapIfVenomed(){
        if (!ConfigOneConfig.autoSwapIfVenomed || mc.currentScreen != null) return;
        EntityPlayer player = mc.thePlayer;
        boolean poison = player.getActivePotionEffect(Potion.poison) != null;
        if (player.inventory.armorItemInSlot(1) == null || DarkPants.hasDarks(player) || player.inventory.armorItemInSlot(1).getItem() != Items.leather_leggings) return;
        if (swapBack && alreadyDidSwap && !poison){
            AutoPantSwap.start(3);
        }
        if (poison && mc.currentScreen==null && hadDiamondPantsInInv() && !alreadyDidSwap){
            alreadyDidSwap = true;
            AutoPantSwap.start(3);
        }
    }
    public static void tryToSwapLeggingsInHand(MouseEvent event) {
        if (!ConfigOneConfig.rightClickPantSwap || mc.currentScreen != null || event.button != 1 || !event.buttonstate) return;
        EntityPlayer player = mc.thePlayer;
        if (player == null || player.inventory.armorItemInSlot(1) == null) return;
        ItemStack heldLeggings = player.inventory.getStackInSlot(player.inventory.currentItem);
        if (heldLeggings == null || !(heldLeggings.getItem() instanceof ItemArmor)) return;
        ItemArmor armor = (ItemArmor) heldLeggings.getItem();
        if (armor.armorType != 2) return;
        AutoPantSwap.start(1);
    }
    public static void start(int swap) {
        switch (swap){
            case 1:
                if (state != State.IDLE) return;
                tickDelay = 0;
                state = State.OPEN_INV;
                break;
            case 2:
                if (stateAutoPod != StateAutoPod.IDLE) return;
                tickDelay2 = 0;
                stateAutoPod = StateAutoPod.OPEN_INV;
                break;
            case 3:
                if (stateSwapIfVenomed != StateSwapIfVenomed.IDLE) return;
                tickDelay3 = 0;
                stateSwapIfVenomed = StateSwapIfVenomed.OPEN_INV;
                break;
        }
    }
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || mc.thePlayer == null) return;
        if (stateSwapIfVenomed != StateSwapIfVenomed.IDLE){
            tickDelay3++;
            switch (stateSwapIfVenomed){
                case OPEN_INV:
                    if (tickDelay3 >= 0){
                        mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                        mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
                        if (swapBack && alreadyDidSwap && mc.thePlayer.getActivePotionEffect(Potion.poison)==null) {
                            stateSwapIfVenomed = StateSwapIfVenomed.SWAP3;
                            tickDelay3 = 0;
                            break;
                        }
                        stateSwapIfVenomed = StateSwapIfVenomed.SWAP1;
                        tickDelay3 = 0;
                    }
                    break;
                case SWAP1:
                    if (tickDelay3 >= 3) {
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
                                tickDelay3 = 0;
                                stateSwapIfVenomed = StateSwapIfVenomed.CLOSE_INV;
                                break;
                            }
                            mc.playerController.windowClick(0, diamPantsSlot, 5, 2, mc.thePlayer);
                            oldInvSlot=diamPantsSlot;
                            tickDelay3 = 0;
                            stateSwapIfVenomed = StateSwapIfVenomed.SWAP2;
                        }
                    }
                    break;
                case SWAP2:
                    if (tickDelay3 >= 3) {
                        if (mc.thePlayer.openContainer==null){
                            stateSwapIfVenomed = StateSwapIfVenomed.IDLE;
                            break;
                        }
                        if (mc.currentScreen instanceof GuiInventory) {
                            int leggingsSlot = 7;
                            int diamPantsSlot = 5;
                            mc.playerController.windowClick(0, leggingsSlot, diamPantsSlot,2, mc.thePlayer);
                            tickDelay3 = 0;
                            stateSwapIfVenomed = StateSwapIfVenomed.CLOSE_INV;
                        }
                    }
                    break;
                case SWAP3:
                    if (tickDelay >=3) {
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
                                tickDelay3 = 0;
                                break;
                            }
                            mc.playerController.windowClick(0, leggingsSlot, 5,2, mc.thePlayer);
                            stateSwapIfVenomed = StateSwapIfVenomed.SWAP4;
                            tickDelay3 = 0;
                        }
                    }
                    break;
                case SWAP4:
                    if (tickDelay >=3) {
                        if (mc.thePlayer.openContainer==null){
                            stateSwapIfVenomed = StateSwapIfVenomed.IDLE;
                            break;
                        }
                        if (mc.currentScreen instanceof GuiInventory) {
                            mc.playerController.windowClick(0, oldInvSlot, 5,2, mc.thePlayer);
                            stateSwapIfVenomed = StateSwapIfVenomed.CLOSE_INV;
                            alreadyDidSwap=false;
                            tickDelay3 = 0;
                        }
                    }
                    break;
                case CLOSE_INV:
                    if (tickDelay2 >= 1) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(0));
                        mc.displayGuiScreen(null);
                        tickDelay3 = 0;
                        if (!swapBack){
                            stateSwapIfVenomed = StateSwapIfVenomed.IDLE;
                            break;
                        }
                    }
                    break;
            }
        }

        if (stateAutoPod != StateAutoPod.IDLE){
            tickDelay2++;
            switch (stateAutoPod) {
                case OPEN_INV:
                    if (tickDelay2 >= 0) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                        mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
                        stateAutoPod = StateAutoPod.SWAP1;
                        tickDelay2 = 0;
                    }
                    break;
                case SWAP1:
                    if (tickDelay2 >= 3) {
                        if (mc.thePlayer.openContainer==null){
                            stateAutoPod = StateAutoPod.IDLE;
                            break;
                        }
                        if (mc.currentScreen instanceof GuiInventory) {
                            int podSlot = -1;
                            int leggingSlot = 7;
                            for (int i = 0; i <= 35; i++) {
                                ItemStack item = mc.thePlayer.inventory.getStackInSlot(i);
                                if (item != null && GetEnchants.hasEnchant(item,"escape_pod")) {
                                    podSlot=i;
                                    break;
                                }
                            }
                            if (podSlot==-1){
                                stateAutoPod=StateAutoPod.CLOSE_INV;
                                break;
                            }
                            oldInvSlot=podSlot;
                            if (podSlot<=8) {
                                mc.playerController.windowClick(0, leggingSlot, podSlot, 2, mc.thePlayer);
                                tickDelay2 = 0;
                                stateAutoPod = StateAutoPod.SWAP3;
                                break;
                            }
                            mc.playerController.windowClick(0, podSlot, 5, 2, mc.thePlayer);
                            tickDelay2 = 0;
                            stateAutoPod = StateAutoPod.SWAP2;
                        }
                    }
                    break;
                case SWAP2:
                    if (tickDelay2 >= 2) {
                        if (mc.thePlayer.openContainer==null){
                            stateAutoPod = StateAutoPod.IDLE;
                            break;
                        }
                        if (mc.currentScreen instanceof GuiInventory) {
                            mc.playerController.windowClick(0, 7, 5,2, mc.thePlayer); //7 is the pants slot id
                            tickDelay2 = 0;
                            stateAutoPod = StateAutoPod.SWAP3;
                        }
                    }
                    break;
                case SWAP3:
                    if (hasPodRegenEffect(mc.thePlayer) && tickDelay2>=3){
                        if (mc.thePlayer.openContainer==null){
                            stateAutoPod = StateAutoPod.IDLE;
                            break;
                        }
                        if (mc.currentScreen instanceof GuiInventory) {
                            if (oldInvSlot<=8){
                                mc.playerController.windowClick(0, 7, oldInvSlot,2, mc.thePlayer);
                                oldInvSlot =-1;
                                tickDelay2 = 0;
                                stateAutoPod = StateAutoPod.CLOSE_INV;
                                break;
                            }
                            mc.playerController.windowClick(0, 7, 5,2, mc.thePlayer);
                            tickDelay2 = 0;
                            stateAutoPod = StateAutoPod.SWAP4;
                        }
                    }
                    break;
                case SWAP4:
                    if (tickDelay2>=3) {
                        if (mc.thePlayer.openContainer == null) {
                            stateAutoPod = StateAutoPod.IDLE;
                            break;
                        }
                        if (mc.currentScreen instanceof GuiInventory){
                            mc.playerController.windowClick(0, oldInvSlot, 5,2, mc.thePlayer);
                            oldInvSlot =-1;
                            tickDelay2 = 0;
                            stateAutoPod = StateAutoPod.CLOSE_INV;
                        }
                    }
                    break;
                case CLOSE_INV:
                    if (tickDelay2 >= 1) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(0));
                        mc.displayGuiScreen(null);
                        tickDelay2 = 0;
                        stateAutoPod = StateAutoPod.IDLE;
                    }
                    break;
            }
        }

        if (state == State.IDLE) return;
        tickDelay++;
        switch (state) {
            case OPEN_INV:
                if (tickDelay >= 0) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                    mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
                    state = State.SWAP;
                    tickDelay = 0;
                }
                break;
            case SWAP:
                if (tickDelay >= 3) {
                    if (mc.thePlayer.openContainer==null){
                        state = State.IDLE;
                        break;
                    }
                    if (mc.currentScreen instanceof GuiInventory) {
                        int armorSlot = 7;
                        int heldItemSlot = mc.thePlayer.inventory.currentItem;
                        mc.playerController.windowClick(0, armorSlot, heldItemSlot,2, mc.thePlayer);
                        tickDelay = 0;
                        state = State.CLOSE_INV;
                    }
                }
                break;
            case CLOSE_INV:
                if (tickDelay >= 1) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(0));
                    mc.displayGuiScreen(null);
                    tickDelay = 0;
                    state = State.IDLE;
                }
                break;
        }
    }
}