package com.github.raimbowsix.betterpit;

import cc.polyfrost.oneconfig.utils.hypixel.LocrawInfo;
import cc.polyfrost.oneconfig.utils.hypixel.LocrawUtil;
import com.github.raimbowsix.betterpit.commands.AutoPodCommand;
import com.github.raimbowsix.betterpit.commands.Denick;
import com.github.raimbowsix.betterpit.commands.GetDisplayName;
import com.github.raimbowsix.betterpit.commands.GetEnchants;
import com.github.raimbowsix.betterpit.commands.GetNBT;
import com.github.raimbowsix.betterpit.commands.QuickMath;
import com.github.raimbowsix.betterpit.commands.SwapToDiamondPants;
import com.github.raimbowsix.betterpit.commands.Watchlist;
import com.github.raimbowsix.betterpit.config.ConfigOneConfig;
import com.github.raimbowsix.betterpit.modules.Bounties;
import com.github.raimbowsix.betterpit.modules.DarkPants;
import com.github.raimbowsix.betterpit.modules.Denicker;
import com.github.raimbowsix.betterpit.modules.Enemies;
import com.github.raimbowsix.betterpit.modules.Automation.AutoBulletTime;
import com.github.raimbowsix.betterpit.modules.Automation.AutoGhead;
import com.github.raimbowsix.betterpit.modules.Automation.AutoPod;
import com.github.raimbowsix.betterpit.modules.Automation.AutoQuickMath;
import com.github.raimbowsix.betterpit.modules.Automation.DiamondPantSwap;
import com.github.raimbowsix.betterpit.modules.Automation.RightClickSwap;
import com.github.raimbowsix.betterpit.render.NametagRenderer;
import com.github.raimbowsix.betterpit.render.ThreeDESP;
import com.github.raimbowsix.betterpit.render.TwoDESP;
import com.github.raimbowsix.betterpit.util.CacheManager;
import com.github.raimbowsix.betterpit.util.InputBlocker;
import com.github.raimbowsix.betterpit.util.WatchlistManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = BetterPit.MODID, name = BetterPit.NAME, version = BetterPit.VERSION, useMetadata = true)
public class BetterPit {
    public static final String MODID = "fentpit";
    public static final String NAME = "FentPit";
    public static final String VERSION = "1.2.2";
    public static ConfigOneConfig config;

    private static final Minecraft mc = Minecraft.getMinecraft();

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new Watchlist());
        ClientCommandHandler.instance.registerCommand(new GetNBT());
        ClientCommandHandler.instance.registerCommand(new GetDisplayName());
        ClientCommandHandler.instance.registerCommand(new Denick());
        ClientCommandHandler.instance.registerCommand(new GetEnchants());
        ClientCommandHandler.instance.registerCommand(new QuickMath());
        ClientCommandHandler.instance.registerCommand(new AutoPodCommand());
        ClientCommandHandler.instance.registerCommand(new SwapToDiamondPants());

        WatchlistManager.loadWatchlist();
        CacheManager.loadCache();

        registerModules();
        config = new ConfigOneConfig();
        System.out.println("Player Notifier Mod Initialized");
    }

    private void registerModules() {
        MinecraftForge.EVENT_BUS.register(new Enemies());
        MinecraftForge.EVENT_BUS.register(new DarkPants());
        MinecraftForge.EVENT_BUS.register(new Denicker());
        MinecraftForge.EVENT_BUS.register(new Bounties());

        MinecraftForge.EVENT_BUS.register(new AutoPod());
        MinecraftForge.EVENT_BUS.register(new DiamondPantSwap());
        MinecraftForge.EVENT_BUS.register(new RightClickSwap());
        MinecraftForge.EVENT_BUS.register(new AutoGhead());
        MinecraftForge.EVENT_BUS.register(new AutoBulletTime());
        MinecraftForge.EVENT_BUS.register(new AutoQuickMath());
        MinecraftForge.EVENT_BUS.register(new InputBlocker());

        MinecraftForge.EVENT_BUS.register(new NametagRenderer());
        MinecraftForge.EVENT_BUS.register(new TwoDESP());
        MinecraftForge.EVENT_BUS.register(new ThreeDESP());
    }

    public static void sendMessage(String msg) {
        EntityPlayerSP player = mc.thePlayer;
        if (player != null) {
            player.addChatMessage(new ChatComponentText(msg));
        }
    }

    public static String getMapName() {
        LocrawInfo locrawInfo = LocrawUtil.INSTANCE.getLocrawInfo();
        if (locrawInfo != null) {
            return locrawInfo.getMapName();
        }
        return "unknown";
    }

    public static boolean isInPit() {
        if (mc.theWorld == null || mc.thePlayer == null) return false;
        LocrawInfo locrawInfo = LocrawUtil.INSTANCE.getLocrawInfo();
        if (locrawInfo != null) {
            return locrawInfo.getGameMode().equals("PIT");
        }
        return false;
    }
}
