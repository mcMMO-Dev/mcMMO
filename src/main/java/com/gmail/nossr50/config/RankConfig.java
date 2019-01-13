package com.gmail.nossr50.config;

public class RankConfig extends AutoUpdateConfigLoader {
    private static RankConfig instance;

    public RankConfig()
    {
        super("skillranks.yml");
        validate();
        this.instance = this;
    }

    @Override
    protected void loadKeys() {

    }

    public static RankConfig getInstance()
    {
        if(instance == null)
            return new RankConfig();

        return instance;
    }
}
