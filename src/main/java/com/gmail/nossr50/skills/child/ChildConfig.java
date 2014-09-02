package com.gmail.nossr50.skills.child;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.nossr50.config.AutoUpdateConfigLoader;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.util.StringUtils;

public class ChildConfig extends AutoUpdateConfigLoader {
	private static ChildConfig INSTANCE = null;
	
    public ChildConfig() {
        super("child.yml");
        loadKeys();
        INSTANCE = this;
    }

    @Override
    protected void loadKeys() {
        config.setDefaults(YamlConfiguration.loadConfiguration(plugin.getResource("child.yml")));

        FamilyTree.clearRegistrations(); // when reloading, need to clear statics

        for (SkillType skill : SkillType.getChildSkills()) {
            plugin.debug("Finding parents of " + skill.getLocalizedName());

            Set<SkillType> parentSkills = new HashSet<SkillType>();
            boolean useDefaults = false; // If we had an error we back out and use defaults

            for (String name : config.getStringList(StringUtils.getCapitalized(skill.getName()))) {
                try {
                    SkillType parentSkill = SkillType.getSkill(name);
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
                try {
	                for (String name : config.getDefaults().getStringList(StringUtils.getCapitalized(skill.getName()))) {
	                    /* We do less checks in here because it's from inside our jar.
	                     * If they're dedicated enough to have modified it, they can have the errors it may produce.
	                     * Alternatively, this can be used to allow child skills to be parent skills, provided there are no circular dependencies this is an advanced sort of configuration.
	                     */
	                    parentSkills.add(SkillType.getSkill(name));
	                }
                }
                catch(Exception e) {}
            }

            // Register them
            for (SkillType parentSkill : parentSkills) {
                plugin.debug("Registering " + parentSkill.getName() + " as parent of " + skill.getName());
                FamilyTree.registerParent(skill, parentSkill);
            }
        }

        FamilyTree.closeRegistration();
    }
    
    public void addParents(SkillType childSkill, SkillType... parents) {
    	if(!config.contains(StringUtils.getCapitalized(childSkill.getName()))) {
    		String[] parentStrings = new String[parents.length];
    		for(int i = 0; i < parents.length; i++) {
    			parentStrings[i] = StringUtils.getCapitalized(parents[i].getName());
    		}
    		config.set(StringUtils.getCapitalized(childSkill.getName()), parentStrings);
    		loadKeys();
    	}
    }
    
    public static ChildConfig getInstance() {
    	return INSTANCE;
    }
}
