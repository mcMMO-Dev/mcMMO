package com.gmail.nossr50.util.skills;

import com.gmail.nossr50.config.MainConfig;
import com.gmail.nossr50.mcMMO;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;

public final class ParticleEffectUtils {

    private ParticleEffectUtils() {};

    public static void playBleedEffect(LivingEntity livingEntity) {
        if (!MainConfig.getInstance().getBleedEffectEnabled()) {
            return;
        }

        livingEntity.getWorld().playEffect(livingEntity.getEyeLocation(), Effect.STEP_SOUND, Material.REDSTONE_WIRE);
    }

    public static void playDodgeEffect(Player player) {
        if (!MainConfig.getInstance().getDodgeEffectEnabled()) {
            return;
        }

        playSmokeEffect(player);
    }

    public static void playFluxEffect(Location location) {
        if (!MainConfig.getInstance().getFluxEffectEnabled()) {
            return;
        }

        location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, 1);
    }

    public static void playSmokeEffect(LivingEntity livingEntity) {
        Location location = livingEntity.getEyeLocation();
        World world = livingEntity.getWorld();

        // Have to do it this way, because not all block directions are valid for smoke
        world.playEffect(location, Effect.SMOKE, BlockFace.SOUTH_EAST);
        world.playEffect(location, Effect.SMOKE, BlockFace.SOUTH);
        world.playEffect(location, Effect.SMOKE, BlockFace.SOUTH_WEST);
        world.playEffect(location, Effect.SMOKE, BlockFace.EAST);
        world.playEffect(location, Effect.SMOKE, BlockFace.SELF);
        world.playEffect(location, Effect.SMOKE, BlockFace.WEST);
        world.playEffect(location, Effect.SMOKE, BlockFace.NORTH_EAST);
        world.playEffect(location, Effect.SMOKE, BlockFace.NORTH);
        world.playEffect(location, Effect.SMOKE, BlockFace.NORTH_WEST);
    }

    public static void playGreaterImpactEffect(LivingEntity livingEntity) {
        if (!MainConfig.getInstance().getGreaterImpactEffectEnabled()) {
            return;
        }

        Location location = livingEntity.getEyeLocation();

        livingEntity.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), 0F, false, false);
    }

    public static void playCallOfTheWildEffect(LivingEntity livingEntity) {
        if (!MainConfig.getInstance().getCallOfTheWildEffectEnabled()) {
            return;
        }

        livingEntity.getWorld().playEffect(livingEntity.getEyeLocation(), Effect.MOBSPAWNER_FLAMES, 1);
    }

    public static void playAbilityEnabledEffect(Player player) {
        if (!MainConfig.getInstance().getAbilityActivationEffectEnabled()) {
            return;
        }

        if (hasHeadRoom(player)) {
            fireworkParticleShower(player, Color.GREEN);
        }
    }

    public static void playAbilityDisabledEffect(Player player) {
        if (!MainConfig.getInstance().getAbilityDeactivationEffectEnabled()) {
            return;
        }

        if (hasHeadRoom(player)) {
            fireworkParticleShower(player, Color.RED);
        }
    }

    public static void fireworkParticleShower(Player player, Color color) {
        Location location = player.getLocation();
        location.setY(location.getY() + (player.isInsideVehicle() ? 1.0 : -1.0));
        location.setPitch(-90);

        Firework firework = (Firework) player.getWorld().spawnEntity(location, EntityType.FIREWORK);
        firework.setMetadata(mcMMO.funfettiMetadataKey, new FixedMetadataValue(mcMMO.p, null));
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(color).with((MainConfig.getInstance().getLargeFireworks() ? Type.BALL_LARGE : Type.BALL)).trail(true).build();
        fireworkMeta.addEffect(effect);
        fireworkMeta.addEffect(effect);
        fireworkMeta.setPower(0);
        firework.setFireworkMeta(fireworkMeta);
    }

    private static boolean hasHeadRoom(Player player) {
        boolean hasHeadRoom = true;
        Block headBlock = player.getEyeLocation().getBlock();

        for (int i = 0; i < 3; i++) {
            if (!headBlock.getRelative(BlockFace.UP, i).isEmpty()) {
                hasHeadRoom = false;
                break;
            }
        }

        return hasHeadRoom;
    }
}
