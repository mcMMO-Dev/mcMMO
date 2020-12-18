package com.gmail.nossr50.skills.child;

import com.gmail.nossr50.datatypes.skills.CoreSkillConstants;
import com.neetgames.mcmmo.exceptions.UnknownSkillException;
import com.neetgames.mcmmo.skill.SkillIdentity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class FamilyTree {

    /*
     * Hacky crap, will remove later
     */

    private static @Nullable Set<SkillIdentity> smeltingParents;
    private static @Nullable Set<SkillIdentity> salvageParents;

    public static @NotNull Set<SkillIdentity> getParentSkills(@NotNull SkillIdentity skillIdentity) throws UnknownSkillException {
        if(CoreSkillConstants.isChildSkill(skillIdentity)) {
            if(smeltingParents == null || salvageParents == null) {
                smeltingParents = new HashSet<>();
                salvageParents = new HashSet<>();

                smeltingParents.add(CoreSkillConstants.MINING_ID);
                smeltingParents.add(CoreSkillConstants.REPAIR_ID);

                salvageParents.add(CoreSkillConstants.FISHING_ID);
                salvageParents.add(CoreSkillConstants.REPAIR_ID);
            }

            if(skillIdentity.equals(CoreSkillConstants.SALVAGE_ID)) {
                return salvageParents;
            } else {
                return smeltingParents;
            }

        } else {
            throw new UnknownSkillException();
        }
    }
}
