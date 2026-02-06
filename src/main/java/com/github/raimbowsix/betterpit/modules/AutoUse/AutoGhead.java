package com.github.raimbowsix.betterpit.modules.AutoUse;

import com.github.raimbowsix.betterpit.config.ConfigOneConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Random;

public class AutoGhead {
    public static Minecraft mc = Minecraft.getMinecraft();
    public static int oldSlot = -1;
    public static int gHeadSlot = -1;
    public static boolean didSwap = false;
    private static long swapTime = 0L;
    private static int tickDelay = 0;
    private static final Random random = new Random();
    private enum State {IDLE, SWAP, EAT, SWAPBACK}
    private static State state = State.IDLE;
    public static boolean isGhead(ItemStack item) {
        if (item == null || !item.hasTagCompound()) return false;
        NBTTagCompound tag = item.getTagCompound();
        if (tag.hasKey("display")) {
            NBTTagCompound display = tag.getCompoundTag("display");
            if (display.hasKey("Name")) {
                String displayName = display.getString("Name");
                return "§6Golden Head".equals(displayName);
            }
        }
        return false;
    }

    /* Auto GHead Use */
    public static void tryToGHead(){
        if (state==State.IDLE) {
            if (mc.theWorld!=null&&mc.thePlayer!=null&&ConfigOneConfig.autoGhead) {
                if (mc.thePlayer.getHealth()>=ConfigOneConfig.defaultHealthValueGhead) return;
                if (mc.thePlayer.getHealth()<=ConfigOneConfig.defaultHealthValueGhead){
                    state=State.SWAP;
                }
            }
        }
        if (state!=State.IDLE) {
            switch (state) {
                case SWAP:
                    if (mc.currentScreen != null) break;
                    gHeadSlot = -1;
                    for (int i = 0; i <= 8; i++) {
                        ItemStack item = mc.thePlayer.inventory.getStackInSlot(i);
                        if (isGhead(item)) {
                            oldSlot = mc.thePlayer.inventory.currentItem;
                            gHeadSlot = i;
                            break;
                        }
                    }

                    if (gHeadSlot != -1) {
                        mc.thePlayer.inventory.currentItem = gHeadSlot;
                        state = State.EAT;
                        tickDelay = 0;
                    }else state=State.IDLE;
                    break;
                case EAT:
                    tickDelay++;
                    if (tickDelay >= 3){
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                        if (tickDelay>=5){
                            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                            state = State.SWAPBACK;
                            tickDelay =0;
                        }
                    }
                    break;
                case SWAPBACK:
                    tickDelay++;
                    if (tickDelay >= 3){
                        mc.thePlayer.inventory.currentItem = oldSlot;
                        state = State.IDLE;
                        tickDelay =0;
                    }
                    break;
            }
        }
    }
    /* Auto GHead Use */
}