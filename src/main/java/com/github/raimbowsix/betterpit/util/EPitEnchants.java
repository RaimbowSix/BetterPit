package com.github.raimbowsix.betterpit.util;

public enum EPitEnchants {
    //pants
    MIND_ASSAULT("mind_assault"),//darks
    SOMBER("somber"),
    VENOM("venom"),
    SANGUISUGE("sanguisuge"),
    GRIM_REAPER("grim_reaper"),
    MISERY("misery"),
    SPITE("spite"),
    NOSTALGIA("nostalgia"),
    GOLDEN_HANDCUFFS("golden_handcuffs"),
    HEDGE_FUND("hedge_fund"),
    HEARTRIPPER("heartripper"),
    NEEDLESS_SUFFERING("needless_suffering"),
    LYCANTHROPY("lycanthropy"),//darks
    SELFCHECK("max_bounty_self_claim"),//other pants
    SYBIL("sybil"),
    SWEATY("streak_xp"),
    XPBUMP("xp_per_kill"),
    NOTGLAD("less_damage_nearby_players"),
    GBUMP("gold_per_kill"),
    MOC("gold_strictly_kills"),
    XPBOOST("xp_boost"),
    GBOOST("gold_boost"),
    LODBROK("increase_armor_drops"),
    MIRROR("immune_true_damage"),
    RGM("rgm"),
    REG("regularity"),
    FRENCH("do_it_like_the_french"),
    ABS("respawn_with_absorption"),
    NEWDEAL("new_deal"),
    BOO("passive_health_regen"),
    REVITALIZE("regen_speed_when_low"),
    PHOENIX("phoenix"),
    LASTSTAND("resistance_when_low"),
    WORM("worm"),
    WOLFPACK("wolf_pack"),
    CREATIVE("wood_blocks"),
    POND("water_bucket"),
    TROPHY("trophy"),
    TRASHPANDA("trash_panda"),
    INSTABOOM("instaboom_tnt"),
    GHEARTS("absorption_on_kill"),
    DOUBLEJUMP("double_jump"),
    BTS("melee_damage_vs_bows"),
    PURPLE_GOLD("gold_break_obsidian"),
    PEBBLE("increase_gold_pickup"),
    RING_ARMOR("less_damage_from_arrows"),
    CRICKET("less_damage_on_grass"),
    MCSWIMMER("less_damage_when_swimming"),
    BILLY("less_damage_when_high_bounty"),
    DAG("less_damage_vs_bounties"),
    DA("less_damage_vs_diamond_weapons"),
    ROD_TRUE_DAMAGE("rod_true_damage"),
    FISHING_ROD_KB("fishing_rod_kb"),
    FISHING_ROD_ENCHANT("fishing_rod_enchant"),
    STEREO("stereo"),
    SNOWBALLS("snowballs"),
    LUCK_OF_THE_POND("luck_of_the_pond"),
    POD("escape_pod"),
    ASSASSIN("sneak_teleport"),
    CF("power_against_crits"),
    RESPAWN_RES("respawn_with_resistance"),
    GOMRAWS_HEARTS("regen_when_ooc"),
    PERO("regen_when_hit"),
    GTGF("perma_speed"),
    PAPA("paparazzi"),
    HTH("counter_bounty_hunter"),
    HIDDEN_JEWEL("hidden_jewel"),
    THINK_OF_PEOPLE("think_of_the_people"),
    REALLY_TOXIC("really_toxic"),
    FRAC("fractional_reserve"),
    MBA("pit_mba"),
    ACE_OF_SPADES("ace_of_spades"),
    TOUGH_CREW("tough_crew"),
    SING("singularity"),
    PRICK("thorns"),
    MARTYRDOM("martyrdom"),
    BREACHING_CHARGE("breaching_charge"),
    SNOWMEN("snowmen"),//other pants
    //sword
    EXE("melee_execute"),//swords
    BILL("melee_literally_p2w"),
    LS("melee_heal_on_hit"),
    BREAPER("melee_damage_vs_bounties"),
    CHEAL("melee_combo_heal"),
    CSPEED("melee_combo_speed"),
    CDAMAGE("melee_combo_damage"),
    MELEE_CRIT_MIDAIR("melee_crit_midair"),
    GUTS("melee_heal_on_kill"),
    HEALER("melee_healer"),
    STUN("melee_stun"),
    PUNCH("melee_launch"),
    SPEEDYHIT("melee_speed_on_hit"),
    GAB("melee_damage_when_absorption"),
    PF("melee_damage_when_low"),
    SHARK("melee_damage_when_close_low_players"),
    GRASSHOPPER("melee_damage_when_on_grass"),
    KINGBUSTER("melee_damage_vs_high_hp"),
    PUNISHER("melee_damage_vs_low_hp"),
    DS("melee_damage_vs_diamond"),
    FR("melee_damage_vs_leather"),
    DUELIST("melee_strike_after_block"),
    GAMBLE("melee_gamble"),
    KB("melee_knockback"),
    PERUN("melee_lightning"),
    CRUSH("melee_weakness"),
    MELEE_CRIT("melee_crit_midair"),
    CRIT_RICH("gold_per_crit"),
    PITPOCKET("gold_per_hit"),
    SIERRA("gold_per_diamond_piece"),
    BRUISER("increased_blocking"),
    BT("blocking_cancels_projectiles"),
    HIDDEN_JEWEL_SWORD("melee_hidden_jewel"),
    CXP("combo_xp"),//swords
    //bow
    VOLLEY("volley"),
    DRAIN("bow_slow"),
    FTTS("bow_combo_speed"),
    SAP("bow_spammer"),
    CHIPPING("bow_to_true_damage"),
    WASP("bow_weakness_on_hit"),
    PULLBOW("pullbow"),
    TELEBOW("telebow"),
    DEVIL_CHICKS("explosive_chickens"),
    EXPLOSIVE("explosive_bow"),
    TRUE_SHOT("arrow_true_damage"),
    DAMAGE_PER_ARROW("damage_per_arrow"),
    PARA("parasite"),
    LUCKY_SHOT("lucky_shot"),
    WDKY("heal_on_shoot_self"),
    BTQ("gain_arrows_on_hit"),
    SNIPER("sniper"),
    FS("first_shot"),
    ROGUE("rogue"),
    //misc
    GUARDIAN("guardian"),
    EVIL_WITHIN("evil_within"),
    ROYALTY("royalty");

    private final String name;

    EPitEnchants(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public static EPitEnchants fromString(String input) {
        for (EPitEnchants enchant : values()) {
            if (input.toLowerCase().contains(enchant.getName())) {
                return enchant;
            }
        }
        return null;
    }
}
