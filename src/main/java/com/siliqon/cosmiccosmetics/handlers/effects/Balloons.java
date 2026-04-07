package com.siliqon.cosmiccosmetics.handlers.effects;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import com.siliqon.cosmiccosmetics.custom.ActiveEffectData;
import com.siliqon.cosmiccosmetics.enums.EffectForm;
import com.siliqon.cosmiccosmetics.enums.EffectType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.siliqon.cosmiccosmetics.CosmeticsPlugin.log;
import static com.siliqon.cosmiccosmetics.utils.Effects.getActiveEffect;
import static com.siliqon.cosmiccosmetics.utils.Effects.getEffectsEnabled;
import static com.siliqon.cosmiccosmetics.utils.Effects.getPlayerActiveEffectData;

public class Balloons {
    private static final CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
    private static final ConcurrentHashMap<UUID, BalloonState> ACTIVE_BALLOONS = new ConcurrentHashMap<>();

    private static final class BalloonState {
        private final Chicken anchor;
        private final ItemDisplay display;
        private int taskId;
        private long tick;
        private EffectType displayedType;
        private Location smoothedAnchor;

        private BalloonState(Chicken anchor, ItemDisplay display, EffectType displayedType, Location smoothedAnchor) {
            this.anchor = anchor;
            this.display = display;
            this.displayedType = displayedType;
            this.smoothedAnchor = smoothedAnchor;
            this.tick = 0L;
            this.taskId = -1;
        }
    }

    public static void startForPlayer(Player player) {
        EffectType effectType = getActiveEffect(player, EffectForm.BALLOONS);
        if (effectType == null) {
            return;
        }

        if (!plugin.isEffectFormEnabledInWorld(EffectForm.BALLOONS, player.getWorld().getName())) {
            removeForPlayer(player);
            return;
        }

        ActiveEffectData pdata = getPlayerActiveEffectData(player);
        if (pdata != null && pdata.getTaskIds().containsKey(EffectForm.BALLOONS)) {
            for (int taskId : new ArrayList<>(pdata.getTaskIds().get(EffectForm.BALLOONS))) {
                Bukkit.getScheduler().cancelTask(taskId);
            }
            pdata.getTaskIds().remove(EffectForm.BALLOONS);
        }

        removeForPlayer(player);

        Location anchorSpawnLocation = calculateAnchorLocation(player, 0L);
        Chicken anchor = player.getWorld().spawn(anchorSpawnLocation, Chicken.class, chicken -> {
            chicken.setInvisible(true);
            chicken.setInvulnerable(true);
            chicken.setSilent(true);
            chicken.setCollidable(false);
            chicken.setGravity(false);
            chicken.setAI(false);
            chicken.setPersistent(false);
        });
        anchor.setLeashHolder(player);

        ItemDisplay display = player.getWorld().spawn(calculateDisplayLocation(anchorSpawnLocation, 0L),
                ItemDisplay.class,
                itemDisplay -> {
                    itemDisplay.setItemStack(getBalloonItem(effectType));
                    itemDisplay.setBillboard(Display.Billboard.FIXED);
                    itemDisplay.setInterpolationDelay(0);
                    itemDisplay.setInterpolationDuration(2);
                    itemDisplay.setTransformation(new Transformation(
                            new Vector3f(0f, 0f, 0f),
                            new Quaternionf(),
                            new Vector3f(0.48f, 0.48f, 0.48f),
                            new Quaternionf()));
                });

        BalloonState state = new BalloonState(anchor, display, effectType, anchorSpawnLocation.clone());
        ACTIVE_BALLOONS.put(player.getUniqueId(), state);

        syncVisibility(player, state);

        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (!isPlayerRenderable(player)) {
                removeForPlayer(player);
                return;
            }
            if (!plugin.isEffectFormEnabledInWorld(EffectForm.BALLOONS, player.getWorld().getName())) {
                removeForPlayer(player);
                return;
            }

            BalloonState current = ACTIVE_BALLOONS.get(player.getUniqueId());
            if (current == null || current != state || !current.anchor.isValid() || !current.display.isValid()) {
                removeForPlayer(player);
                return;
            }

            EffectType currentType = getActiveEffect(player, EffectForm.BALLOONS);
            if (currentType == null) {
                removeForPlayer(player);
                return;
            }

            Location anchorTarget = calculateAnchorLocation(player, current.tick);
            current.smoothedAnchor = smoothTowards(current.smoothedAnchor, anchorTarget);
            current.anchor.teleport(current.smoothedAnchor);

            current.display.teleport(calculateDisplayLocation(current.smoothedAnchor, current.tick));
            if (current.displayedType != currentType) {
                current.display.setItemStack(getBalloonItem(currentType));
                current.displayedType = currentType;
            }

            if ((current.tick & 0x7L) == 0L) {
                if (!current.anchor.isLeashed() || current.anchor.getLeashHolder() != player) {
                    current.anchor.setLeashHolder(player);
                }
                syncVisibility(player, current);
            }

            current.tick++;
        }, 0L, 1L);

        state.taskId = taskId;

        if (pdata != null) {
            pdata.addTaskId(EffectForm.BALLOONS, taskId);
        }
        if (plugin.debugLevel >= 2) {
            log("Registered BALLOONS task for " + player.getName());
        }
    }

    public static void removeForPlayer(Player player) {
        removeForPlayer(player.getUniqueId());
    }

    public static void removeForPlayer(UUID playerUUID) {
        BalloonState state = ACTIVE_BALLOONS.remove(playerUUID);
        if (state == null) {
            return;
        }

        if (state.taskId > 0) {
            Bukkit.getScheduler().cancelTask(state.taskId);
        }

        if (state.anchor != null && state.anchor.isValid()) {
            state.anchor.remove();
        }
        if (state.display != null && state.display.isValid()) {
            state.display.remove();
        }

        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            ActiveEffectData pdata = getPlayerActiveEffectData(player);
            if (pdata != null) {
                var taskIds = pdata.getTaskIds().get(EffectForm.BALLOONS);
                if (taskIds != null) {
                    taskIds.removeIf(id -> id == state.taskId);
                    if (taskIds.isEmpty()) {
                        pdata.getTaskIds().remove(EffectForm.BALLOONS);
                    }
                }
            }
        }
    }

    public static void removeAll() {
        for (UUID playerUUID : new ArrayList<>(ACTIVE_BALLOONS.keySet())) {
            removeForPlayer(playerUUID);
        }
    }

    private static Location calculateAnchorLocation(Player player, long tick) {
        Location playerLocation = player.getLocation();
        Vector horizontalDirection = playerLocation.getDirection().setY(0);
        if (horizontalDirection.lengthSquared() < 1.0E-6) {
            horizontalDirection = new Vector(0, 0, 1);
        } else {
            horizontalDirection.normalize();
        }

        Vector right = new Vector(-horizontalDirection.getZ(), 0, horizontalDirection.getX()).normalize();
        Vector trailingOffset = horizontalDirection.multiply(-0.04);
        Vector sideOffset = right.multiply(0.06);

        return playerLocation.add(0, 1.62, 0).add(trailingOffset).add(sideOffset);
    }

    private static Location calculateDisplayLocation(Location anchorLocation, long tick) {
        double xFloat = 0.04 * Math.sin(tick * 0.18);
        double zFloat = 0.04 * Math.cos(tick * 0.18);
        double yFloat = 0.03 * Math.sin(tick * 0.12);
        return anchorLocation.clone().add(xFloat, 1.18 + yFloat, zFloat);
    }

    private static Location smoothTowards(Location current, Location target) {
        if (current == null || !Objects.equals(current.getWorld(), target.getWorld())) {
            return target.clone();
        }

        double smoothing = 0.35D;
        double x = current.getX() + (target.getX() - current.getX()) * smoothing;
        double y = current.getY() + (target.getY() - current.getY()) * smoothing;
        double z = current.getZ() + (target.getZ() - current.getZ()) * smoothing;
        return new Location(target.getWorld(), x, y, z, target.getYaw(), target.getPitch());
    }

    private static boolean isPlayerRenderable(Player player) {
        return player.isOnline() && player.isValid() && !player.isDead();
    }

    private static void syncVisibility(Player owner, BalloonState state) {
        for (Player viewer : owner.getWorld().getPlayers()) {
            if (viewer.equals(owner) || getEffectsEnabled(viewer)) {
                viewer.showEntity(plugin, state.anchor);
                viewer.showEntity(plugin, state.display);
            } else {
                viewer.hideEntity(plugin, state.anchor);
                viewer.hideEntity(plugin, state.display);
            }
        }
    }

    private static ItemStack getBalloonItem(EffectType effectType) {
        Material material;
        switch (effectType) {
            case BALLOON_BLUE -> material = Material.BLUE_WOOL;
            case BALLOON_GREEN -> material = Material.LIME_WOOL;
            case BALLOON_YELLOW -> material = Material.YELLOW_WOOL;
            case BALLOON_PURPLE -> material = Material.PURPLE_WOOL;
            case BALLOON_ORANGE -> material = Material.ORANGE_WOOL;
            case BALLOON_CYAN -> material = Material.CYAN_WOOL;
            case BALLOON_PINK -> material = Material.PINK_WOOL;
            case BALLOON_RED, BALLOONS -> material = Material.RED_WOOL;
            default -> material = Material.WHITE_WOOL;
        }
        return new ItemStack(material);
    }
}
