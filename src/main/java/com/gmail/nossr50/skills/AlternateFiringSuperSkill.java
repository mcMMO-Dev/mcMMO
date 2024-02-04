package com.gmail.nossr50.skills;

public interface AlternateFiringSuperSkill {
    int chargeSuper();

    void fireSuper();

    void resetCharge();

    boolean isReadyToFire();

    long lastChargeTime();
}
