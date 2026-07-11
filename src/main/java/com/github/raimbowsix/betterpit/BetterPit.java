package com.github.raimbowsix.betterpit;

import cc.polyfrost.oneconfig.utils.hypixel.LocrawInfo;
import cc.polyfrost.oneconfig.utils.hypixel.LocrawUtil;
import com.github.raimbowsix.betterpit.commands.*;
import com.github.raimbowsix.betterpit.commands.AutoPodCommand;
import com.github.raimbowsix.betterpit.config.ConfigOneConfig;
import com.github.raimbowsix.betterpit.modules.*;
import com.github.raimbowsix.betterpit.modules.Automation.*;
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
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.Collection;
import java.util.List;

@Mod(modid = BetterPit.MODID, name = BetterPit.NAME, version = BetterPit.VERSION, useMetadata = true)
public class BetterPit {
    public static final String MODID = "fentpit";
    public static final String NAME = "FentPit";
    public static final String VERSION = "1.2.2";
    public static ConfigOneConfig config;

    private static final Minecraft mc = Minecraft.getMinecraft();
    public static boolean inputBlock = false;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        //commands
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

        MinecraftForge.EVENT_BUS.register(this);
        //Automation
        MinecraftForge.EVENT_BUS.register(new DiamondPantSwap());
        MinecraftForge.EVENT_BUS.register(new AutoPod());
        MinecraftForge.EVENT_BUS.register(new RightClickSwap());

        //Render
        MinecraftForge.EVENT_BUS.register(new NametagRenderer());
        MinecraftForge.EVENT_BUS.register(new TwoDESP());
        MinecraftForge.EVENT_BUS.register(new ThreeDESP());
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

    private boolean connected = false;
    @SubscribeEvent
    public void onConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (!connected) {
            connected = true;
        }
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        connected = false;
        AutoPod.stateAutoPod = AutoPod.StateAutoPod.IDLE;
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

            //Automation
            AutoPod.tryToEscapePod();
            DiamondPantSwap.tryToSwapIfVenomed();
            AutoGhead.tryToGHead();
            if (mc.thePlayer.isDead){
                AutoPod.alreadyDidPod=false;
                AutoPod.stateAutoPod = AutoPod.StateAutoPod.IDLE;
                sendMessage("[AutoPod] AutoPod conditional reset isDead");
            }
        }
    }

    @SubscribeEvent
    public void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.player == mc.thePlayer) {
            AutoPod.alreadyDidPod = false;
            AutoPod.stateAutoPod = AutoPod.StateAutoPod.IDLE;
            sendMessage("[AutoPod] AutoPod conditional reset onRespawn");
        }
    }
    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        if (event.entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entityLiving;
            if(mc.thePlayer == player){
                AutoPod.alreadyDidPod = false;
                AutoPod.stateAutoPod = AutoPod.StateAutoPod.IDLE;
                sendMessage("[AutoPod] AutoPod conditional reset onDeath");
            }
        }
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event){
        if (mc.thePlayer != null && mc.theWorld != null) {
            AutoQuickMath.solveQuickMath(event);
        }
    }

    //input blocking
    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        if (inputBlock) event.setCanceled(true);
    }
    @SubscribeEvent
    public void onMouse(InputEvent.MouseInputEvent event) {
        if (inputBlock) event.setCanceled(true);
    }
    @SubscribeEvent
    public void onGuiKey(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if (inputBlock) event.setCanceled(true);
    }
    @SubscribeEvent
    public void onGuiMouse(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (inputBlock) event.setCanceled(true);
    }


    @SubscribeEvent
    public void onMouseClick(MouseEvent event){
        RightClickSwap.tryToSwapItemInHand(event);
        AutoBulletTime.tryToBulletTime(event);
    }
}