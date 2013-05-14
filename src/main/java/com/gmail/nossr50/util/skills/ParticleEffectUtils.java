package com.gmail.nossr50.util.skills;

import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.skills.SkillType;

public final class ParticleEffectUtils {

    private ParticleEffectUtils() {};

    public static void playBleedEffect(LivingEntity livingEntity) {
        if (!Config.getInstance().getBleedEffectEnabled()) {
            return;
        }

        livingEntity.getWorld().playEffect(livingEntity.getEyeLocation(), Effect.STEP_SOUND, Material.REDSTONE_WIRE);
    }

    public static void playDodgeEffect(Player player) {
        if (!Config.getInstance().getDodgeEffectEnabled()) {
            return;
        }

        Location location = player.getEyeLocation();
        World world = player.getWorld();

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
        if (!Config.getInstance().getGreaterImpactEffectEnabled()) {
            return;
        }

        Location location = livingEntity.getEyeLocation();

        livingEntity.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), 0F, false, false);
    }

    public static void playAbilityEnabledEffect(Player player) {
        if (!Config.getInstance().getAbilityActivationEffectEnabled()) {
            return;
        }

        if (hasHeadRoom(player)) {
            fireworkParticleShower(player, Color.GREEN);
        }
    }

    public static void playAbilityDisabledEffect(Player player) {
        if (!Config.getInstance().getAbilityDeactivationEffectEnabled()) {
            return;
        }

        if (hasHeadRoom(player)) {
            fireworkParticleShower(player, Color.RED);
        }
    }

    public static void runescapeModeCelebration(Player player, SkillType skill) {
        fireworkParticleShower(player, skill.getRunescapeModeColor());
    }

    private static void fireworkParticleShower(Player player, Color color) {
        Location location = player.getLocation();
        location.setY(location.getY() - 1.0);
        location.setPitch(-90);

        Firework firework = (Firework) player.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(color).with((Config.getInstance().getLargeFireworks() ? Type.BALL_LARGE : Type.BALL)).trail(true).build();
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
