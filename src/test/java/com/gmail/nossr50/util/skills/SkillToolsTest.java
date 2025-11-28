package com.gmail.nossr50.util.skills;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.config.GeneralConfig;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.compat.CompatibilityManager;
import com.gmail.nossr50.util.platform.MinecraftGameVersion;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

@TestInstance(Lifecycle.PER_CLASS)
class SkillToolsTest {

    private static final @NotNull Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private static MockedStatic<mcMMO> mockedMcMMO;
    private static MockedStatic<LocaleLoader> mockedLocaleLoader;

    private GeneralConfig generalConfig;
    private CompatibilityManager compatibilityManager;

    @BeforeAll
    void setUpAll() {
        // Static mcMMO + LocaleLoader mocks
        mockedMcMMO = Mockito.mockStatic(mcMMO.class);
        mockedLocaleLoader = Mockito.mockStatic(LocaleLoader.class);

        // Plugin instance
        mcMMO.p = mock(mcMMO.class);
        when(mcMMO.p.getLogger()).thenReturn(logger);

        // General config
        generalConfig = mock(GeneralConfig.class);
        when(mcMMO.p.getGeneralConfig()).thenReturn(generalConfig);
        when(generalConfig.getLocale()).thenReturn("en_US");

        // Compatibility manager + game version
        compatibilityManager = mock(CompatibilityManager.class);
        when(mcMMO.getCompatibilityManager()).thenReturn(compatibilityManager);

        // LocaleLoader – just echo key back to keep things simple/deterministic
        mockedLocaleLoader.when(() -> LocaleLoader.getString(anyString()))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @AfterAll
    void tearDownAll() {
        mockedLocaleLoader.close();
        mockedMcMMO.close();
    }

    private SkillTools newSkillToolsForVersion(int major, int minor, int patch) throws Exception {
        when(compatibilityManager.getMinecraftGameVersion())
                .thenReturn(new MinecraftGameVersion(major, minor, patch));
        return new SkillTools(mcMMO.p);
    }

    // ------------------------------------------------------------------------
    // NON_CHILD_SKILLS / isChildSkill / CHILD_SKILLS
    // ------------------------------------------------------------------------

    @Test
    void nonChildSkillsShouldContainAllPrimarySkillsExceptSalvageAndSmelting() {
        List<PrimarySkillType> expected = Arrays.stream(PrimarySkillType.values())
                .filter(t -> t != PrimarySkillType.SALVAGE && t != PrimarySkillType.SMELTING)
                .collect(Collectors.toList());

        assertThat(SkillTools.NON_CHILD_SKILLS)
                .containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void isChildSkillShouldReturnTrueOnlyForSalvageAndSmelting() {
        for (PrimarySkillType type : PrimarySkillType.values()) {
            boolean isChild = SkillTools.isChildSkill(type);

            if (type == PrimarySkillType.SALVAGE || type == PrimarySkillType.SMELTING) {
                assertThat(isChild)
                        .as("%s should be considered a child skill", type)
                        .isTrue();
            } else {
                assertThat(isChild)
                        .as("%s should NOT be considered a child skill", type)
                        .isFalse();
            }
        }
    }

    @Test
    void childSkillsListShouldMatchIsChildSkillClassification() throws Exception {
        SkillTools skillTools = newSkillToolsForVersion(1, 21, 11);

        List<PrimarySkillType> expectedChildren = Arrays.stream(PrimarySkillType.values())
                .filter(SkillTools::isChildSkill)
                .collect(Collectors.toList());

        assertThat(skillTools.getChildSkills())
                .containsExactlyInAnyOrderElementsOf(expectedChildren);
    }

    // ------------------------------------------------------------------------
    // Child skill parents (SALVAGE_PARENTS / SMELTING_PARENTS / getChildSkillParents)
    // ------------------------------------------------------------------------

    @Test
    void childSkillParentsShouldMatchStaticParentLists() throws Exception {
        SkillTools skillTools = newSkillToolsForVersion(1, 21, 11);

        assertThat(skillTools.getChildSkillParents(PrimarySkillType.SALVAGE))
                .as("SALVAGE parents")
                .containsExactlyElementsOf(SkillTools.SALVAGE_PARENTS);

        assertThat(skillTools.getChildSkillParents(PrimarySkillType.SMELTING))
                .as("SMELTING parents")
                .containsExactlyElementsOf(SkillTools.SMELTING_PARENTS);
    }

    @Test
    void getChildSkillParentsShouldThrowForNonChildSkill() throws Exception {
        SkillTools skillTools = newSkillToolsForVersion(1, 21, 11);

        assertThatThrownBy(() -> skillTools.getChildSkillParents(PrimarySkillType.MINING))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("is not a child skill");
    }

    // ------------------------------------------------------------------------
    // Super ability ↔ primary skill relationships
    // ------------------------------------------------------------------------

    @Test
    void superAbilityParentMappingShouldMatchDefinedSwitch() throws Exception {
        SkillTools skillTools = newSkillToolsForVersion(1, 21, 11);

        assertThat(skillTools.getPrimarySkillBySuperAbility(SuperAbilityType.BERSERK))
                .isEqualTo(PrimarySkillType.UNARMED);
        assertThat(skillTools.getPrimarySkillBySuperAbility(SuperAbilityType.GREEN_TERRA))
                .isEqualTo(PrimarySkillType.HERBALISM);
        assertThat(skillTools.getPrimarySkillBySuperAbility(SuperAbilityType.TREE_FELLER))
                .isEqualTo(PrimarySkillType.WOODCUTTING);
        assertThat(skillTools.getPrimarySkillBySuperAbility(SuperAbilityType.SUPER_BREAKER))
                .isEqualTo(PrimarySkillType.MINING);
        assertThat(skillTools.getPrimarySkillBySuperAbility(SuperAbilityType.BLAST_MINING))
                .isEqualTo(PrimarySkillType.MINING);
        assertThat(skillTools.getPrimarySkillBySuperAbility(SuperAbilityType.SKULL_SPLITTER))
                .isEqualTo(PrimarySkillType.AXES);
        assertThat(skillTools.getPrimarySkillBySuperAbility(SuperAbilityType.SERRATED_STRIKES))
                .isEqualTo(PrimarySkillType.SWORDS);
        assertThat(skillTools.getPrimarySkillBySuperAbility(SuperAbilityType.GIGA_DRILL_BREAKER))
                .isEqualTo(PrimarySkillType.EXCAVATION);
        assertThat(skillTools.getPrimarySkillBySuperAbility(SuperAbilityType.SUPER_SHOTGUN))
                .isEqualTo(PrimarySkillType.CROSSBOWS);
        assertThat(skillTools.getPrimarySkillBySuperAbility(SuperAbilityType.TRIDENTS_SUPER_ABILITY))
                .isEqualTo(PrimarySkillType.TRIDENTS);
        assertThat(skillTools.getPrimarySkillBySuperAbility(SuperAbilityType.EXPLOSIVE_SHOT))
                .isEqualTo(PrimarySkillType.ARCHERY);
        assertThat(skillTools.getPrimarySkillBySuperAbility(SuperAbilityType.MACES_SUPER_ABILITY))
                .isEqualTo(PrimarySkillType.MACES);
        assertThat(skillTools.getPrimarySkillBySuperAbility(SuperAbilityType.SPEARS_SUPER_ABILITY))
                .isEqualTo(PrimarySkillType.SPEARS);
    }

    @Test
    void mainActivatedAbilityChildMapShouldOmitBlastMiningAndMapOthersBackToAbility() throws Exception {
        SkillTools skillTools = newSkillToolsForVersion(1, 21, 11);

        // All super abilities EXCEPT BLAST_MINING should be discoverable via getSuperAbility()
        assertThat(skillTools.getSuperAbility(PrimarySkillType.MINING))
                .as("MINING should not expose BLAST_MINING as the 'main' tool-readied ability")
                .isEqualTo(SuperAbilityType.SUPER_BREAKER);

        assertThat(skillTools.getSuperAbility(PrimarySkillType.UNARMED))
                .isEqualTo(SuperAbilityType.BERSERK);
        assertThat(skillTools.getSuperAbility(PrimarySkillType.HERBALISM))
                .isEqualTo(SuperAbilityType.GREEN_TERRA);
        assertThat(skillTools.getSuperAbility(PrimarySkillType.WOODCUTTING))
                .isEqualTo(SuperAbilityType.TREE_FELLER);
        assertThat(skillTools.getSuperAbility(PrimarySkillType.AXES))
                .isEqualTo(SuperAbilityType.SKULL_SPLITTER);
        assertThat(skillTools.getSuperAbility(PrimarySkillType.SWORDS))
                .isEqualTo(SuperAbilityType.SERRATED_STRIKES);
        assertThat(skillTools.getSuperAbility(PrimarySkillType.EXCAVATION))
                .isEqualTo(SuperAbilityType.GIGA_DRILL_BREAKER);
        assertThat(skillTools.getSuperAbility(PrimarySkillType.CROSSBOWS))
                .isEqualTo(SuperAbilityType.SUPER_SHOTGUN);
        assertThat(skillTools.getSuperAbility(PrimarySkillType.TRIDENTS))
                .isEqualTo(SuperAbilityType.TRIDENTS_SUPER_ABILITY);
        assertThat(skillTools.getSuperAbility(PrimarySkillType.ARCHERY))
                .isEqualTo(SuperAbilityType.EXPLOSIVE_SHOT);
        assertThat(skillTools.getSuperAbility(PrimarySkillType.MACES))
                .isEqualTo(SuperAbilityType.MACES_SUPER_ABILITY);
        assertThat(skillTools.getSuperAbility(PrimarySkillType.SPEARS))
                .isEqualTo(SuperAbilityType.SPEARS_SUPER_ABILITY);

        // Skills without a main activated ability should return null
        assertThat(skillTools.getSuperAbility(PrimarySkillType.REPAIR)).isNull();
        assertThat(skillTools.getSuperAbility(PrimarySkillType.FISHING)).isNull();
    }

    // ------------------------------------------------------------------------
    // Sub-skill → primary-skill mapping (name prefix convention)
    // ------------------------------------------------------------------------

    @Test
    void primarySkillBySubSkillShouldFollowNamePrefixConvention() throws Exception {
        SkillTools skillTools = newSkillToolsForVersion(1, 21, 11);

        for (SubSkillType sub : SubSkillType.values()) {
            PrimarySkillType parent = skillTools.getPrimarySkillBySubSkill(sub);

            assertThat(parent)
                    .as("SubSkill %s should have a parent PrimarySkillType", sub)
                    .isNotNull();

            String subName = sub.name().toUpperCase(Locale.ENGLISH);
            String parentPrefix = parent.name().toUpperCase(Locale.ENGLISH);

            assertThat(subName.startsWith(parentPrefix))
                    .as("SubSkill %s should start with its parent skill name %s", subName, parentPrefix)
                    .isTrue();
        }
    }

    // ------------------------------------------------------------------------
    // primarySkillToolMap
    // ------------------------------------------------------------------------

    @Test
    void primarySkillToolTypeMappingShouldMatchDefinition() throws Exception {
        SkillTools skillTools = newSkillToolsForVersion(1, 21, 11);

        assertThat(skillTools.getPrimarySkillToolType(PrimarySkillType.AXES))
                .isEqualTo(ToolType.AXE);
        assertThat(skillTools.getPrimarySkillToolType(PrimarySkillType.WOODCUTTING))
                .isEqualTo(ToolType.AXE);
        assertThat(skillTools.getPrimarySkillToolType(PrimarySkillType.UNARMED))
                .isEqualTo(ToolType.FISTS);
        assertThat(skillTools.getPrimarySkillToolType(PrimarySkillType.SWORDS))
                .isEqualTo(ToolType.SWORD);
        assertThat(skillTools.getPrimarySkillToolType(PrimarySkillType.EXCAVATION))
                .isEqualTo(ToolType.SHOVEL);
        assertThat(skillTools.getPrimarySkillToolType(PrimarySkillType.HERBALISM))
                .isEqualTo(ToolType.HOE);
        assertThat(skillTools.getPrimarySkillToolType(PrimarySkillType.MINING))
                .isEqualTo(ToolType.PICKAXE);

        // And any skill not explicitly mapped should currently return null
        assertThat(skillTools.getPrimarySkillToolType(PrimarySkillType.FISHING)).isNull();
        assertThat(skillTools.getPrimarySkillToolType(PrimarySkillType.TAMING)).isNull();
    }

    // ------------------------------------------------------------------------
    // Combat / Gathering / Misc groupings by Minecraft version
    // ------------------------------------------------------------------------

    @Test
    void combatGatheringMiscGroupingsShouldMatchDefinitionForModernSpearsAndMacesVersion()
            throws Exception {
        SkillTools skillTools = newSkillToolsForVersion(1, 21, 11);

        assertThat(skillTools.getCombatSkills())
                .containsExactly(
                        PrimarySkillType.ARCHERY,
                        PrimarySkillType.AXES,
                        PrimarySkillType.CROSSBOWS,
                        PrimarySkillType.MACES,
                        PrimarySkillType.SWORDS,
                        PrimarySkillType.SPEARS,
                        PrimarySkillType.TAMING,
                        PrimarySkillType.TRIDENTS,
                        PrimarySkillType.UNARMED
                );

        assertThat(skillTools.getGatheringSkills())
                .containsExactly(
                        PrimarySkillType.EXCAVATION,
                        PrimarySkillType.FISHING,
                        PrimarySkillType.HERBALISM,
                        PrimarySkillType.MINING,
                        PrimarySkillType.WOODCUTTING
                );

        assertThat(skillTools.getMiscSkills())
                .containsExactly(
                        PrimarySkillType.ACROBATICS,
                        PrimarySkillType.ALCHEMY,
                        PrimarySkillType.REPAIR,
                        PrimarySkillType.SALVAGE,
                        PrimarySkillType.SMELTING
                );
    }

    @Test
    void combatSkillsShouldMatchDefinitionForVersionWithMacesButWithoutSpears() throws Exception {
        SkillTools skillTools = newSkillToolsForVersion(1, 21, 0);

        assertThat(skillTools.getCombatSkills())
                .containsExactly(
                        PrimarySkillType.ARCHERY,
                        PrimarySkillType.AXES,
                        PrimarySkillType.CROSSBOWS,
                        PrimarySkillType.MACES,
                        PrimarySkillType.SWORDS,
                        PrimarySkillType.TAMING,
                        PrimarySkillType.TRIDENTS,
                        PrimarySkillType.UNARMED
                );
    }

    @Test
    void combatSkillsShouldMatchDefinitionForVersionWithoutMacesOrSpears() throws Exception {
        SkillTools skillTools = newSkillToolsForVersion(1, 20, 4);

        assertThat(skillTools.getCombatSkills())
                .containsExactly(
                        PrimarySkillType.ARCHERY,
                        PrimarySkillType.AXES,
                        PrimarySkillType.CROSSBOWS,
                        PrimarySkillType.SWORDS,
                        PrimarySkillType.TAMING,
                        PrimarySkillType.TRIDENTS,
                        PrimarySkillType.UNARMED
                );
    }

    // ------------------------------------------------------------------------
    // LOCALIZED_SKILL_NAMES basic sanity (size + uniqueness, not content)
    // ------------------------------------------------------------------------

    @Test
    void localizedSkillNamesShouldContainOneEntryPerPrimarySkillAndBeSorted() throws Exception {
        SkillTools skillTools = newSkillToolsForVersion(1, 21, 11);

        List<String> names = new ArrayList<>(skillTools.LOCALIZED_SKILL_NAMES);

        // One per PrimarySkillType
        assertThat(names).hasSize(PrimarySkillType.values().length);

        // No duplicates
        assertThat(new HashSet<>(names)).hasSize(names.size());

        // Sorted ascending
        List<String> sorted = new ArrayList<>(names);
        Collections.sort(sorted);
        assertThat(names).isEqualTo(sorted);
    }
}
