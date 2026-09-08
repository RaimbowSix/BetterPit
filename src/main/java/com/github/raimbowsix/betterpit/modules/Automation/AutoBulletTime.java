package com.github.raimbowsix.betterpit.modules.Automation;

import com.github.raimbowsix.betterpit.config.ConfigOneConfig;
import com.github.raimbowsix.betterpit.util.GetEnchants;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

public class AutoBulletTime {
    private static final String BULLET_TIME_ENCHANT = "blocking_cancels_projectiles";
    private static final Random random = new Random();

    private static int oldSlot = -1;
    private static boolean didSwap = false;
    private static long swapTime = 0L;
    private static int randomizedDelay = 0;

    @SubscribeEvent
    public void onMouseClick(MouseEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (!ConfigOneConfig.autoBulletTime || mc.thePlayer == null || mc.currentScreen != null) return;
        if (event.button != 1) return;
        if (event.buttonstate) {
            swapToBulletTime(mc, mc.thePlayer);
            return;
        }
        if (didSwap && oldSlot != -1 && System.currentTimeMillis() - swapTime >= randomizedDelay) {
            mc.thePlayer.inventory.currentItem = oldSlot;
            mc.playerController.updateController();
            mc.thePlayer.swingItem();
            didSwap = false;
            oldSlot = -1;
            randomizedDelay = 0;
        }
    }

    private static void swapToBulletTime(Minecraft mc, EntityPlayer player) {
        ItemStack heldItem = player.getHeldItem();
        if (heldItem == null || !(heldItem.getItem() instanceof ItemSword)
                || !GetEnchants.hasEnchant(heldItem, BULLET_TIME_ENCHANT)) return;
        for (int i = 0; i <= 8; i++) {
            ItemStack item = player.inventory.getStackInSlot(i);
            if (item != null && GetEnchants.hasEnchant(item, BULLET_TIME_ENCHANT)) {
                oldSlot = player.inventory.currentItem;
                player.inventory.currentItem = i;
                mc.playerController.updateController();
                mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, player.getHeldItem());
                didSwap = true;
                swapTime = System.currentTimeMillis();
                randomizedDelay = 100 + random.nextInt(151);
                break;
            }
        }
    }
}
