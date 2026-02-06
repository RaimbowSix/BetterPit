package com.github.raimbowsix.betterpit.hud;

import cc.polyfrost.oneconfig.hud.TextHud;
import com.github.raimbowsix.betterpit.BetterPit;
import com.github.raimbowsix.betterpit.config.ConfigOneConfig;
import com.github.raimbowsix.betterpit.modules.Denicker;
import com.github.raimbowsix.betterpit.util.CacheManager;
import com.github.raimbowsix.betterpit.util.PlayerLocation;
import net.minecraft.client.Minecraft;
import java.util.List;

public class NickedHud extends TextHud {
    public NickedHud() {
        super(true);
    }
    @Override
    protected void getLines(List<String> lines, boolean example) {
        if (ConfigOneConfig.whileInPit && !BetterPit.isInPit()) return;
        if (Denicker.lastNickedSet.isEmpty()) {
            lines.add("No nicked in lobby");
        } else {
            if (Minecraft.getMinecraft().theWorld==null)return;
            lines.add("§bNicked: " + Denicker.lastNickedSet.toArray().length);
            for (final String name : Denicker.lastNickedSet) {
                Minecraft.getMinecraft().theWorld.playerEntities.stream()
                        .filter(player -> player.getName().equalsIgnoreCase(name))
                        .findFirst()
                        .ifPresent(player -> lines.add(player.getDisplayName().getFormattedText()
                                +denickedFormat(name)
                                +PlayerLocation.getPlayerDistance(player.getName(), ConfigOneConfig.nickedDistance)));
            }
        }
    }
    public String denickedFormat(String nick){
        if (CacheManager.getFromCache(nick)==null) return "";
        String realname = CacheManager.getFromCache(nick);
        //PlayerNotifier.sendMessage("Found from cache §b"+nick+"§r");
        return " -> §e"+ realname+"§r";
    }
}