package com.gmail.nossr50.skills.child;

import java.util.EnumSet;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.nossr50.config.AutoUpdateConfigLoader;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.util.StringUtils;

public class ChildConfig extends AutoUpdateConfigLoader {
    public ChildConfig() {
        super("child.yml");
        loadKeys();
    }

    @Override
    protected void loadKeys() {
        config.setDefaults(YamlConfiguration.loadConfiguration(plugin.getResource("child.yml")));

        for (SkillType skill : SkillType.values()) {
            if (skill.isChildSkill()) {
                plugin.debug("Finding parents of " + skill.name());

                List<String> parentNames = config.getStringList(StringUtils.getCapitalized(skill.name()));
                EnumSet<SkillType> parentSkills = EnumSet.noneOf(SkillType.class);
                boolean useDefaults = false; // If we had an error we back out and use defaults

                for (String name : parentNames) {
                    try {
                        SkillType parentSkill = Enum.valueOf(SkillType.class, name.toUpperCase());
                        FamilyTree.enforceNotChildSkill(parentSkill);
                        parentSkills.add(parentSkill);
                    }
                    catch (IllegalArgumentException ex) {
                        plugin.getLogger().warning(name + " is not a valid skill type, or is a child skill!");
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
                        parentSkills.add(SkillType.valueOf(name.toUpperCase()));
                    }
                }

                // Register them
                for (SkillType parentSkill : parentSkills) {
                    plugin.debug("Registering " + parentSkill.name() + " as parent of " + skill.name());
                    FamilyTree.registerParent(skill, parentSkill);
                }
            }
        }

        FamilyTree.closeRegistration();
    }
}
