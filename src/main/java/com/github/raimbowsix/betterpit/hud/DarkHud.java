package com.github.raimbowsix.betterpit.hud;

import cc.polyfrost.oneconfig.hud.TextHud;
import com.github.raimbowsix.betterpit.BetterPit;
import com.github.raimbowsix.betterpit.config.ConfigOneConfig;
import com.github.raimbowsix.betterpit.modules.DarkPants;
import com.github.raimbowsix.betterpit.util.GetEnchants;
import com.github.raimbowsix.betterpit.util.PlayerLocation;
import net.minecraft.client.Minecraft;
import java.util.ArrayList;
import java.util.List;


public class DarkHud extends TextHud {
    public DarkHud(){super(true);}
    @Override
    protected void getLines(List<String> lines, boolean example) {
        if (ConfigOneConfig.whileInPit && !BetterPit.isInPit()) return;
        if (DarkPants.lastDarkSet.isEmpty()) {
            lines.add("No darks in lobby");
        } else {
            ArrayList<String> Darks = new ArrayList<>();
            if (Minecraft.getMinecraft().theWorld!=null && Minecraft.getMinecraft().theWorld.playerEntities != null)
                Minecraft.getMinecraft().theWorld.playerEntities.stream()
                    .filter(DarkPants::hasDarks)
                    .forEach(p->{
                        if (!GetEnchants.getDarkPantsEnchantFromName(p).equals("FRESH")) {
                            Darks.add(p.getDisplayName().getFormattedText() + " " + GetEnchants.getDarkPantsEnchantFromName(p) + PlayerLocation.getPlayerDistance(p.getName(), ConfigOneConfig.darkDistance));
                        }
                });
            lines.add("§5Darks: " + Darks.size());
            lines.addAll(Darks);
        }
    }
}
