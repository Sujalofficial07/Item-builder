package com.sujal.skyblockmaker.registry;

import com.sujal.skyblockmaker.api.SkyblockEconomyApi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SkyblockScoreboardHandler {
    
    private static final String OBJECTIVE_NAME = "SB_Sidebar";

    public static void updateScoreboard(ServerPlayerEntity player) {
        Scoreboard scoreboard = player.getScoreboard();
        ScoreboardObjective objective = scoreboard.getObjective(OBJECTIVE_NAME);

        // 1. Create Objective if missing
        if (objective == null) {
            objective = scoreboard.addObjective(OBJECTIVE_NAME, ScoreboardCriterion.DUMMY, 
                Text.literal("SKYBLOCK").formatted(Formatting.GOLD, Formatting.BOLD), 
                ScoreboardCriterion.RenderType.INTEGER);
        }

        // 2. Set to Sidebar
        scoreboard.setObjectiveSlot(1, objective); // 1 = Sidebar Slot

        // 3. Reset Old Scores (Taaki flickering text na ho, hum clear karke rewrite karte hain)
        // Note: Real optimization ke liye hum Teams use karte hain, par ye simple method bhi work karega.
        for (String entry : scoreboard.getKnownPlayers()) {
            if (scoreboard.getPlayerScore(entry, objective) != null) {
                scoreboard.resetPlayerScore(entry, objective);
            }
        }

        // 4. Data Preparation
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yy"));
        String serverId = "M74"; // Fake Server ID
        long time = player.getWorld().getTimeOfDay();
        String dateLine = "§7" + date + " " + "§8" + serverId;
        
        // Time/Season Logic (Simple)
        String season = getSeason(time);
        String timeStr = getTimeString(time);
        
        // Location
        String location = "§7⏣ §aVillage";

        // Purse
        double coins = SkyblockEconomyApi.getCoins(player);
        String purseLine = "§fPurse: §6" + SkyblockEconomyApi.formatCoins(coins);

        // Objective
        String objTitle = "§eObjective";
        String objTask = "§fGrind Skills!";

        // Website
        String footer = "§ewww.hypixel.net";

        // 5. Draw Lines (Score determines order, 15 is top)
        setLine(scoreboard, objective, dateLine, 12);
        setLine(scoreboard, objective, "§1", 11); // Empty Line
        setLine(scoreboard, objective, "§f" + season, 10);
        setLine(scoreboard, objective, "§7" + timeStr, 9);
        setLine(scoreboard, objective, location, 8);
        setLine(scoreboard, objective, "§2", 7); // Empty Line
        setLine(scoreboard, objective, purseLine, 6);
        setLine(scoreboard, objective, "§3", 5); // Empty Line
        setLine(scoreboard, objective, objTitle, 4);
        setLine(scoreboard, objective, objTask, 3);
        setLine(scoreboard, objective, "§4", 2); // Empty Line
        setLine(scoreboard, objective, footer, 1);
    }

    private static void setLine(Scoreboard sb, ScoreboardObjective obj, String text, int score) {
        sb.getPlayerScore(text, obj).setScore(score);
    }

    // Helpers
    private static String getSeason(long time) {
        // Hypixel seasons change every 31 hours roughly.
        // For static look:
        return "Early Summer 10th"; 
    }

    private static String getTimeString(long time) {
        long hours = (time / 1000 + 6) % 24;
        long minutes = (time % 1000) * 60 / 1000;
        String ampm = hours >= 12 ? "pm" : "am";
        if (hours > 12) hours -= 12;
        if (hours == 0) hours = 12;
        return String.format("%d:%02d%s", hours, minutes, ampm);
    }
}
