package com.gmail.nossr50.skills.child;

import com.gmail.nossr50.datatypes.skills.CoreSkills;
import com.gmail.nossr50.mcMMO;
import com.neetgames.mcmmo.exceptions.UnknownSkillException;
import com.neetgames.mcmmo.skill.RootSkill;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class FamilyTree {

    /*
     * Hacky crap, will remove later
     */
    private static @Nullable Set<RootSkill> smeltingParents;
    private static @Nullable Set<RootSkill> salvageParents;

    public static @NotNull Set<RootSkill> getParentSkills(@NotNull RootSkill rootSkill) throws UnknownSkillException {
        if(PrimarySkillType.isChildSkill(rootSkill)) {
            if(smeltingParents == null || salvageParents == null) {
                smeltingParents = new HashSet<>();
                salvageParents = new HashSet<>();

                smeltingParents.add(PrimarySkillType.MINING);
                smeltingParents.add(PrimarySkillType.REPAIR);

                salvageParents.add(PrimarySkillType.FISHING);
                salvageParents.add(PrimarySkillType.REPAIR);
            }

            if(rootSkill.equals(PrimarySkillType.SALVAGE)) {
                return salvageParents;
            } else if (rootSkill.equals(PrimarySkillType.SMELTING)) {
                return smeltingParents;
            } else {
                mcMMO.p.getLogger().severe("root skill argument is not a child skill! " + rootSkill.toString());
                throw new UnknownSkillException();
            }

        } else {
            throw new UnknownSkillException();
        }
    }
}
