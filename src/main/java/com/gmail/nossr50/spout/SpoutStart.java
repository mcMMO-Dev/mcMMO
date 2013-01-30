package com.gmail.nossr50.spout;

import com.gmail.nossr50.mcMMO;

public class SpoutStart implements Runnable{

    @Override
    public void run() {
        //Spout Stuff
        if (mcMMO.spoutEnabled) {
            SpoutConfig.getInstance();
            SpoutStuff.setupSpoutConfigs();
            SpoutStuff.registerCustomEvent();

            //Handle spout players after a /reload
            SpoutStuff.reloadSpoutPlayers();
        }
    }
}
