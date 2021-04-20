package com.gmail.nossr50.skills.child;

import com.gmail.nossr50.mcMMO;
import com.neetgames.mcmmo.exceptions.UnknownSkillException;
import com.neetgames.mcmmo.skill.RootSkill;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.gmail.nossr50.util.skills.SkillTools;

import java.util.HashSet;
import java.util.Set;

public class FamilyTree {

    /*
     * Hacky crap, will remove later
     */
    private static @Nullable Set<RootSkill> smeltingParents;
    private static @Nullable Set<RootSkill> salvageParents;

    public static @NotNull Set<RootSkill> getParentSkills(@NotNull PrimarySkillType primarySkillType) throws UnknownSkillException {
        if(PrimarySkillType.isChildSkill(primarySkillType)) {
            if(smeltingParents == null || salvageParents == null) {
                smeltingParents = new HashSet<>();
                salvageParents = new HashSet<>();

                smeltingParents.add(PrimarySkillType.MINING);
                smeltingParents.add(PrimarySkillType.REPAIR);

                salvageParents.add(PrimarySkillType.FISHING);
                salvageParents.add(PrimarySkillType.REPAIR);
            }

            if(primarySkillType.equals(PrimarySkillType.SALVAGE)) {
                return salvageParents;
            } else if (primarySkillType.equals(PrimarySkillType.SMELTING)) {
                return smeltingParents;
            } else {
                mcMMO.p.getLogger().severe("root skill argument is not a child skill! " + primarySkillType.toString());
                throw new UnknownSkillException();
            }

        } else {
            throw new UnknownSkillException();
        }
    }
}
