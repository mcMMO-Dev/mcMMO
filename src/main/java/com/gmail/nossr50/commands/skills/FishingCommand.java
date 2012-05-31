package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.commands.SkillCommand;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.gathering.Fishing;

public class FishingCommand extends SkillCommand {
    private int lootTier;
    private String magicChance;

    private boolean canTreasureHunt;
    private boolean canMagicHunt;
    private boolean canShake;

    public FishingCommand() {
        super(SkillType.FISHING);
    }

    @Override
    protected void dataCalculations() {
        lootTier = Fishing.getFishingLootTier(profile);
        magicChance = percent.format((float) lootTier / 15);
    }

    @Override
    protected void permissionsCheck() {
        canTreasureHunt = permInstance.fishingTreasures(player);
        canMagicHunt = permInstance.fishingMagic(player);
        canShake = permInstance.shakeMob(player);
    }

    @Override
    protected boolean effectsHeaderPermissions() {
        return canTreasureHunt || canMagicHunt || canShake;
    }

    @Override
    protected void effectsDisplay() {
        if (canTreasureHunt) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Fishing.Effect.0"), LocaleLoader.getString("Fishing.Effect.1") }));
        }

        if (canMagicHunt) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Fishing.Effect.2"), LocaleLoader.getString("Fishing.Effect.3") }));
        }

        if (canShake) {
            player.sendMessage(LocaleLoader.getString("Effects.Template", new Object[] { LocaleLoader.getString("Fishing.Effect.4"), LocaleLoader.getString("Fishing.Effect.5") }));
        }
    }

    @Override
    protected boolean statsHeaderPermissions() {
        return canTreasureHunt || canMagicHunt || canShake;
    }

    @Override
    protected void statsDisplay() {
        if (canTreasureHunt) {
            player.sendMessage(LocaleLoader.getString("Fishing.Ability.Rank", new Object[] { lootTier }));
        }

        if (canMagicHunt) {
            player.sendMessage(LocaleLoader.getString("Fishing.Enchant.Chance", new Object[] { magicChance }));
        }

        if (canShake) {
            //TODO: Do we really need to display this twice? Not like there are any associated stats.
            if (skillValue < 150) {
                player.sendMessage(LocaleLoader.getString("Ability.Generic.Template.Lock", new Object[] { LocaleLoader.getString("Fishing.Ability.Locked.0") }));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Fishing.Ability.Shake"));
            }
        }
    }
}
