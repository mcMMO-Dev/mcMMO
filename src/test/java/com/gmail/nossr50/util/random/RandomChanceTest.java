//package com.gmail.nossr50.util.random;
//
//import com.gmail.nossr50.datatypes.player.McMMOPlayer;
//import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
//import com.gmail.nossr50.datatypes.skills.SubSkillType;
//import com.gmail.nossr50.util.Permissions;
//import com.gmail.nossr50.util.player.UserManager;
//import org.bukkit.entity.Player;
//import org.jetbrains.annotations.NotNull;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mockito;
//import org.powermock.api.mockito.PowerMockito;
//import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.modules.junit4.PowerMockRunner;
//
//import static org.mockito.Mockito.mock;
//
////TODO: Rewrite the entire com.gmail.nossr50.util.random package, it was written in haste and it disgusts me
////TODO: Add more tests for the other types of random dice rolls
//@RunWith(PowerMockRunner.class)
//@PrepareForTest({RandomChanceUtil.class, UserManager.class})
//public class RandomChanceTest {
//
//    private Player luckyPlayer;
//    private McMMOPlayer mmoPlayerLucky;
//
//    private Player normalPlayer;
//    private McMMOPlayer mmoPlayerNormal;
//
//    private SubSkillType subSkillType;
//    private PrimarySkillType primarySkillType;
//
//    private final String testASCIIHeader = "---- mcMMO Tests ----";
//
//    @Before
//    public void setUpMock() {
//        primarySkillType = PrimarySkillType.HERBALISM;
//        subSkillType = SubSkillType.HERBALISM_GREEN_THUMB;
//
//        //TODO: Likely needs to be changed per skill if more tests were added
//        PowerMockito.stub(PowerMockito.method(RandomChanceUtil.class, "getMaximumProbability", subSkillType.getClass())).toReturn(100D);
//        PowerMockito.stub(PowerMockito.method(RandomChanceUtil.class, "getMaxBonusLevelCap", subSkillType.getClass())).toReturn(1000D);
//
//        normalPlayer = mock(Player.class);
//        luckyPlayer = mock(Player.class);
//
//        mmoPlayerNormal = mock(McMMOPlayer.class);
//        mmoPlayerLucky = mock(McMMOPlayer.class);
//
//        PowerMockito.mockStatic(UserManager.class);
//        Mockito.when(UserManager.getPlayer(normalPlayer)).thenReturn(mmoPlayerNormal);
//        Mockito.when(UserManager.getPlayer(luckyPlayer)).thenReturn(mmoPlayerLucky);
//
//        Mockito.when(mmoPlayerNormal.getPlayer()).thenReturn(normalPlayer);
//        Mockito.when(mmoPlayerLucky.getPlayer()).thenReturn(luckyPlayer);
//
//        //Lucky player has the lucky permission
//        //Normal player doesn't have any lucky permission
//        Mockito.when(Permissions.lucky(luckyPlayer, primarySkillType)).thenReturn(true);
//        Mockito.when(Permissions.lucky(normalPlayer, primarySkillType)).thenReturn(false);
//
//        Mockito.when(mmoPlayerNormal.getSkillLevel(primarySkillType)).thenReturn(800);
//        Mockito.when(mmoPlayerLucky.getSkillLevel(primarySkillType)).thenReturn(800);
//    }
//
//    @Test
//    public void testLuckyChance() {
//        System.out.println(testASCIIHeader);
//        System.out.println("Testing success odds to fall within expected values...");
//        assertEquals(80D, getSuccessChance(mmoPlayerNormal),0D);
//        assertEquals(80D * RandomChanceUtil.LUCKY_MODIFIER, getSuccessChance(mmoPlayerLucky),0D);
//    }
//
//    @Test
//    public void testNeverFailsSuccessLuckyPlayer() {
//        System.out.println(testASCIIHeader);
//        System.out.println("Test - Lucky Player with 80% base success should never fail (10,000 iterations)");
//        for(int x = 0; x < 10000; x++) {
//            Assert.assertTrue(RandomChanceUtil.checkRandomChanceExecutionSuccess(luckyPlayer, SubSkillType.HERBALISM_GREEN_THUMB, true));
//            if(x == 10000-1)
//                System.out.println("They never failed!");
//        }
//    }
//
//    @Test
//    public void testFailsAboutExpected() {
//        System.out.println(testASCIIHeader);
//        System.out.println("Test - Player with 800 skill should fail about 20% of the time (100,000 iterations)");
//        double ratioDivisor = 1000; //1000 because we run the test 100,000 times
//        double expectedFailRate = 20D;
//
//        double win = 0, loss = 0;
//        for(int x = 0; x < 100000; x++) {
//            if(RandomChanceUtil.checkRandomChanceExecutionSuccess(normalPlayer, SubSkillType.HERBALISM_GREEN_THUMB, true)) {
//                win++;
//            } else {
//                loss++;
//            }
//        }
//
//        double lossRatio = (loss / ratioDivisor);
//        Assert.assertEquals(lossRatio, expectedFailRate, 1D);
//    }
//
//    private double getSuccessChance(@NotNull McMMOPlayer mmoPlayer) {
//        RandomChanceSkill randomChanceSkill = new RandomChanceSkill(mmoPlayer.getPlayer(), subSkillType, true);
//        return RandomChanceUtil.calculateChanceOfSuccess(randomChanceSkill);
//    }
//
//    private void assertEquals(double expected, double actual, double delta) {
//        Assert.assertEquals(expected, actual, delta);
//    }
//}
