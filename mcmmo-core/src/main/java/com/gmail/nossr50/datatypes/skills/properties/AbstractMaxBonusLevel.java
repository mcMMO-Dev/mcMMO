package com.gmail.nossr50.datatypes.skills.properties;

public class AbstractMaxBonusLevel implements MaxBonusLevel {

    private int retro;
    private int standard;

    public AbstractMaxBonusLevel(int standard, int retro) {
        this.standard = standard;
        this.retro = retro;
    }

    public AbstractMaxBonusLevel(int standard) {
        this.standard = standard;
        this.retro = standard * 10;
    }

    @Override
    public int getRetroScaleValue() {
        return retro;
    }

    @Override
    public int getStandardScaleValue() {
        return standard;
    }

    public void setRetro(int retro) {
        this.retro = retro;
    }

    public void setStandard(int standard) {
        this.standard = standard;
    }
}
