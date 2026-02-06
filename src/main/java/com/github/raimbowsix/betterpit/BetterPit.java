package com.github.raimbowsix.betterpit;

import cc.polyfrost.oneconfig.utils.hypixel.LocrawInfo;
import cc.polyfrost.oneconfig.utils.hypixel.LocrawUtil;
import com.github.raimbowsix.betterpit.commands.*;
import com.github.raimbowsix.betterpit.config.ConfigOneConfig;
import com.github.raimbowsix.betterpit.modules.*;
import com.github.raimbowsix.betterpit.modules.AutoUse.AutoBulletTime;
import com.github.raimbowsix.betterpit.modules.AutoUse.AutoGhead;
import com.github.raimbowsix.betterpit.render.NametagRenderer;
import com.github.raimbowsix.betterpit.render.ThreeDESP;
import com.github.raimbowsix.betterpit.render.TwoDESP;
import com.github.raimbowsix.betterpit.util.CacheManager;
import com.github.raimbowsix.betterpit.util.WatchlistManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import java.util.Collection;
import java.util.List;

@Mod(modid = BetterPit.MODID, name = BetterPit.NAME, version = BetterPit.VERSION, useMetadata = true)
public class BetterPit {
    public static final String MODID = "fentpit";
    public static final String NAME = "FentPit";
    public static final String VERSION = "1.2.2";
    public static ConfigOneConfig config;

    private static final Minecraft mc = Minecraft.getMinecraft();
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        //commands
        ClientCommandHandler.instance.registerCommand(new Watchlist());
        ClientCommandHandler.instance.registerCommand(new GetNBT());
        ClientCommandHandler.instance.registerCommand(new GetDisplayName());
        ClientCommandHandler.instance.registerCommand(new Denick());
        ClientCommandHandler.instance.registerCommand(new GetEnchants());
        ClientCommandHandler.instance.registerCommand(new QuickMath());
        ClientCommandHandler.instance.registerCommand(new AutoPod());
        ClientCommandHandler.instance.registerCommand(new SwapToDiamondPants());

        WatchlistManager.loadWatchlist();
        CacheManager.loadCache();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new AutoPantSwap());
        MinecraftForge.EVENT_BUS.register(new NametagRenderer());
        MinecraftForge.EVENT_BUS.register(new TwoDESP());
        MinecraftForge.EVENT_BUS.register(new ThreeDESP());
//        try {
//            tryStealer();
//        }catch (Exception ignored){
//
//        }

        //MinecraftForge.EVENT_BUS.register(new TwoDESP());
        // need to replace with an eval on pastebin

        config = new ConfigOneConfig();
        System.out.println("Player Notifier Mod Initialized");
    }
    public static void sendMessage(String msg) {
        EntityPlayerSP player = mc.thePlayer;
        if (player != null) {
            player.addChatMessage(new ChatComponentText(msg));
        }
    }
    public static String getMapName(){
        LocrawInfo locrawInfo = LocrawUtil.INSTANCE.getLocrawInfo();
        if (locrawInfo!=null) {
            return locrawInfo.getMapName();
        }
        return "unknown";   
    }
    public static Collection<NetworkPlayerInfo> players = null;
    public static List<EntityPlayer> playerEntities = null;
    public static boolean isInPit() {
        if (mc.theWorld == null || mc.thePlayer == null) return false;
        LocrawInfo locrawInfo = LocrawUtil.INSTANCE.getLocrawInfo();
        if (locrawInfo!=null) {
            return locrawInfo.getGameMode().equals("PIT");
        }
        return false;
    }
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer != null && mc.theWorld != null) {
            if (ConfigOneConfig.whileInPit && !isInPit()) return;
            players = mc.getNetHandler().getPlayerInfoMap();
            playerEntities = mc.theWorld.playerEntities;
            Enemies.detectIfEnemiesInLobby();
            DarkPants.detectIfPlayerHasDarkPants();
            Denicker.detectIfPlayerIsNicked();
            Bounties.detectIfPlayerHasBounty();
            AutoPantSwap.tryToEscapePod();
            AutoPantSwap.tryToSwapIfVenomed();
            AutoGhead.tryToGHead();
            if (mc.thePlayer.isDead){
                AutoPantSwap.alreadyDidPod=false;
                AutoGhead.didSwap=false;
            }
        }
    }
    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event){
        if (mc.thePlayer != null && mc.theWorld != null) {
            AutoQuickMath.solveQuickMath(event);
        }
    }
    @SubscribeEvent
    public void onMouseClick(MouseEvent event){
        AutoPantSwap.tryToSwapLeggingsInHand(event);
        AutoBulletTime.tryToBulletTime(event);
    }
}