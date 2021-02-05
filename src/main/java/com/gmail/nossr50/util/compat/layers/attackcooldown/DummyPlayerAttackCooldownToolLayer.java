//package com.gmail.nossr50.util.compat.layers.attackcooldown;
//
//import com.gmail.nossr50.util.nms.NMSVersion;
//import org.bukkit.entity.Player;
//import org.jetbrains.annotations.NotNull;
//
//import java.lang.reflect.InvocationTargetException;
//
//public class DummyPlayerAttackCooldownToolLayer extends PlayerAttackCooldownToolLayer {
//    public DummyPlayerAttackCooldownToolLayer() {
//        super(NMSVersion.UNSUPPORTED);
//    }
//
//    @Override
//    public boolean initializeLayer() {
//        return noErrorsOnInitialize;
//    }
//
//    @Override
//    public float getAttackStrength(@NotNull Player player) throws InvocationTargetException, IllegalAccessException {
//        return 1.0F; //Always full strength
//    }
//
//    @Override
//    public float getCooldownValue(@NotNull Player player) throws InvocationTargetException, IllegalAccessException {
//        return 0F;
//    }
//
//    @Override
//    public void resetAttackStrength(@NotNull Player player) throws InvocationTargetException, IllegalAccessException {
//        //Do nothing
//    }
//
//    @Override
//    public int getCooldownFieldValue(@NotNull Player player) throws InvocationTargetException, IllegalAccessException {
//        return 0;
//    }
//
//    @Override
//    public void setCooldownFieldValue(@NotNull Player player, int fieldValue) throws InvocationTargetException, IllegalAccessException {
//    }
//}
