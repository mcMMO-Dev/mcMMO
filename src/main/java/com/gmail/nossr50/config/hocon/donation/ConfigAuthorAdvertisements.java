package com.gmail.nossr50.config.hocon.donation;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigAuthorAdvertisements {

    public static final boolean SHOW_DONATION_DEFAULT = true;
    public static final boolean SHOW_PATREON_DEFAULT = true;
    public static final boolean SHOW_WEBSITE_LINKS_DEFAULT = true;

    @Setting(value = "Show-Donation-Info",
            comment = "Shows donation info in various mcMMO commands." +
                    "\nSuch as a paypal link for the author on the /mcmmo command" +
                    "\nSupport through donations helps keep mcMMO development going." +
                    "\nDefault value: "+ SHOW_DONATION_DEFAULT)
    private boolean showDonationInfo = SHOW_DONATION_DEFAULT;

    @Setting(value = "Show-Patreon-Links",
            comment = "Shows patreon links for the mcMMO author in various mcMMO commands." +
                    "\nSupport through Patreon helps keep mcMMO development going" +
                    "\nDefault value: "+SHOW_PATREON_DEFAULT)
    private boolean showPatreonInfo = SHOW_PATREON_DEFAULT;

    @Setting(value = "Show-Website-Links", comment = "Allows links to various affiliated websites for mcMMO." +
            "\nNOTE: mcMMO loses some functionality related to skill info pages with this turned off." +
            "\nThis includes..." +
            "\nThe mcMMO Official Website" +
            "\nThe mcMMO Wiki and links to specific skill pages" +
            "\nThe mcMMO authors Patreon" +
            "\nThe Official mcMMO Spigot Listing" +
            "\nThe Official translation website for mcMMO" +
            "\nDefault value: "+SHOW_WEBSITE_LINKS_DEFAULT)
    private boolean showWebsiteLinks = SHOW_WEBSITE_LINKS_DEFAULT;

    public boolean isShowDonationInfo() {
        return showDonationInfo;
    }

    public boolean isShowPatreonInfo() {
        return showPatreonInfo;
    }

    public boolean isShowWebsiteLinks() {
        return showWebsiteLinks;
    }
}
