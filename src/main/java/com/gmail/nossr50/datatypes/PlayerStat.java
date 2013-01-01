package com.gmail.nossr50.datatypes;

public class PlayerStat {
    public String name;
    public int statVal = 0;

    public PlayerStat() {};

    public PlayerStat(String name, int value) {
        this.name = name;
        this.statVal = value;
    }
}