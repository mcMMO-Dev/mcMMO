package com.gmail.nossr50.datatypes.skills;

import com.gmail.nossr50.config.GeneralConfig;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.SkillTools;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public enum PrimarySkillType {
    ACROBATICS,
    ALCHEMY,
    ARCHERY,
    AXES,
    EXCAVATION,
    FISHING,
    HERBALISM,
    MINING,
    REPAIR,
    SALVAGE,
    SMELTING,
    SWORDS,
    TAMING,
    UNARMED,
    WOODCUTTING;
//    boolean issueWarning = true;

    /*
     *  Everything below here will be removed in 2.2 (Tridents & Crossbows)
     *  Everything below here will be removed in 2.2 (Tridents & Crossbows)
     *  Everything below here will be removed in 2.2 (Tridents & Crossbows)
     *  Everything below here will be removed in 2.2 (Tridents & Crossbows)
     *  Everything below here will be removed in 2.2 (Tridents & Crossbows)
     *  Everything below here will be removed in 2.2 (Tridents & Crossbows)
     *  Everything below here will be removed in 2.2 (Tridents & Crossbows)
     *  Everything below here will be removed in 2.2 (Tridents & Crossbows)
     *  Everything below here will be removed in 2.2 (Tridents & Crossbows)
     *  Everything below here will be removed in 2.2 (Tridents & Crossbows)
     *  Everything below here will be removed in 2.2 (Tridents & Crossbows)
     *  Everything below here will be removed in 2.2 (Tridents & Crossbows)
     *  Everything below here will be removed in 2.2 (Tridents & Crossbows)
     *  Everything below here will be removed in 2.2 (Tridents & Crossbows)
     *  Everything below here will be removed in 2.2 (Tridents & Crossbows)
     *  Everything below here will be removed in 2.2 (Tridents & Crossbows)
     */


//    private void processWarning() {
//        if(issueWarning) {
//            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
//            Bukkit.getScheduler().scheduleSyncDelayedTask(mcMMO.p, () -> {
//                mcMMO.p.getLogger().severe("A plugin that hooks into mcMMO via the mcMMO API is using soon to be deprecated API calls. Contact the plugin author and inform them to update their code before it breaks.");
//                mcMMO.p.getLogger().severe("Deprecation Call from: " + stackTraceElements[2].toString());
//                mcMMO.p.getLogger().severe("This warning will not repeat itself. Nothing is broken for now, but in the future it will be.");
//            });
//
//            issueWarning = !issueWarning;
//        }
//    }

    /**
     * WARNING: Being removed in an upcoming update, you should be using mcMMO.getSkillTools() instead
     * @return the max level of this skill
     * @see SkillTools#getLevelCap(com.gmail.nossr50.datatypes.skills.PrimarySkillType)
     * @deprecated this is being removed in an upcoming update, you should be using mcMMO.getSkillTools() instead
     */
    @Deprecated
    public int getMaxLevel() {
        return mcMMO.p.getSkillTools().getLevelCap(this);
    }

    /**
     * WARNING: Being removed in an upcoming update, you should be using mcMMO.getSkillTools() instead
     * @return the max level of this skill
     * @see SkillTools#isSuperAbilityUnlocked(com.gmail.nossr50.datatypes.skills.PrimarySkillType, org.bukkit.entity.Player)
     * @deprecated this is being removed in an upcoming update, you should be using mcMMO.getSkillTools() instead
     */
    @Deprecated
    public boolean isSuperAbilityUnlocked(@NotNull Player player) {
        return mcMMO.p.getSkillTools().isSuperAbilityUnlocked(this, player);
    }

    /**
     * WARNING: Being removed in an upcoming update, you should be using mcMMO.getSkillTools() instead
     * @return the max level of this skill
     * @see SkillTools#getPVPEnabled(com.gmail.nossr50.datatypes.skills.PrimarySkillType)
     * @deprecated this is being removed in an upcoming update, you should be using mcMMO.getSkillTools() instead
     */
    @Deprecated
    public boolean getPVPEnabled() {
        return mcMMO.p.getSkillTools().getPVPEnabled(this);
    }

    /**
     * WARNING: Being removed in an upcoming update, you should be using mcMMO.getSkillTools() instead
     * @return the max level of this skill
     * @see SkillTools#getPVEEnabled(com.gmail.nossr50.datatypes.skills.PrimarySkillType)
     * @deprecated this is being removed in an upcoming update, you should be using mcMMO.getSkillTools() instead
     */
    @Deprecated
    public boolean getPVEEnabled() {
        return mcMMO.p.getSkillTools().getPVEEnabled(this);
    }

    /**
     * WARNING: Being removed in an upcoming update, you should be using mcMMO.getSkillTools() instead
     * @return the max level of this skill
     * @see GeneralConfig#getDoubleDropsDisabled(com.gmail.nossr50.datatypes.skills.PrimarySkillType)
     * @deprecated this is being removed in an upcoming update, you should be using mcMMO.getSkillTools() instead
     */
    @Deprecated
    public boolean getDoubleDropsDisabled() {
        return mcMMO.p.getGeneralConfig().getDoubleDropsDisabled(this);
    }

    /**
     * WARNING: Being removed in an upcoming update, you should be using mcMMO.getSkillTools() instead
     * @return the max level of this skill
     * @see SkillTools#getHardcoreStatLossEnabled(com.gmail.nossr50.datatypes.skills.PrimarySkillType)
     * @deprecated this is being removed in an upcoming update, you should be using mcMMO.getSkillTools() instead
     */
    @Deprecated
    public boolean getHardcoreStatLossEnabled() {
        return mcMMO.p.getSkillTools().getHardcoreStatLossEnabled(this);
    }

    /**
     * WARNING: Being removed in an upcoming update, you should be using mcMMO.getSkillTools() instead
     * @return the max level of this skill
     * @see SkillTools#getHardcoreVampirismEnabled(com.gmail.nossr50.datatypes.skills.PrimarySkillType)
     * @deprecated this is being removed in an upcoming update, you should be using mcMMO.getSkillTools() instead
     */
    @Deprecated
    public boolean getHardcoreVampirismEnabled() {
        return mcMMO.p.getSkillTools().getHardcoreVampirismEnabled(this);
    }

    /**
     * WARNING: Being removed in an upcoming update, you should be using mcMMO.getSkillTools() instead
     * @return the max level of this skill
     * @see SkillTools#getPrimarySkillToolType(com.gmail.nossr50.datatypes.skills.PrimarySkillType)
     * @deprecated this is being removed in an upcoming update, you should be using mcMMO.getSkillTools() instead
     */
    @Deprecated
    public ToolType getTool() {
        return mcMMO.p.getSkillTools().getPrimarySkillToolType(this);
    }

    /**
     * WARNING: Being removed in an upcoming update, you should be using mcMMO.getSkillTools() instead
     * @return the max level of this skill
     * @see SkillTools#getSubSkills(com.gmail.nossr50.datatypes.skills.PrimarySkillType)
     * @deprecated this is being removed in an upcoming update, you should be using mcMMO.getSkillTools() instead
     */
    @Deprecated
    public List<SubSkillType> getSkillAbilities() {
        return new ArrayList<>(mcMMO.p.getSkillTools().getSubSkills(this));
    }

    /**
     * WARNING: Being removed in an upcoming update, you should be using mcMMO.getSkillTools() instead
     * @return the max level of this skill
     * @see SkillTools#getXpModifier(com.gmail.nossr50.datatypes.skills.PrimarySkillType)
     * @deprecated this is being removed in an upcoming update, you should be using mcMMO.getSkillTools() instead
     */
    @Deprecated
    public double getXpModifier() {
        return mcMMO.p.getSkillTools().getXpModifier(this);
    }

    /**
     * WARNING: Being removed in an upcoming update, you should be using mcMMO.getSkillTools() instead
     * @return the max level of this skill
     * @see SkillTools#matchSkill(java.lang.String)
     * @deprecated this is being removed in an upcoming update, you should be using mcMMO.getSkillTools() instead
     */
    @Deprecated
    public static PrimarySkillType getSkill(String skillName) {
        return mcMMO.p.getSkillTools().matchSkill(skillName);
    }

    /**
     * WARNING: Being removed in an upcoming update, you should be using mcMMO.getSkillTools() instead
     * @return the max level of this skill
     * @see SkillTools#isChildSkill(com.gmail.nossr50.datatypes.skills.PrimarySkillType)
     * @deprecated this is being removed in an upcoming update, you should be using mcMMO.getSkillTools() instead
     */
    @Deprecated
    public boolean isChildSkill() {
        return SkillTools.isChildSkill(this);
    }

    /**
     * WARNING: Being removed in an upcoming update, you should be using mcMMO.getSkillTools() instead
     * @return the max level of this skill
     * @see SkillTools#getPrimarySkillBySubSkill(com.gmail.nossr50.datatypes.skills.SubSkillType)
     * @deprecated this is being removed in an upcoming update, you should be using mcMMO.getSkillTools() instead
     */
    @Deprecated
    public static PrimarySkillType bySecondaryAbility(SubSkillType subSkillType) {
        return mcMMO.p.getSkillTools().getPrimarySkillBySubSkill(subSkillType);
    }

    /**
     * WARNING: Being removed in an upcoming update, you should be using mcMMO.getSkillTools() instead
     * @return the max level of this skill
     * @see SkillTools#getPrimarySkillBySuperAbility(com.gmail.nossr50.datatypes.skills.SuperAbilityType)
     * @deprecated this is being removed in an upcoming update, you should be using mcMMO.getSkillTools() instead
     */
    @Deprecated
    public static PrimarySkillType byAbility(SuperAbilityType superAbilityType) {
        return mcMMO.p.getSkillTools().getPrimarySkillBySuperAbility(superAbilityType);
    }

    /**
     * WARNING: Being removed in an upcoming update, you should be using mcMMO.getSkillTools() instead
     * @return the max level of this skill
     * @see SkillTools#getLocalizedSkillName(com.gmail.nossr50.datatypes.skills.PrimarySkillType)
     * @deprecated this is being removed in an upcoming update, you should be using mcMMO.getSkillTools() instead
     */
    @Deprecated
    public String getName() {
        return mcMMO.p.getSkillTools().getLocalizedSkillName(this);
    }

    /**
     * WARNING: Being removed in an upcoming update, you should be using mcMMO.getSkillTools() instead
     * @return the max level of this skill
     * @see Permissions#skillEnabled(org.bukkit.permissions.Permissible, com.gmail.nossr50.datatypes.skills.PrimarySkillType)
     * @deprecated this is being removed in an upcoming update
     */
    @Deprecated
    public boolean getPermissions(Player player) {
        return mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(player, this);
    }

    /**
     * WARNING: Being removed in an upcoming update, you should be using mcMMO.getSkillTools() instead
     * @return the max level of this skill
     * @see SkillTools#canCombatSkillsTrigger(com.gmail.nossr50.datatypes.skills.PrimarySkillType, org.bukkit.entity.Entity)
     * @deprecated this is being removed in an upcoming update, you should be using mcMMO.getSkillTools() instead
     */
    @Deprecated
    public boolean shouldProcess(Entity target) {
        return mcMMO.p.getSkillTools().canCombatSkillsTrigger(this, target);
    }
}