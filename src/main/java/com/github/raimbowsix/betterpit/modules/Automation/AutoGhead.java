package com.github.raimbowsix.betterpit.modules.Automation;

import com.github.raimbowsix.betterpit.config.ConfigOneConfig;
import com.github.raimbowsix.betterpit.util.Lobby;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoGhead {
    private enum State {
        IDLE,
        SWAP_TO_HEAD,
        EAT,
        SWAP_BACK
    }

    private static State state = State.IDLE;
    private static int tickDelay = 0;
    private static int oldSlot = -1;
    private static int gHeadSlot = -1;

    public static boolean isGhead(ItemStack item) {
        if (item == null || !item.hasTagCompound()) return false;
        NBTTagCompound tag = item.getTagCompound();
        if (tag.hasKey("display")) {
            NBTTagCompound display = tag.getCompoundTag("display");
            if (display.hasKey("Name")) {
                return "§6Golden Head".equals(display.getString("Name"));
            }
        }
        return false;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || !Lobby.isReady()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (state == State.IDLE) {
            if (ConfigOneConfig.autoGhead && mc.thePlayer.getHealth() < ConfigOneConfig.defaultHealthValueGhead) {
                state = State.SWAP_TO_HEAD;
            }
            return;
        }
        switch (state) {
            case SWAP_TO_HEAD:
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
                } else {
                    state = State.IDLE;
                }
                break;

            case EAT:
                tickDelay++;
                if (tickDelay >= 3) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                }
                if (tickDelay >= 5) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                    state = State.SWAP_BACK;
                    tickDelay = 0;
                }
                break;

            case SWAP_BACK:
                tickDelay++;
                if (tickDelay >= 3) {
                    mc.thePlayer.inventory.currentItem = oldSlot;
                    gHeadSlot = -1;
                    state = State.IDLE;
                    tickDelay = 0;
                }
                break;
        }
    }
}
