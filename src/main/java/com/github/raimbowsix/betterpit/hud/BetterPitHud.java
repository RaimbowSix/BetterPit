package com.github.raimbowsix.betterpit.hud;

import cc.polyfrost.oneconfig.hud.TextHud;
import com.github.raimbowsix.betterpit.BetterPit;
import com.github.raimbowsix.betterpit.config.ConfigOneConfig;
import net.minecraft.client.Minecraft;
import java.util.List;

public class BetterPitHud extends TextHud {
    public BetterPitHud() {
        super(true);
    }
    @Override
    protected void getLines(List<String> lines, boolean example) {
        if (ConfigOneConfig.whileInPit && !BetterPit.isInPit()) return;
        lines.add("§7[§6BetterPit§7] §31.2.2§r "+Minecraft.getDebugFPS()+" fps");
    }
}