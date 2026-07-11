package com.github.raimbowsix.betterpit.config;

import cc.polyfrost.oneconfig.config.annotations.*;
import com.github.raimbowsix.betterpit.BetterPit;
//hud
import com.github.raimbowsix.betterpit.hud.*;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.config.data.OptionSize;
import com.github.raimbowsix.betterpit.modules.Automation.AutoPod;


public class ConfigOneConfig extends Config {
    //categories
    private static final transient String BETTERPIT = "BetterPit";
    private static final transient String ENEMIES = "Enemies";
    private static final transient String DARK = "Darks";
    private static final transient String DENICKER = "Denicker";
    private static final transient String BOUNTIES = "Bounties";
    private static final transient String QUICKMATH = "QuickMath";
    private static final transient String AUTOMATION = "Automation";
    private static final transient String DEBUG = "Debug";


    private static final transient String BETTERPITMISC = "Optional";
    private static final transient String ENEMIESOPTIONAL = "Optional";
    private static final transient String DARKOPTIONAL = "Optional";
    private static final transient String DENICKEROPTIONAL = "Optional";
    private static final transient String BOUNTIESOPTIONAL = "Optional";
    private static final transient String AUTOBULLETTIME = "AutoBulletTime";
    private static final transient String AUTOPOD = "AutoPod";
    private static final transient String RIGHTCLICKPANTSWAP = "RightClickSwap";
    private static final transient String VENOMCOUNTER = "VenomCounter";
    private static final transient String AUTOGHEAD = "AutoGhead";

    //BetterPit
    @HUD(
            name = "Fent Pit HUD",
            category = BETTERPIT
    )
    public static BetterPitHud betterPitHud = new BetterPitHud();
    //Bountied Players
    @HUD(
            name = "Bountied Players HUD",
            category = BOUNTIES
    )
    public static BountiesHud bountiesHud = new BountiesHud();
    @Switch(
            name = "Display Distance",
            category = BOUNTIES,
            subcategory = BOUNTIESOPTIONAL
    )
    public static boolean bountyDistance = true;
    @Switch(
            name = "Display Position",
            category = BOUNTIES,
            subcategory = BOUNTIESOPTIONAL
    )
    public static boolean bountyPosition = true;
    @Slider(
            name = "Minimal Bounty",
            category = BOUNTIES,
            subcategory = BOUNTIESOPTIONAL,
            step = 200,
            min = 0,
            max = 5000
    )
    public static int bountyMinPosition = 600;
    //AutoSwapIfVenomed
    @Switch(
            name = "Swap To Diamond Pants When Venomed",
            category = AUTOMATION,
            subcategory = VENOMCOUNTER
    )
    public static boolean autoSwapIfVenomed = false;
    @Switch(
            name = "Swap Back",
            category = AUTOMATION,
            subcategory = VENOMCOUNTER
    )
    public static boolean swapBack = false;
    //AutoPod
    @Switch(
            name = "Toggle Auto Pod",
            category = AUTOMATION,
            subcategory = AUTOPOD
    )
    public static boolean autoPod = false;
    @Switch(
            name = "Show Inventory",
            category = AUTOMATION,
            subcategory = AUTOPOD
    )
    public static boolean autoPodShowInv = true;
    @Button(
            name = "",
            text = "Rearm",
            description = "Escape pod is normally usable once per life but if you need to rearm it press the button",
            category = AUTOMATION,
            subcategory = AUTOPOD
    )
    public void rearmAutoPod(){
        AutoPod.alreadyDidPod=false;
        BetterPit.sendMessage("§7[§6BetterPit§7] §rAutoPod has been re-armed.");
    }

    @Slider(
            name = "Swap When Health = ",
            category = AUTOMATION,
            subcategory = AUTOPOD,
            min = 0F,
            max = 20F
    )
    public static float defaultHealthValuePod = 5F;

    @Switch(
            name = "Toggle right click armor swapping",
            size = OptionSize.SINGLE,
            category = AUTOMATION,
            subcategory = RIGHTCLICKPANTSWAP
    )
    public static boolean rightClickPantSwap = false;
    //AutoEatGoldenHeads
    @Switch(
            name = "Toggle AutoGhead",
            size = OptionSize.SINGLE,
            category = AUTOMATION,
            subcategory = AUTOGHEAD
    )
    public static boolean autoGhead = false;
    @Slider(
            name = "Swap When Health = ",
            category = AUTOMATION,
            subcategory = AUTOGHEAD,
            min = 0F,
            max = 20F
    )
    public static float defaultHealthValueGhead = 10F;
    //AutoBulletTime
    @Switch(
            name = "Toggle AutoBulletTime",
            size = OptionSize.SINGLE,
            category = AUTOMATION,
            subcategory = AUTOBULLETTIME
    )
    public static boolean autoBulletTime = false;
    //QuickMath
    @Switch(
            name = "Toggle QuickMath",
            size = OptionSize.SINGLE,
            category = QUICKMATH
    )
    public static boolean quickMath = true;
    @Slider(
            name = "QuickMath Minimum Delay",
            category = QUICKMATH,
            min = 500,
            max = 3000,
            description = "delay to avoid getting banned like a bozo"
    )
    public static int getQuickMathMinDelay = 1900;
    //Dark Pants
    @HUD(
            name = "Dark Pants HUD",
            category = DARK
    )
    public static DarkHud darkHud = new DarkHud();
    @Switch(
            name = "Display Distance",
            size = OptionSize.SINGLE,
            category = DARK,
            subcategory = DARKOPTIONAL
    )
    public static boolean darkDistance = true;
    @Switch(
            name = "Display Position",
            size = OptionSize.SINGLE,
            category = DARK,
            subcategory = DARKOPTIONAL
    )
    public static boolean darkPosition = true;

    //Nicked Players
    @HUD(
            name = "Nicked Players HUD",
            category = DENICKER
    )
    public static NickedHud nickedHud = new NickedHud();

    @Switch(
            name = "Display Distance",
            size = OptionSize.SINGLE,
            category = DENICKER,
            subcategory = DENICKEROPTIONAL
    )
    public static boolean nickedDistance = true;
    @Switch(
            name = "Display Position",
            size = OptionSize.SINGLE,
            category = DENICKER,
            subcategory = DENICKEROPTIONAL
    )
    public static boolean nickedPosition = true;
    @Switch(
            name = "Chat Notification",
            description = "sends chat message when a nicked player joins",
            size = OptionSize.SINGLE,
            category = DENICKER,
            subcategory = DENICKEROPTIONAL
    )
    public static boolean nickedChatNotification = true;
    @Switch(
            name = "Auto Denicker",
            description = "tries to denick players in lobby automatically",
            size = OptionSize.SINGLE,
            category = DENICKER,
            subcategory = DENICKEROPTIONAL
    )
    public static boolean autoDenick = true;

    @Switch(
            name = "Custom Nametag",
            description = "",
            size = OptionSize.SINGLE,
            category = DENICKER,
            subcategory = DENICKEROPTIONAL
    )
    public static boolean customFont = true;

    //Render
    @Switch(
            name = "2D Player ESP",
            size = OptionSize.SINGLE,
            category = DEBUG
    )
    public static boolean twoDESP = false;
    @Switch(
            name = "2d ESP Health Bar",
            size = OptionSize.SINGLE,
            category = DEBUG
    )
    public static boolean healthBar = false;

    @Switch(
            name = "3D Player ESP",
            size = OptionSize.SINGLE,
            category = DEBUG
    )
    public static boolean threeDESP = false;

    //Enemy Players
    @HUD(
            name = "Enemies HUD",
            category = ENEMIES
    )
    public static EnemiesHud enemiesHud = new EnemiesHud();

    @Switch(
            name = "Display Distance",
            size = OptionSize.SINGLE,
            category = ENEMIES,
            subcategory = ENEMIESOPTIONAL
    )
    public static boolean enemyDistance = true;
    @Switch(
            name = "Display Position",
            size = OptionSize.SINGLE,
            category = ENEMIES,
            subcategory = ENEMIESOPTIONAL
    )
    public static boolean enemyPosition = true;
    @Switch(
            name = "Chat Notification",
            description = "sends chat message when an enemy player joins",
            size = OptionSize.SINGLE,
            category = ENEMIES,
            subcategory = ENEMIESOPTIONAL
    )
    public static boolean enemyChatNotification = true;
    @Switch(
            name = "Inventory Packets",
            category = DEBUG
    )
    public static boolean invPackets = false;
    @Switch(
            name = "Only while on the Pit",
            size = OptionSize.SINGLE,
            category = BETTERPIT,
            subcategory = BETTERPITMISC
    )
    public static boolean whileInPit = false;
    public ConfigOneConfig() {
        super(new Mod(BetterPit.NAME, ModType.UTIL_QOL), BetterPit.MODID + ".json");
        initialize();
    }
}