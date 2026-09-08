package com.github.raimbowsix.betterpit.modules.Automation;

import com.github.raimbowsix.betterpit.BetterPit;
import com.github.raimbowsix.betterpit.config.ConfigOneConfig;
import com.github.raimbowsix.betterpit.util.Lobby;
import com.github.raimbowsix.betterpit.util.MathSolver;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoQuickMath {
    private static final String PREFIX = "QUICK MATHS! Solve: ";

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (!ConfigOneConfig.quickMath || !Lobby.isReady()) return;
        Minecraft mc = Minecraft.getMinecraft();
        String rawMessage = event.message.getUnformattedText();
        if (!rawMessage.startsWith(PREFIX)) return;
        if (mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat)) return;

        String mathProblem = rawMessage.substring(PREFIX.length());
        long millisStarted = System.currentTimeMillis();
        new Thread(() -> {
            try {
                Thread.sleep(ConfigOneConfig.getQuickMathMinDelay + (int) (Math.random() * 50));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            long millisToSolve = System.currentTimeMillis() - millisStarted;
            BetterPit.sendMessage("§7[§cBetterPit§7] §aSolved this math problem §7(" + millisToSolve + "ms)");
            Minecraft.getMinecraft().thePlayer.sendChatMessage("" + MathSolver.solve(mathProblem));
        }).start();
    }
}
