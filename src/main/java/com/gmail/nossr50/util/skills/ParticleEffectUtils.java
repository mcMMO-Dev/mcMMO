package com.gmail.nossr50.util.skills;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class ParticleEffectUtils {

    private ParticleEffectUtils() {}

    public static void playGreenThumbEffect(Location location) {
        World world = location.getWorld();
        playSmokeEffect(location);
        SoundManager.worldSendSoundMaxPitch(world, location, SoundType.POP);
    }

    public static void playBleedEffect(@NotNull LivingEntity livingEntity) {
        if (!mcMMO.p.getGeneralConfig().getBleedEffectEnabled()) {
            return;
        }

        livingEntity.getWorld().playEffect(getParticleLocation(livingEntity), Effect.STEP_SOUND, Material.REDSTONE_WIRE);
    }

    private static @NotNull Location getParticleLocation(@NotNull LivingEntity livingEntity) {
        Location origin = livingEntity.getEyeLocation().clone();
        World world = origin.getWorld();

        double x = origin.getX();
        double y = origin.getY();
        double z = origin.getZ();

        double offSetVal = 0.3D;

        switch(RandomUtils.nextInt(10)) {

            case 0:
                return new Location(world, x - offSetVal, y, z);
            case 1:
                return new Location(world, x + offSetVal, y, z);
            case 2:
                return new Location(world, x, y + offSetVal, z);
            case 3:
                return new Location(world, x, y - offSetVal, z);
            case 4: Location locE = new Location(world, x, y, z + offSetVal);
                return new Location(world, x, y, z - offSetVal);
            case 5:
                return new Location(world, x + offSetVal, y, z + offSetVal);
            case 6:
                return new Location(world, x - offSetVal, y, z - offSetVal);
            case 7:
                return new Location(world, x - offSetVal, y - offSetVal, z - offSetVal);
            case 8:
                return new Location(world, x + offSetVal, y - offSetVal, z + offSetVal);
            case 9:
                return new Location(world, x - offSetVal, y + offSetVal, z - offSetVal);
            default:
                return new Location(world, x + offSetVal, y + offSetVal, z - offSetVal);
        }
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

        if(location.getWorld() == null)
            return;

        location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, 1);
    }

    public static void playSmokeEffect(Location location) {
        World world = location.getWorld();

        if(world == null)
            return;

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

        livingEntity.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), 0F, false, false);
    }

    public static void playCallOfTheWildEffect(LivingEntity livingEntity) {
        if (!mcMMO.p.getGeneralConfig().getCallOfTheWildEffectEnabled()) {
            return;
        }

        livingEntity.getWorld().playEffect(livingEntity.getEyeLocation(), Effect.MOBSPAWNER_FLAMES, 1);
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
