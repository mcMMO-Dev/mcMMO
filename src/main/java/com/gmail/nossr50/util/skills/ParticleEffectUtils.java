package com.gmail.nossr50.util.skills;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import org.apache.commons.lang3.RandomUtils;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class ParticleEffectUtils {

    private ParticleEffectUtils() {
    }

    public static void playGreenThumbEffect(Location location) {
        World world = location.getWorld();
        playSmokeEffect(location);
        SoundManager.worldSendSoundMaxPitch(world, location, SoundType.POP);
    }

    public static void playBleedEffect(@NotNull LivingEntity livingEntity) {
        if (!mcMMO.p.getGeneralConfig().getBleedEffectEnabled()) {
            return;
        }

        livingEntity.getWorld().playEffect(getParticleLocation(livingEntity),
                Effect.STEP_SOUND, Material.REDSTONE_WIRE);
    }

    public static void playCrippleEffect(@NotNull LivingEntity livingEntity) {
        if (!mcMMO.p.getGeneralConfig().getCrippleEffectEnabled()) {
            return;
        }

        SoundManager.sendCategorizedSound(livingEntity.getLocation(), SoundType.CRIPPLE,
                SoundCategory.PLAYERS, 0.2F);
        livingEntity.getWorld()
                .playEffect(getParticleLocation(livingEntity), Effect.ANVIL_BREAK, null, 20);

        if (livingEntity instanceof Player player) {
            final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);
            boolean useChatNotification = mmoPlayer == null || mmoPlayer.useChatNotifications();
            if (useChatNotification) {
                NotificationManager.sendPlayerInformation(
                        player, NotificationType.SUBSKILL_MESSAGE, "Maces.SubSkill.Cripple.Proc");
            }
        }
    }

    private static @NotNull Location getParticleLocation(@NotNull LivingEntity livingEntity) {
        Location origin = livingEntity.getEyeLocation().clone();
        World world = origin.getWorld();

        double x = origin.getX();
        double y = origin.getY();
        double z = origin.getZ();

        double offSetVal = 0.3D;

        return switch (RandomUtils.nextInt(0, 10)) {
            case 0 -> new Location(world, x - offSetVal, y, z);
            case 1 -> new Location(world, x + offSetVal, y, z);
            case 2 -> new Location(world, x, y + offSetVal, z);
            case 3 -> new Location(world, x, y - offSetVal, z);
            case 4 -> {
                Location locE = new Location(world, x, y, z + offSetVal);
                yield new Location(world, x, y, z - offSetVal);
            }
            case 5 -> new Location(world, x + offSetVal, y, z + offSetVal);
            case 6 -> new Location(world, x - offSetVal, y, z - offSetVal);
            case 7 -> new Location(world, x - offSetVal, y - offSetVal, z - offSetVal);
            case 8 -> new Location(world, x + offSetVal, y - offSetVal, z + offSetVal);
            case 9 -> new Location(world, x - offSetVal, y + offSetVal, z - offSetVal);
            default -> new Location(world, x + offSetVal, y + offSetVal, z - offSetVal);
        };
    }

    public static void playDodgeEffect(Player player) {
        if (!mcMMO.p.getGeneralConfig().getDodgeEffectEnabled()) {
            return;
        }

        playSmokeEffect(player.getLocation());
    }

    public static void playFluxEffect(Location location) {
        if (!mcMMO.p.getGeneralConfig().getFluxEffectEnabled()) {
            return;
        }

        if (location.getWorld() == null) {
            return;
        }

        location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, 1);
    }

    public static void playSmokeEffect(Location location) {
        World world = location.getWorld();

        if (world == null) {
            return;
        }

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
        if (!mcMMO.p.getGeneralConfig().getGreaterImpactEffectEnabled()) {
            return;
        }

        Location location = livingEntity.getEyeLocation();

        livingEntity.getWorld()
                .createExplosion(location.getX(), location.getY(), location.getZ(), 0F, false,
                        false);
    }

    public static void playCallOfTheWildEffect(LivingEntity livingEntity) {
        if (!mcMMO.p.getGeneralConfig().getCallOfTheWildEffectEnabled()) {
            return;
        }

        livingEntity.getWorld()
                .playEffect(livingEntity.getEyeLocation(), Effect.MOBSPAWNER_FLAMES, 1);
    }

    public static void playAbilityDisabledEffect(Player player) {
        if (!mcMMO.p.getGeneralConfig().getAbilityDeactivationEffectEnabled()) {
        }

        /*if (hasHeadRoom(player)) {
            fireworkParticleShower(player, Color.RED);
        }*/
    }

/*    public static void fireworkParticleShower(Player player, Color color) {
        Location location = player.getLocation();
        location.setY(location.getY() + (player.isInsideVehicle() ? 1.0 : -1.0));
        location.setPitch(-90);

        Firework firework = (Firework) player.getWorld().spawnEntity(location, EntityType.FIREWORK);
        firework.setMetadata(mcMMO.funfettiMetadataKey, new FixedMetadataValue(mcMMO.p, null));
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(color).with((Config.getInstance().getLargeFireworks() ? Type.BALL_LARGE : Type.BALL)).trail(true).build();
        fireworkMeta.addEffect(effect);
        fireworkMeta.addEffect(effect);
        fireworkMeta.setPower(0);
        firework.setFireworkMeta(fireworkMeta);
    }*/

    /**
     * Gold + crimson dust sphere around the player on skill level-up (bookedMC theme).
     */
    public static void playGoldCrimsonLevelUpEffect(@NotNull Player player) {
        Location center = player.getLocation().add(0, 1, 0);
        World world = center.getWorld();
        if (world == null) {
            return;
        }
        Particle.DustOptions goldDust = new Particle.DustOptions(Color.fromRGB(255, 170, 0), 1.5F);
        Particle.DustOptions crimsonDust = new Particle.DustOptions(Color.fromRGB(180, 0, 0), 1.5F);
        for (int i = 0; i < 40; i++) {
            double angle = (2 * Math.PI / 40) * i;
            double radius = 1.0;
            Location loc = center.clone().add(
                    Math.cos(angle) * radius,
                    (i % 4) * 0.25 - 0.5,
                    Math.sin(angle) * radius);
            Particle.DustOptions dust = (i % 2 == 0) ? goldDust : crimsonDust;
            world.spawnParticle(Particle.DUST, loc, 1, dust);
        }
    }

    /**
     * Crimson dust burst on super-ability activation (bookedMC theme).
     */
    public static void playGoldCrimsonAbilityEffect(@NotNull Player player) {
        Location center = player.getLocation().add(0, 1, 0);
        World world = center.getWorld();
        if (world == null) {
            return;
        }
        Particle.DustOptions crimsonDust = new Particle.DustOptions(Color.fromRGB(180, 0, 0), 1.2F);
        for (int i = 0; i < 20; i++) {
            double angle = (2 * Math.PI / 20) * i;
            Location loc = center.clone().add(
                    Math.cos(angle) * 0.7,
                    0.0,
                    Math.sin(angle) * 0.7);
            world.spawnParticle(Particle.DUST, loc, 1, crimsonDust);
        }
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
