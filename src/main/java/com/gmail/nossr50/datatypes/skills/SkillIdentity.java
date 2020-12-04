package com.gmail.nossr50.datatypes.skills;

import com.google.common.base.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//TODO: This documentation is a rough draft and will be rewritten
/**
 * Used to identify skills
 * The goal of this class is to avoid namespace conflicts and clearly identify a skill with its established relationship to other skills as needed
 *
 * This class will include representation of the relationship between skills, which includes whether or not one skill is parented by another
 * Skills are not aware of their children, but they can be aware of their parents
 * You will be able to use the skill register to grab all children of a skill, but no SkillIdentity will be defined with children, the burden of the relationship declaration is on the children
 *
 * Any skill with a null parent skill within its SkillIdentity will be considered the skill at the top of the food chain, which at this point has been referred to as a {@link PrimarySkill}
 * Any skill with a parent will be considered a SubSkill and treated completely differently
 *
 * Skills with parents do not gain experience, and instead their intended effects will be based on the strength of the parent skill (its level)
 * Skills are registered, no two skills can share the same fully qualified name (in this case, a combination of the namespace and skill name)
 *
 * A fully qualified name is generated based on the namespace and skill name
 * @see #genFullyQualifiedName()
 */
public class SkillIdentity {
    @NotNull private final String nameSpace;
    @NotNull private final String skillName;
    @Nullable private final SkillIdentity parentSkill;

    @NotNull private final String fullyQualifiedName;

    public SkillIdentity(@NotNull String nameSpace, @NotNull String skillName, @Nullable SkillIdentity parentSkill) {
        this.nameSpace = nameSpace;
        this.skillName = skillName;
        this.parentSkill = parentSkill;

        fullyQualifiedName = genFullyQualifiedName();
    }

    public SkillIdentity(@NotNull String nameSpace, @NotNull String skillName) {
        this.nameSpace = nameSpace;
        this.skillName = skillName;
        this.parentSkill = null;

        fullyQualifiedName = genFullyQualifiedName();
    }

    /**
     * Creates a fully qualified name for this skill
     * @return the fully qualified name for this skill
     */
    private String genFullyQualifiedName() {
        return nameSpace + ":" + "skillName";
    }

    /**
     * Whether or not this skill has a parent
     * @return true if this skill has a parent
     */
    public boolean hasParent() {
        return parentSkill != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkillIdentity that = (SkillIdentity) o;
        return Objects.equal(nameSpace, that.nameSpace) && Objects.equal(skillName, that.skillName) && Objects.equal(parentSkill, that.parentSkill) && Objects.equal(fullyQualifiedName, that.fullyQualifiedName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nameSpace, skillName, parentSkill, fullyQualifiedName);
    }
}
