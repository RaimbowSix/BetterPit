package com.github.raimbowsix.betterpit.modules;

import com.github.raimbowsix.betterpit.config.ConfigOneConfig;
import com.github.raimbowsix.betterpit.util.Lobby;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bounties {
    private static final Pattern BOUNTY_PATTERN = Pattern.compile("§l(\\d+)g");
    public static Set<String> lastBountiedSet = new HashSet<>();

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!Lobby.shouldRun()) return;
        detect();
    }

    public static void detect() {
        Set<String> currentBountiedSet = new HashSet<>();
        for (EntityPlayer player : Lobby.playerEntities()) {
            String bounty = extractBounty(player.getDisplayName().getFormattedText());
            if (bounty != null && hasMinimumBounty(bounty)) {
                currentBountiedSet.add(player.getName());
            }
        }
        lastBountiedSet.clear();
        lastBountiedSet.addAll(currentBountiedSet);
    }

    private static String extractBounty(String input) {
        Matcher m = BOUNTY_PATTERN.matcher(input);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    private static boolean hasMinimumBounty(String bountyString) {
        try {
            return Integer.parseInt(bountyString) >= ConfigOneConfig.bountyMinPosition;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
