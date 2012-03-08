package com.gmail.nossr50;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.inventory.ItemStack;
import com.gmail.nossr50.config.*;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.events.FakeBlockBreakEvent;
import com.gmail.nossr50.events.McMMOItemSpawnEvent;
import com.gmail.nossr50.skills.Repair;

public class m {
    public static final Logger log = Logger.getLogger("Minecraft");

    /**
     * Gets a capitalized version of the target string.
     *
     * @param target String to capitalize
     * @return the capitalized string
     */
    public static String getCapitalized(String target) {
        String firstLetter = target.substring(0,1);
        String remainder = target.substring(1);
        String capitalized = firstLetter.toUpperCase() + remainder.toLowerCase();

        return capitalized;
    }

    /**
     * Gets the int represented by this string.
     *
     * @param string The string to parse
     * @return the int represented by this string
     */
    public static int getInt(String string) {
        if (isInt(string)) {
            return Integer.parseInt(string);
        }
        else {
            return 0;
        }
    }

    /**
     * Checks to see if an entity is currently invincible.
     *
     * @param le The LivingEntity to check
     * @param event The event the entity is involved in
     * @return true if the entity is invincible, false otherwise
     */
    public static boolean isInvincible(LivingEntity le, EntityDamageEvent event) {

        /*
         * So apparently if you do more damage to a LivingEntity than its last damage int you bypass the invincibility.
         * So yeah, this is for that.
         */
        if (le.getNoDamageTicks() > le.getMaximumNoDamageTicks() / 2.0F && event.getDamage() <= le.getLastDamage()) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Gets the power level of a player.
     *
     * @param player The player to get the power level of
     * @param PP The profile of the player
     * @return the power level of the player
     */
    public static int getPowerLevel(Player player, PlayerProfile PP) {
        int powerLevel = 0;

        for (SkillType type : SkillType.values()) {
            if (type.getPermissions(player)) {
                powerLevel += PP.getSkillLevel(type);
            }
        }

        return powerLevel;
    }

    /**
     * Simulate a block break event.
     *
     * @param block The block to break
     * @param player The player breaking the block
     * @param shouldArmSwing true if an armswing event should be fired, false otherwise
     * @return true if the event wasn't cancelled, false otherwise
     */
    public static boolean blockBreakSimulate(Block block, Player player, Boolean shouldArmSwing) {

        //Support for NoCheat
        if (shouldArmSwing) {
            PlayerAnimationEvent armswing = new PlayerAnimationEvent(player);
            Bukkit.getPluginManager().callEvent(armswing);
        }

        FakeBlockBreakEvent event = new FakeBlockBreakEvent(block, player);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Get the upgrade tier of the item in hand.
     *
     * @param inHand The item to check the tier of
     * @return the tier of the item
     */
    public static Integer getTier(ItemStack inHand) {
        int tier = 0;

        if (Repair.isWoodTools(inHand)) {
            tier = 1;
        }
        else if (Repair.isStoneTools(inHand)) {
            tier = 2;
        }
        else if (Repair.isIronTools(inHand)) {
            tier = 3;
        }
        else if(Repair.isGoldTools(inHand)) {
            tier = 1;
        }
        else if(Repair.isDiamondTools(inHand))
            tier = 4;

        return tier;
    }

    /**
     * Determine if two locations are near each other.
     *
     * @param first The first location
     * @param second The second location
     * @param maxDistance The max distance apart
     * @return true if the distance between <code>first</code> and <code>second</code> is less than <code>maxDistance</code>, false otherwise
     */
    public static boolean isNear(Location first, Location second, int maxDistance) {
        double relX = first.getX() - second.getX();
        double relY = first.getY() - second.getY();
        double relZ = first.getZ() - second.getZ();
        double dist = (relX * relX) + (relY * relY) + (relZ * relZ);

        if (dist < maxDistance * maxDistance) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Determine if a string represents an Integer
     *
     * @param string String to check
     * @return true if the string is an Integer, false otherwise
     */
    public static boolean isInt(String string) {
        try {
            Integer.parseInt(string);
            return true;
        }
        catch (NumberFormatException nFE) {
            return false;
        }
    }

    /**
     * Drop items at a given location.
     *
     * @param location The location to drop the items at
     * @param is The items to drop
     * @param quantity The amount of items to drop
     */
    public static void mcDropItems(Location location, ItemStack is, int quantity) {
        for (int i = 0; i < quantity; i++) {
            mcDropItem(location, is);
        }
    }

    /**
     * Randomly drop an item at a given location.
     *
     * @param location The location to drop the items at
     * @param is The item to drop
     * @param chance The percentage chance for the item to drop
     */
    public static void mcRandomDropItem(Location location, ItemStack is, double chance) {
        if (Math.random() * 100 < chance) {
            mcDropItem(location, is);
        }
    }

    /**
     * Randomly drop items at a given location.
     *
     * @param location The location to drop the items at
     * @param is The item to drop
     * @param chance The percentage chance for the item to drop
     * @param quantity The amount of items to drop
     */
    public static void mcRandomDropItems(Location location, ItemStack is, int chance, int quantity) {
        for(int i = 0; i < quantity; i++) {
            mcRandomDropItem(location, is, chance);
        }
    }

    /**
     * Drop an item at a given location.
     *
     * @param location The location to drop the item at
     * @param itemStack The item to drop
     */
    public static void mcDropItem(Location location, ItemStack itemStack) {

        // We can't get the item until we spawn it and we want to make it cancellable, so we have a custom event.
        McMMOItemSpawnEvent event = new McMMOItemSpawnEvent(location, itemStack);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }
        else {
            location.getWorld().dropItemNaturally(location, itemStack);
        }
    }

    /**
     * Convert FlatFile data to MySQL data.
     */
    public static void convertToMySQL() {
        if (!LoadProperties.useMySQL) {
            return;
        }

        Bukkit.getScheduler().scheduleAsyncDelayedTask(Bukkit.getPluginManager().getPlugin("mcMMO"), new Runnable() {
            public void run() {
                String location = "plugins/mcMMO/FlatFileStuff/mcmmo.users";

                try {

                    //Open the user file
                    FileReader file = new FileReader(location);
                    BufferedReader in = new BufferedReader(file);
                    String line = "";
                    String playerName = null;
                    String party = null;
                    String mining = null;
                    String woodcutting = null;
                    String repair = null;
                    String unarmed = null;
                    String herbalism = null;
                    String excavation = null;
                    String archery = null;
                    String swords = null;
                    String axes = null;
                    String acrobatics = null;
                    String taming = null;
                    String fishing = null;
                    String miningXP = null;
                    String woodCuttingXP = null;
                    String repairXP = null;
                    String unarmedXP = null;
                    String herbalismXP = null;
                    String excavationXP = null;
                    String archeryXP = null;
                    String swordsXP = null;
                    String axesXP = null;
                    String acrobaticsXP = null;
                    String tamingXP = null;
                    String fishingXP = null;
                    int id = 0;
                    int theCount = 0;

                    while ((line = in.readLine()) != null) {

                        //Find if the line contains the player we want.
                        String[] character = line.split(":");
                        playerName = character[0];

                        //Check for things we don't want put in the DB
                        if (playerName == null || playerName.equals("null") || playerName.equals("#Storage place for user information")) {
                            continue;
                        }

                        if (character.length > 1) {
                            mining = character[1];
                        }

                        if (character.length > 3) {
                            party = character[3];
                        }

                        if (character.length > 4) {
                            miningXP = character[4];
                        }

                        if (character.length > 5) {
                            woodcutting = character[5];
                        }

                        if (character.length > 6) {
                            woodCuttingXP = character[6];
                        }

                        if (character.length > 7) {
                            repair = character[7];
                        }

                        if (character.length > 8) {
                            unarmed = character[8];
                        }

                        if (character.length > 9) {
                            herbalism = character[9];
                        }

                        if (character.length > 10) {
                            excavation = character[10];
                        }

                        if (character.length > 11) {
                            archery = character[11];
                        }

                        if (character.length > 12) {
                            swords = character[12];
                        }

                        if (character.length > 13) {
                            axes = character[13];
                        }

                        if (character.length > 14) {
                            acrobatics = character[14];
                        }

                        if (character.length > 15) {
                            repairXP = character[15];
                        }

                        if (character.length > 16) {
                            unarmedXP = character[16];
                        }

                        if (character.length > 17) {
                            herbalismXP = character[17];
                        }

                        if (character.length > 18) {
                            excavationXP = character[18];
                        }

                        if (character.length > 19) {
                            archeryXP = character[19];
                        }

                        if (character.length > 20) {
                            swordsXP = character[20];
                        }

                        if (character.length > 21) {
                            axesXP = character[21];
                        }

                        if (character.length > 22) {
                            acrobaticsXP = character[22];
                        }

                        if (character.length > 24) {
                            taming = character[24];
                        }

                        if (character.length > 25) {
                            tamingXP = character[25];
                        }

                        if (character.length > 34) {
                            fishing = character[34];
                        }

                        if (character.length > 35) {
                            fishingXP = character[35];
                        }

                        //Check to see if the user is in the DB
                        id = mcMMO.database.getInt("SELECT id FROM "
                                + LoadProperties.MySQLtablePrefix
                                + "users WHERE user = '" + playerName + "'");

                        if (id > 0) {
                            theCount++;

                            //Update the skill values
                            mcMMO.database.write("UPDATE "
                                    + LoadProperties.MySQLtablePrefix
                                    + "users SET lastlogin = " + 0
                                    + " WHERE id = " + id);
                            mcMMO.database.write("UPDATE "
                                    + LoadProperties.MySQLtablePrefix
                                    + "skills SET " + "  taming = taming+"
                                    + getInt(taming) + ", mining = mining+"
                                    + getInt(mining) + ", repair = repair+"
                                    + getInt(repair)
                                    + ", woodcutting = woodcutting+"
                                    + getInt(woodcutting)
                                    + ", unarmed = unarmed+" + getInt(unarmed)
                                    + ", herbalism = herbalism+"
                                    + getInt(herbalism)
                                    + ", excavation = excavation+"
                                    + getInt(excavation)
                                    + ", archery = archery+" + getInt(archery)
                                    + ", swords = swords+" + getInt(swords)
                                    + ", axes = axes+" + getInt(axes)
                                    + ", acrobatics = acrobatics+"
                                    + getInt(acrobatics)
                                    + ", fishing = fishing+" + getInt(fishing)
                                    + " WHERE user_id = " + id);
                            mcMMO.database.write("UPDATE "
                                    + LoadProperties.MySQLtablePrefix
                                    + "experience SET " + "  taming = "
                                    + getInt(tamingXP) + ", mining = "
                                    + getInt(miningXP) + ", repair = "
                                    + getInt(repairXP) + ", woodcutting = "
                                    + getInt(woodCuttingXP) + ", unarmed = "
                                    + getInt(unarmedXP) + ", herbalism = "
                                    + getInt(herbalismXP) + ", excavation = "
                                    + getInt(excavationXP) + ", archery = "
                                    + getInt(archeryXP) + ", swords = "
                                    + getInt(swordsXP) + ", axes = "
                                    + getInt(axesXP) + ", acrobatics = "
                                    + getInt(acrobaticsXP) + ", fishing = "
                                    + getInt(fishingXP) + " WHERE user_id = "
                                    + id);
                        }
                        else {
                            theCount++;

                            //Create the user in the DB
                            mcMMO.database.write("INSERT INTO "
                                    + LoadProperties.MySQLtablePrefix
                                    + "users (user, lastlogin) VALUES ('"
                                    + playerName + "',"
                                    + System.currentTimeMillis() / 1000 + ")");
                            id = mcMMO.database
                                    .getInt("SELECT id FROM "
                                            + LoadProperties.MySQLtablePrefix
                                            + "users WHERE user = '"
                                            + playerName + "'");
                            mcMMO.database.write("INSERT INTO "
                                    + LoadProperties.MySQLtablePrefix
                                    + "skills (user_id) VALUES (" + id + ")");
                            mcMMO.database.write("INSERT INTO "
                                    + LoadProperties.MySQLtablePrefix
                                    + "experience (user_id) VALUES (" + id
                                    + ")");
                            //Update the skill values
                            mcMMO.database.write("UPDATE "
                                    + LoadProperties.MySQLtablePrefix
                                    + "users SET lastlogin = " + 0
                                    + " WHERE id = " + id);
                            mcMMO.database.write("UPDATE "
                                    + LoadProperties.MySQLtablePrefix
                                    + "users SET party = '" + party
                                    + "' WHERE id = " + id);
                            mcMMO.database.write("UPDATE "
                                    + LoadProperties.MySQLtablePrefix
                                    + "skills SET " + "  taming = "
                                    + getInt(taming) + ", mining = "
                                    + getInt(mining) + ", repair = "
                                    + getInt(repair) + ", woodcutting = "
                                    + getInt(woodcutting) + ", unarmed = "
                                    + getInt(unarmed) + ", herbalism = "
                                    + getInt(herbalism) + ", excavation = "
                                    + getInt(excavation) + ", archery = "
                                    + getInt(archery) + ", swords = "
                                    + getInt(swords) + ", axes = "
                                    + getInt(axes) + ", acrobatics = "
                                    + getInt(acrobatics) + ", fishing = "
                                    + getInt(fishing) + " WHERE user_id = "
                                    + id);
                            mcMMO.database.write("UPDATE "
                                    + LoadProperties.MySQLtablePrefix
                                    + "experience SET " + "  taming = "
                                    + getInt(tamingXP) + ", mining = "
                                    + getInt(miningXP) + ", repair = "
                                    + getInt(repairXP) + ", woodcutting = "
                                    + getInt(woodCuttingXP) + ", unarmed = "
                                    + getInt(unarmedXP) + ", herbalism = "
                                    + getInt(herbalismXP) + ", excavation = "
                                    + getInt(excavationXP) + ", archery = "
                                    + getInt(archeryXP) + ", swords = "
                                    + getInt(swordsXP) + ", axes = "
                                    + getInt(axesXP) + ", acrobatics = "
                                    + getInt(acrobaticsXP) + ", fishing = "
                                    + getInt(fishingXP) + " WHERE user_id = "
                                    + id);
                        }
                    }

                    System.out.println("[mcMMO] MySQL Updated from users file, " + theCount + " items added/updated to MySQL DB");
                    in.close();
                }
                catch (Exception e) {
                    log.log(Level.SEVERE, "Exception while reading " + location + " (Are you sure you formatted it correctly?)", e);
                }
            }
        }, 1);
    }
}
