package com.siliqon.cosmiccosmetics.handlers.effects;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import com.siliqon.cosmiccosmetics.custom.ActiveEffectData;
import com.siliqon.cosmiccosmetics.enums.EffectForm;
import com.siliqon.cosmiccosmetics.enums.Glow;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import net.kyori.adventure.text.Component;

import static com.siliqon.cosmiccosmetics.CosmeticsPlugin.log;
import static com.siliqon.cosmiccosmetics.utils.Effects.*;

public class Glows {
    private static void applyTeamToPacket(String teamName, Glow glowType, Player player) {
        WrapperPlayServerTeams.ScoreBoardTeamInfo teamInfo = new WrapperPlayServerTeams.ScoreBoardTeamInfo(
                Component.text(teamName),
                Component.empty(),
                Component.empty(),
                WrapperPlayServerTeams.NameTagVisibility.ALWAYS,
                WrapperPlayServerTeams.CollisionRule.ALWAYS,
                glowType.getChatColor(),
                WrapperPlayServerTeams.OptionData.ALL);

        WrapperPlayServerTeams teamPacket = new WrapperPlayServerTeams(
                teamName,
                WrapperPlayServerTeams.TeamMode.CREATE,
                teamInfo,
                java.util.Collections.singletonList(player.getName()));

        for (Player p : Bukkit.getOnlinePlayers()) {
            PacketEvents.getAPI().getPlayerManager().sendPacket(p, teamPacket);
        }
    }

    private static final CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();

    public static void startForPlayer(Player player) {
        Enum<?> activeEffectEnum = getActiveEffect(player, EffectForm.GLOW);
        if (!(activeEffectEnum instanceof Glow)) {
            return;
        }
        Glow glowType = (Glow) activeEffectEnum;

        if (!plugin.isEffectFormEnabledInWorld(EffectForm.GLOW, player.getWorld().getName()))
            return;

        String teamName = "CC_GLOW_" + glowType.name();
        if (teamName.length() > 64) {
            teamName = teamName.substring(0, 64);
        }

        applyTeamToPacket(teamName, glowType, player);

        player.setGlowing(true);

        ActiveEffectData pdata = getPlayerActiveEffectData(player);
        final String finalTeamName = teamName;
        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (player.isDead() || !player.isValid() || !player.isOnline()) {
                return;
            }
            if (!plugin.isEffectFormEnabledInWorld(EffectForm.GLOW, player.getWorld().getName())) {
                player.setGlowing(false);
                return;
            }
            player.setGlowing(true);

            applyTeamToPacket(finalTeamName, glowType, player);

        }, 0L, 20L * 3);

        pdata.addTaskId(EffectForm.GLOW, taskId);

        if (plugin.debugLevel >= 2)
            log("Registered GLOW task for " + player.getName());
    }

    public static void removeForPlayer(Player player) {
        player.setGlowing(false);
        Enum<?> activeEffectEnum = getActiveEffect(player, EffectForm.GLOW);
        if (activeEffectEnum instanceof Glow) {
            String teamName = "CC_GLOW_" + activeEffectEnum.name();
            if (teamName.length() > 64) {
                teamName = teamName.substring(0, 64);
            }

            WrapperPlayServerTeams removePacket = new WrapperPlayServerTeams(
                    teamName,
                    WrapperPlayServerTeams.TeamMode.REMOVE,
                    (WrapperPlayServerTeams.ScoreBoardTeamInfo) null,
                    java.util.Collections.singletonList(player.getName()));

            for (Player p : Bukkit.getOnlinePlayers()) {
                PacketEvents.getAPI().getPlayerManager().sendPacket(p, removePacket);
            }
        }
    }
}
