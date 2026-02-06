package com.github.raimbowsix.betterpit.hud;

import cc.polyfrost.oneconfig.hud.TextHud;
import com.github.raimbowsix.betterpit.BetterPit;
import com.github.raimbowsix.betterpit.config.ConfigOneConfig;
import com.github.raimbowsix.betterpit.modules.Enemies;
import com.github.raimbowsix.betterpit.util.PlayerLocation;
import net.minecraft.client.Minecraft;
import java.util.List;

public class EnemiesHud extends TextHud {
    public EnemiesHud() {
        super(true);
    }
    @Override
    protected void getLines(List<String> lines, boolean example) {
        if (ConfigOneConfig.whileInPit && !BetterPit.isInPit()) return;
        if (Enemies.lastEnemySet.isEmpty()) {
            lines.add("No enemies in lobby");
        } else {
            if (Minecraft.getMinecraft().theWorld==null)return;
            lines.add("§cEnemies: " + Enemies.lastEnemySet.toArray().length);
            for (final String name : Enemies.lastEnemySet) {
                Minecraft.getMinecraft().theWorld.playerEntities.stream()
                        .filter(player -> player.getName().equalsIgnoreCase(name))
                        .findFirst()
                        .ifPresent(player -> lines.add(player.getDisplayName().getFormattedText()
                                +PlayerLocation.getPlayerDistance(player.getName(), ConfigOneConfig.enemyDistance)));
            }
        }
    }
}
