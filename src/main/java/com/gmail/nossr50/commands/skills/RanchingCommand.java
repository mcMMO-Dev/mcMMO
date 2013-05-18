package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.ranching.Ranching;
import com.gmail.nossr50.util.Permissions;

public class RanchingCommand extends SkillCommand {
    private String multipleBirthChance;
    private String multipleBirthChanceLucky;
    private int masterHerderTime;
    private String shearsMasteryChance;
    private String shearsMasteryChanceLucky;
    private String artisanButcherChance;
    private String artisanButcherChanceLucky;
    private int carnivoresDietRank;

    private boolean canMultipleBirth;
    private boolean canMasterHerder;
    private boolean canShearsMastery;
    private boolean canArtisanButcher;
    private boolean canCarnivoresDiet;

    public RanchingCommand() {
        super(SkillType.RANCHING);
    }

    @Override
    protected void dataCalculations() {
        // MULTIPLE BIRTH
        if (canMultipleBirth) {
            String[] multipleBirthStrings = calculateAbilityDisplayValues(Ranching.multipleBirthIncreaseLevel, Ranching.multipleBirthMaxChance);
            multipleBirthChance = multipleBirthStrings[0];
            multipleBirthChanceLucky = multipleBirthStrings[1];
        }

        // MASTER HERDER
        if (canMasterHerder) {
            int masterHerderTime = (int) (300 - (skillValue / Ranching.masterHerderIncreaseLevel) * 10);

            if (masterHerderTime < Ranching.masterHerderMinimumSeconds) {
                masterHerderTime = Ranching.masterHerderMinimumSeconds;
            }
        }

        // SHEARS MASTERY
        if (canShearsMastery) {
            String[] shearsMasteryStrings = calculateAbilityDisplayValues(Ranching.shearsMasteryMaxLevel, Ranching.shearsMasteryMaxChance);
            shearsMasteryChance = shearsMasteryStrings[0];
            shearsMasteryChanceLucky = shearsMasteryStrings[1];
        }

        // ARTISAN BUTCHER
        if (canArtisanButcher) {
            String[] artisanButcherStrings = calculateAbilityDisplayValues(Ranching.artisanButcherMaxLevel, Ranching.artisanButcherMaxChance);
            artisanButcherChance = artisanButcherStrings[0];
            artisanButcherChanceLucky = artisanButcherStrings[1];
        }

        // CARNIVORES DIET
        if (canCarnivoresDiet) {
            carnivoresDietRank = calculateRank(Ranching.carnivoresDietMaxLevel, Ranching.carnivoresDietRankLevel1);
        }
    }

    @Override
    protected void permissionsCheck() {
        canMultipleBirth = Permissions.multipleBirth(player);
        canMasterHerder = Permissions.masterHerder(player);
        canShearsMastery = Permissions.shearsMastery(player);
        canArtisanButcher = Permissions.artisanButcher(player);
        canCarnivoresDiet = Permissions.carnivoresDiet(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canMultipleBirth || canMasterHerder || canShearsMastery || canArtisanButcher || canCarnivoresDiet;
    }

    @Override
    protected void effectsDisplay() {
        luckyEffectsDisplay();

        if (canMultipleBirth) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Ranching.Effect.0"), LocaleLoader.getString("Ranching.Effect.1")));
        }

        if (canMasterHerder) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Ranching.Effect.2"), LocaleLoader.getString("Ranching.Effect.3")));
        }

        if (canShearsMastery) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Ranching.Effect.4"), LocaleLoader.getString("Ranching.Effect.5")));
        }

        if (canArtisanButcher) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Ranching.Effect.6"), LocaleLoader.getString("Ranching.Effect.7")));
        }

        if (canCarnivoresDiet) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Ranching.Effect.8"), LocaleLoader.getString("Ranching.Effect.9")));
        }
    }

    @Override
    protected boolean statsHeaderPermissions() {
        return canMultipleBirth || canMasterHerder || canShearsMastery || canArtisanButcher || canCarnivoresDiet;
    }

    @Override
    protected void statsDisplay() {
        if (canMultipleBirth) {
            int unlockLevel = Ranching.multipleBirthIncreaseLevel;

            if (skillValue < unlockLevel) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Ranching.Ability.Locked.0", unlockLevel)));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Ranching.Ability.MultipleBirth", multipleBirthChance) + (isLucky ? LocaleLoader.getString("Perks.lucky.bonus", multipleBirthChanceLucky) : ""));
            }
        }

        if (canMasterHerder) {
            int unlockLevel = Ranching.masterHerderIncreaseLevel;

            if (skillValue < unlockLevel) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", LocaleLoader.getString("Ranching.Ability.Locked.1", unlockLevel)));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Ranching.Ability.MasterHerder", masterHerderTime));
            }
        }

        if (canShearsMastery) {
            player.sendMessage(LocaleLoader.getString("Ranching.Ability.ShearsMastery", shearsMasteryChance) + (isLucky ? LocaleLoader.getString("Perks.lucky.bonus", shearsMasteryChanceLucky) : ""));
        }

        if (canArtisanButcher) {
            player.sendMessage(LocaleLoader.getString("Ranching.Ability.ArtisanButcher", artisanButcherChance) + (isLucky ? LocaleLoader.getString("Perks.lucky.bonus", artisanButcherChanceLucky) : ""));
        }

        if (canCarnivoresDiet) {
            player.sendMessage(LocaleLoader.getString("Ranching.Ability.CarnivoresDiet", carnivoresDietRank));
        }
    }
}
