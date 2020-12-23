package com.gmail.nossr50.datatypes.skills;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.text.StringUtils;
import com.neetgames.mcmmo.api.SkillRegister;
import com.neetgames.mcmmo.skill.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Skills mcMMO is aware of are registered here
 * The skill register will be used for a few things in mcMMO
 * Removing a skill from the register doesn't mean it isn't doing anything as the register is simply for mcMMO's own awareness
 * When a player uses certain commands, such as checking their skill levels, if the skill isn't registered it won't be added to the resulting output of that command
 */
public class SkillRegisterImpl implements SkillRegister {
    //TODO: Move maps and collections to their own container
    private final @NotNull HashMap<String, Skill> skillNameMap;
    private final @NotNull Set<Skill> registeredSkills;
    private final @NotNull Set<SuperSkill> superSkills;
    private final @NotNull Set<RankedSkill> rankedSkills;
    private final @NotNull Set<RootSkill> rootSkills; //Can include not-official root skills
    private final @NotNull Set<RootSkill> coreRootSkills; //Only includes official root skills

    public SkillRegisterImpl() {
        skillNameMap = new HashMap<>();
        registeredSkills = new HashSet<>();
        rootSkills = new HashSet<>();
        superSkills = new HashSet<>();
        rankedSkills = new HashSet<>();
        coreRootSkills = new HashSet<>();

        //TODO: allow config to turn off certain core skills
        registerCoreSkills();
    }

    @Override
    public @Nullable Skill getSkill(@NotNull String fullyQualifiedName) {
        return skillNameMap.get(fullyQualifiedName);
    }

    @Override
    public @Nullable Skill getSkill(@NotNull SkillIdentity skillIdentity) {
        return skillNameMap.get(skillIdentity.getFullyQualifiedName());
    }

    @Override
    public @NotNull Set<SuperSkill> getSuperSkills() {
        return superSkills;
    }

    @Override
    public @NotNull Set<RankedSkill> getRankedSkills() {
        return rankedSkills;
    }

    @Override
    public @NotNull Set<RootSkill> getRootSkills() {
        return rootSkills;
    }

    @Override
    public boolean isSkillRegistered(@NotNull Skill skill) {
        return registeredSkills.contains(skill);
    }

    @Override
    public void registerSkill(@NotNull Skill skill) {
        registeredSkills.add(skill);
        addedSkillRegisterProcessing(skill);
    }

    @Override
    public void registerSkill(@NotNull Skill skill, boolean override) {
        if(isSkillRegistered(skill) && override) {
            registeredSkills.remove(skill);
        }

        registeredSkills.add(skill);
        addedSkillRegisterProcessing(skill);
    }

    @Override
    public @NotNull Set<Skill> getRegisteredSkills() {
        return registeredSkills;
    }

    private void postRemovalSkillRegisterProcessing(@NotNull Skill skill) {
        removeSkillNameLookup(skill);
        removeCollectionCache(skill);
    }

    private void removeCollectionCache(@NotNull Skill skill) {
        //Add to collections for cached lookups
        if(skill instanceof RootSkill) {
            rootSkills.remove(skill);
        } else if (skill instanceof SuperSkill) {
            superSkills.remove(skill);
        } else if(skill instanceof RankedSkill) {
            rankedSkills.remove( skill);
        }
    }

    private void removeSkillNameLookup(@NotNull Skill skill) {
        skillNameMap.remove(skill.getSkillIdentity().getFullyQualifiedName());
    }

    private void addedSkillRegisterProcessing(@NotNull Skill skill) {
        addSkillNameLookup(skill);
        addCollectionCache(skill);
    }

    private void addCollectionCache(@NotNull Skill skill) {
        //Add to various collections for cached lookups
        if(skill instanceof CoreRootSkill) {
            coreRootSkills.add((CoreRootSkill) skill);
        }

        if(skill instanceof RootSkill) {
            rootSkills.add((RootSkill) skill);
        } else if (skill instanceof SuperSkill) {
            superSkills.add((SuperSkill) skill);
        } else if(skill instanceof RankedSkill) {
            rankedSkills.add((RankedSkill) skill);
        }
    }

    private void addSkillNameLookup(@NotNull Skill skill) {
        skillNameMap.put(skill.getSkillIdentity().getFullyQualifiedName(), skill);
    }

    @Override
    public void unregisterSkill(@NotNull Skill skill) {
        mcMMO.p.getLogger().info("Skill "+skill.toString()+" has been removed from the skill register.");
        registeredSkills.remove(skill);
        skillNameMap.remove(skill.getSkillIdentity().getFullyQualifiedName());

        //Collection cache cleanup
        postRemovalSkillRegisterProcessing(skill);
    }

    private void registerCoreSkills() {
        for(RootSkill rootSkill : CoreSkills.getCoreSkills()) {
            mcMMO.p.getLogger().info("Registering core skill: "+rootSkill.getSkillName());
            registerSkill(rootSkill);
        }
    }

    @Override
    public @NotNull Set<RootSkill> getCoreRootSkills() {
        return coreRootSkills;
    }

    /**
     * Used to match skill by a "skill name"
     * This is NOT case sensitive
     *
     * Will match against any registered root skill if one of the following is true
     * 1) The skills localized name is equal to the provided {@link String skillName}
     * 2) The provided {@link String skillName} matches a root skill's fully qualified name
     * 3) The provided {@link String skillName} matches the name of the default name of the skill (the en_US not localized name, this name is never overridden by locale)
     *
     * @param skillName skill name or skill identity
     * @return The matching {@link RootSkill} if it exists
     * @see SkillIdentity#getFullyQualifiedName()
     */
    public @Nullable RootSkill matchRootSkill(@NotNull String skillName) {
        for (RootSkill rootSkill : rootSkills) {
            if (rootSkill.getSkillIdentity().getFullyQualifiedName().equalsIgnoreCase(skillName)
                    || skillName.equalsIgnoreCase(LocaleLoader.getString(StringUtils.getCapitalized(rootSkill.getSkillName()) + ".SkillName"))
                    || rootSkill.getSkillName().equalsIgnoreCase(skillName)) {
                return rootSkill;
            }
        }

        return null;
    }
}
