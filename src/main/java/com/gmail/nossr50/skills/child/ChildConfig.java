package com.gmail.nossr50.skills.child;

import com.gmail.nossr50.config.BukkitConfig;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.text.StringUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.EnumSet;
import java.util.Locale;

public class ChildConfig extends BukkitConfig {
    public ChildConfig() {
        super("child.yml");
        loadKeys();
    }

    @Override
    protected void validateConfigKeys() {
        //TODO: Rewrite legacy validation code
    }

    @Override
    protected void loadKeys() {
        config.setDefaults(YamlConfiguration.loadConfiguration(mcMMO.p.getResourceAsReader("child.yml")));

        FamilyTree.clearRegistrations(); // when reloading, need to clear statics

        for (PrimarySkillType skill : mcMMO.p.getSkillTools().CHILD_SKILLS) {
            mcMMO.p.debug("Finding parents of " + skill.name());

            EnumSet<PrimarySkillType> parentSkills = EnumSet.noneOf(PrimarySkillType.class);
            boolean useDefaults = false; // If we had an error we back out and use defaults

            for (String name : config.getStringList(StringUtils.getCapitalized(skill.name()))) {
                try {
                    PrimarySkillType parentSkill = PrimarySkillType.valueOf(name.toUpperCase(Locale.ENGLISH));
                    FamilyTree.enforceNotChildSkill(parentSkill);
                    parentSkills.add(parentSkill);
                }
                catch (IllegalArgumentException ex) {
                    mcMMO.p.getLogger().warning(name + " is not a valid skill type, or is a child skill!");
                    useDefaults = true;
                    break;
                }
            }

            if (useDefaults) {
                parentSkills.clear();
                for (String name : config.getDefaults().getStringList(StringUtils.getCapitalized(skill.name()))) {
                    /* We do less checks in here because it's from inside our jar.
                     * If they're dedicated enough to have modified it, they can have the errors it may produce.
                     * Alternatively, this can be used to allow child skills to be parent skills, provided there are no circular dependencies this is an advanced sort of configuration.
                     */
                    parentSkills.add(PrimarySkillType.valueOf(name.toUpperCase(Locale.ENGLISH)));
                }
            }

            // Register them
            for (PrimarySkillType parentSkill : parentSkills) {
                mcMMO.p.debug("Registering " + parentSkill.name() + " as parent of " + skill.name());
                FamilyTree.registerParent(skill, parentSkill);
            }
        }

        FamilyTree.closeRegistration();
    }
}
