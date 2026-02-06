package com.github.raimbowsix.betterpit.modules.AutoUse;

import com.github.raimbowsix.betterpit.config.ConfigOneConfig;
import com.github.raimbowsix.betterpit.util.GetEnchants;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraftforge.client.event.MouseEvent;
import java.util.Random;

public class AutoBulletTime {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static int oldSlot = -1;
    private static boolean didSwap = false;
    private static long swapTime = 0L;
    private static int randomizedDelay = 0;

    private static final Random random = new Random();

    public static void tryToBulletTime(MouseEvent event) {
        if (!ConfigOneConfig.autoBulletTime || mc.currentScreen != null || mc.thePlayer == null) return;

        EntityPlayer player = mc.thePlayer;
        if (event.button == 1 && event.buttonstate) {
            ItemStack heldItem = player.getHeldItem();
            if (heldItem != null && heldItem.getItem() instanceof ItemSword && GetEnchants.hasEnchant(heldItem,"blocking_cancels_projectiles")) {
                for (int i = 0; i <= 8; i++) {
                    ItemStack item = player.inventory.getStackInSlot(i);
                    if (item != null && GetEnchants.hasEnchant(item,"blocking_cancels_projectiles")) {
                        oldSlot = player.inventory.currentItem;
                        player.inventory.currentItem = i;
                        mc.playerController.updateController();
                        heldItem = player.getHeldItem();
                        mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, heldItem);
                        didSwap = true;
                        swapTime = System.currentTimeMillis();
                        randomizedDelay = 100 + random.nextInt(151);
                        break;
                    }
                }
            }
        }
        if (event.button == 1 && !event.buttonstate && didSwap && oldSlot != -1) {
            long elapsed = System.currentTimeMillis() - swapTime;
            if (elapsed >= randomizedDelay) {
                player.inventory.currentItem = oldSlot;
                mc.playerController.updateController();
                player.swingItem();
                didSwap = false;
                oldSlot = -1;
                randomizedDelay = 0;
            }
        }
    }
}
