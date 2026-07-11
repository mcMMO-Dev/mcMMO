package com.gmail.nossr50.skills.alchemy;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.config.skills.alchemy.PotionConfig;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.alchemy.PotionStage;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.skills.RankUtils;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

/**
 * Covers the Alchemy manager: the Catalysis brew speed curve (unlock gate, linear scaling,
 * max clamp, lucky perk) and the Concoctions tier plumbing that feeds ingredient lists and
 * brew XP payouts.
 */
class AlchemyManagerTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(AlchemyManagerTest.class.getName());

    private static final int CATALYSIS_UNLOCK_LEVEL = 100;
    private static final int CATALYSIS_MAX_BONUS_LEVEL = 1000;
    private static final double CATALYSIS_MIN_SPEED = 1.0;
    private static final double CATALYSIS_MAX_SPEED = 5.0;
    private static final double LUCKY_MODIFIER = 4.0 / 3.0;

    private AlchemyManager alchemyManager;
    private PotionConfig potionConfig;

    private int originalMaxBonusLevel;
    private double originalMinSpeed;
    private double originalMaxSpeed;

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(logger);

        potionConfig = mock(PotionConfig.class);
        when(mcMMO.p.getPotionConfig()).thenReturn(potionConfig);

        originalMaxBonusLevel = Alchemy.catalysisMaxBonusLevel;
        originalMinSpeed = Alchemy.catalysisMinSpeed;
        originalMaxSpeed = Alchemy.catalysisMaxSpeed;
        Alchemy.catalysisMaxBonusLevel = CATALYSIS_MAX_BONUS_LEVEL;
        Alchemy.catalysisMinSpeed = CATALYSIS_MIN_SPEED;
        Alchemy.catalysisMaxSpeed = CATALYSIS_MAX_SPEED;

        when(RankUtils.getUnlockLevel(SubSkillType.ALCHEMY_CATALYSIS))
                .thenReturn(CATALYSIS_UNLOCK_LEVEL);

        alchemyManager = Mockito.spy(new AlchemyManager(mmoPlayer));
    }

    @AfterEach
    void tearDown() {
        Alchemy.catalysisMaxBonusLevel = originalMaxBonusLevel;
        Alchemy.catalysisMinSpeed = originalMinSpeed;
        Alchemy.catalysisMaxSpeed = originalMaxSpeed;
        cleanUpStaticMocks();
    }

    private void setAlchemyLevel(int skillLevel) {
        doReturn(skillLevel).when(mmoPlayer).getSkillLevel(PrimarySkillType.ALCHEMY);
    }

    private ItemStack mockIngredient(Material material) {
        final ItemStack ingredient = mock(ItemStack.class);
        when(ingredient.getType()).thenReturn(material);
        return ingredient;
    }

    @Nested
    class CalculateBrewSpeed {
        /**
         * The Catalysis curve: minimum speed until the subskill unlocks, then a linear ramp
         * between the unlock level and the configured max bonus level, clamped at max speed.
         */
        @ParameterizedTest
        @CsvSource({
                "0, 1.0",
                "99, 1.0",
                "100, 1.0",
                "550, 3.0",
                "1000, 5.0",
                "5000, 5.0",
        })
        void brewSpeedShouldFollowTheCatalysisCurve(int skillLevel, double expectedSpeed) {
            // Given - a player at a given Alchemy level
            setAlchemyLevel(skillLevel);

            // When - the brew speed is computed without the lucky perk
            final double brewSpeed = alchemyManager.calculateBrewSpeed(false);

            // Then - the speed matches the curve
            assertThat(brewSpeed).isCloseTo(expectedSpeed, within(1e-9));
        }

        @Test
        void luckyPerkShouldMultiplySpeedByFourThirds() {
            // Given - a player halfway up the Catalysis curve
            setAlchemyLevel(550);

            // When - the brew speed is computed with the lucky perk
            final double brewSpeed = alchemyManager.calculateBrewSpeed(true);

            // Then - the curve speed is multiplied by the lucky modifier
            assertThat(brewSpeed).isCloseTo(3.0 * LUCKY_MODIFIER, within(1e-9));
        }

        @Test
        void luckyPerkShouldApplyOnTopOfTheMaxSpeedClamp() {
            // Given - a player past the max bonus level
            setAlchemyLevel(5000);

            // When - the brew speed is computed with the lucky perk
            final double brewSpeed = alchemyManager.calculateBrewSpeed(true);

            // Then - the lucky modifier multiplies the clamped max, exceeding it
            assertThat(brewSpeed).isCloseTo(CATALYSIS_MAX_SPEED * LUCKY_MODIFIER, within(1e-9));
        }

        @Test
        void luckyPerkShouldNotHelpBeforeCatalysisUnlocks() {
            // Given - a player below the Catalysis unlock level
            setAlchemyLevel(50);

            // When - the brew speed is computed with the lucky perk
            final double brewSpeed = alchemyManager.calculateBrewSpeed(true);

            // Then - the pre-unlock minimum is not lucky-boosted
            assertThat(brewSpeed).isCloseTo(CATALYSIS_MIN_SPEED, within(1e-9));
        }
    }

    @Nested
    class ConcoctionsTier {
        @Test
        void tierShouldMirrorTheConcoctionsRank() {
            // Given - a player at Concoctions rank 3
            when(RankUtils.getRank(player, SubSkillType.ALCHEMY_CONCOCTIONS)).thenReturn(3);

            // When - the tier is read
            // Then - it is the Concoctions rank
            assertThat(alchemyManager.getTier()).isEqualTo(3);
        }

        @Test
        void ingredientListShouldJoinConfigNamesWithCommas() {
            // Given - a tier with two configured ingredients
            when(RankUtils.getRank(player, SubSkillType.ALCHEMY_CONCOCTIONS)).thenReturn(2);
            final ItemStack netherWart = mockIngredient(Material.NETHER_WART);
            final ItemStack blazePowder = mockIngredient(Material.BLAZE_POWDER);
            when(potionConfig.getIngredients(2)).thenReturn(List.of(netherWart, blazePowder));

            // When - the display list is built
            // Then - the config-style names are comma-joined without a leading separator
            assertThat(alchemyManager.getIngredientList())
                    .isEqualTo("Nether_Wart, Blaze_Powder");
        }

        @Test
        void singleIngredientListShouldHaveNoSeparator() {
            // Given - a tier with one configured ingredient
            when(RankUtils.getRank(player, SubSkillType.ALCHEMY_CONCOCTIONS)).thenReturn(1);
            final ItemStack netherWart = mockIngredient(Material.NETHER_WART);
            when(potionConfig.getIngredients(1)).thenReturn(List.of(netherWart));

            // When - the display list is built
            // Then - the single name stands alone
            assertThat(alchemyManager.getIngredientList()).isEqualTo("Nether_Wart");
        }
    }

    @Test
    void brewSuccessShouldPayStageXpPerPotion() {
        // Given - a stage worth 15 XP and a brew that produced three potions
        when(ExperienceConfig.getInstance().getPotionXP(PotionStage.THREE)).thenReturn(15.0);
        doNothing().when(alchemyManager).applyXpGain(anyFloat(), any(), any());

        // When - the brew successes are handled
        alchemyManager.handlePotionBrewSuccesses(PotionStage.THREE, 3);

        // Then - the XP payout scales with the amount and is passive PVE XP
        verify(alchemyManager).applyXpGain(45.0f, XPGainReason.PVE, XPGainSource.PASSIVE);
    }
}
