package com.siliqon.cosmiccosmetics.handlers.effects;

import com.siliqon.cosmiccosmetics.CosmeticsPlugin;
import com.siliqon.cosmiccosmetics.custom.ActiveEffectData;
import com.siliqon.cosmiccosmetics.enums.EffectForm;
import com.siliqon.cosmiccosmetics.enums.EffectType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Wolf;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.siliqon.cosmiccosmetics.CosmeticsPlugin.log;
import static com.siliqon.cosmiccosmetics.utils.Effects.getActiveEffect;
import static com.siliqon.cosmiccosmetics.utils.Effects.getEffectsEnabled;
import static com.siliqon.cosmiccosmetics.utils.Effects.getPlayerActiveEffectData;

public class Pets {
    private static final CosmeticsPlugin plugin = CosmeticsPlugin.getInstance();
    private static final ConcurrentHashMap<UUID, PetState> ACTIVE_PETS = new ConcurrentHashMap<>();

    private static final class PetState {
        private Mob pet;
        private int taskId;
        private EffectType displayedType;

        private PetState(Mob pet, EffectType displayedType) {
            this.pet = pet;
            this.displayedType = displayedType;
            this.taskId = -1;
        }
    }

    public static void startForPlayer(Player player) {
        EffectType effectType = getActiveEffect(player, EffectForm.PETS);
        if (effectType == null) {
            return;
        }

        if (!plugin.isEffectFormEnabledInWorld(EffectForm.PETS, player.getWorld().getName())) {
            removeForPlayer(player);
            return;
        }

        ActiveEffectData pdata = getPlayerActiveEffectData(player);
        if (pdata != null && pdata.getTaskIds().containsKey(EffectForm.PETS)) {
            for (int taskId : new ArrayList<>(pdata.getTaskIds().get(EffectForm.PETS))) {
                Bukkit.getScheduler().cancelTask(taskId);
            }
            pdata.getTaskIds().remove(EffectForm.PETS);
        }

        removeForPlayer(player);

        Mob pet = spawnPet(player, effectType);
        if (pet == null) {
            return;
        }

        PetState state = new PetState(pet, effectType);
        ACTIVE_PETS.put(player.getUniqueId(), state);
        syncVisibility(player, state);

        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (!isPlayerRenderable(player)) {
                removeForPlayer(player);
                return;
            }
            if (!plugin.isEffectFormEnabledInWorld(EffectForm.PETS, player.getWorld().getName())) {
                removeForPlayer(player);
                return;
            }

            PetState current = ACTIVE_PETS.get(player.getUniqueId());
            if (current == null || current != state || !current.pet.isValid() || current.pet.isDead()) {
                removeForPlayer(player);
                return;
            }

            EffectType currentType = getActiveEffect(player, EffectForm.PETS);
            if (currentType == null) {
                removeForPlayer(player);
                return;
            }
            if (currentType != current.displayedType) {
                startForPlayer(player);
                return;
            }

            followOwner(player, current.pet);
            syncVisibility(player, current);
        }, 0L, 1L);

        state.taskId = taskId;

        if (pdata != null) {
            pdata.addTaskId(EffectForm.PETS, taskId);
        }

        if (plugin.debugLevel >= 2) {
            log("Registered PETS task for " + player.getName());
        }
    }

    public static void removeForPlayer(Player player) {
        removeForPlayer(player.getUniqueId());
    }

    public static void removeForPlayer(UUID playerUUID) {
        PetState state = ACTIVE_PETS.remove(playerUUID);
        if (state == null) {
            return;
        }

        if (state.taskId > 0) {
            Bukkit.getScheduler().cancelTask(state.taskId);
        }

        if (state.pet != null && state.pet.isValid()) {
            state.pet.remove();
        }

        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            ActiveEffectData pdata = getPlayerActiveEffectData(player);
            if (pdata != null) {
                var taskIds = pdata.getTaskIds().get(EffectForm.PETS);
                if (taskIds != null) {
                    taskIds.removeIf(id -> id == state.taskId);
                    if (taskIds.isEmpty()) {
                        pdata.getTaskIds().remove(EffectForm.PETS);
                    }
                }
            }
        }
    }

    public static void removeAll() {
        for (UUID playerUUID : new ArrayList<>(ACTIVE_PETS.keySet())) {
            removeForPlayer(playerUUID);
        }
    }

    private static Mob spawnPet(Player player, EffectType effectType) {
        Location spawnLocation = calculatePetTargetLocation(player.getLocation());
        Mob pet;

        switch (effectType) {
            case PET_CAT -> pet = player.getWorld().spawn(spawnLocation, Cat.class, cat -> {
                cat.setInvulnerable(true);
                cat.setSilent(true);
                cat.setCollidable(false);
                cat.setCanPickupItems(false);
                cat.setPersistent(false);
            });
            case PET_WOLF -> pet = player.getWorld().spawn(spawnLocation, Wolf.class, wolf -> {
                wolf.setInvulnerable(true);
                wolf.setSilent(true);
                wolf.setCollidable(false);
                wolf.setCanPickupItems(false);
                wolf.setPersistent(false);
                wolf.setTamed(true);
                wolf.setOwner(player);
            });
            case PET_RABBIT -> pet = player.getWorld().spawn(spawnLocation, Rabbit.class, rabbit -> {
                rabbit.setInvulnerable(true);
                rabbit.setSilent(true);
                rabbit.setCollidable(false);
                rabbit.setCanPickupItems(false);
                rabbit.setPersistent(false);
            });
            case PET_FOX -> pet = player.getWorld().spawn(spawnLocation, Fox.class, fox -> {
                fox.setInvulnerable(true);
                fox.setSilent(true);
                fox.setCollidable(false);
                fox.setCanPickupItems(false);
                fox.setPersistent(false);
            });
            case PET_PIG -> pet = player.getWorld().spawn(spawnLocation, Pig.class, pig -> {
                pig.setInvulnerable(true);
                pig.setSilent(true);
                pig.setCollidable(false);
                pig.setCanPickupItems(false);
                pig.setPersistent(false);
            });
            case PET_SHEEP -> pet = player.getWorld().spawn(spawnLocation, Sheep.class, sheep -> {
                sheep.setInvulnerable(true);
                sheep.setSilent(true);
                sheep.setCollidable(false);
                sheep.setCanPickupItems(false);
                sheep.setPersistent(false);
            });
            default -> {
                return null;
            }
        }

        if (pet instanceof Ageable ageable) {
            ageable.setBaby();
        }

        pet.setAware(false);
        pet.setAI(false);

        return pet;
    }

    private static void followOwner(Player owner, Mob pet) {
        Location ownerLocation = owner.getLocation();
        Location targetLocation = calculatePetTargetLocation(ownerLocation);
        Location petLocation = pet.getLocation();

        double distanceSquared = petLocation.distanceSquared(targetLocation);
        if (distanceSquared > 144.0) {
            pet.teleport(targetLocation);
            return;
        }

        if (distanceSquared > 0.04) {
            Vector delta = targetLocation.toVector().subtract(petLocation.toVector());
            double distance = Math.sqrt(distanceSquared);
            double step = Math.min(0.35, distance);
            Vector move = delta.multiply(step / Math.max(distance, 1.0E-6));
            Location next = petLocation.clone().add(move);
            pet.teleport(next);
            petLocation = next;
        }

        pet.setRotation(ownerLocation.getYaw(), petLocation.getPitch());
    }

    private static Location calculatePetTargetLocation(Location ownerLocation) {
        Vector forward = ownerLocation.getDirection().setY(0);
        if (forward.lengthSquared() < 1.0E-6) {
            forward = new Vector(0, 0, 1);
        } else {
            forward.normalize();
        }

        Vector right = new Vector(forward.getZ(), 0, -forward.getX());
        if (right.lengthSquared() < 1.0E-6) {
            right = new Vector(1, 0, 0);
        } else {
            right.normalize();
        }

        return ownerLocation.clone()
                .add(right.multiply(1.0))
                .subtract(forward.multiply(0.25))
                .add(0, 0.05, 0);
    }

    private static boolean isPlayerRenderable(Player player) {
        return player.isOnline() && player.isValid() && !player.isDead();
    }

    private static void syncVisibility(Player owner, PetState state) {
        for (Player viewer : owner.getWorld().getPlayers()) {
            if (viewer.equals(owner) || getEffectsEnabled(viewer)) {
                viewer.showEntity(plugin, state.pet);
            } else {
                viewer.hideEntity(plugin, state.pet);
            }
        }
    }
}
