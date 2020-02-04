package com.gmail.nossr50.mcmmo.api.platform.util;


import com.gmail.nossr50.mcmmo.api.data.MMOEntity;
import com.gmail.nossr50.mcmmo.api.data.MMOPlayer;

@Deprecated // Not really deprecated, just /really/ needs a do-over...
public interface MobHealthBarManager<PT, ET> {
    /**
     * Fix issues with death messages caused by the mob healthbars.
     *
     * @param deathMessage The original death message
     * @param player       The player who died
     * @return the fixed death message
     */
    public String fixDeathMessage(String deathMessage, MMOPlayer<PT> player);

    /**
     * Handle the creation of mob healthbars.
     *
     * @param target the targetted entity
     * @param damage damage done by the attack triggering this
     */
    public void handleMobHealthbars(MMOEntity<ET> target, double damage);


}
