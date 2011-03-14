
package com.gmail.nossr50.vPlayersOnline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Mark Tolley
 */
class Config
{
    public static String name;
    private static final String CONFIG_FILE = "plugins/vPlayersOnline/vplayersonline.properties";

    public static Properties loadConfig() {
        Properties config = defaultConfig();

        try {
            config.load(new FileReader(CONFIG_FILE));
        }
        catch (FileNotFoundException e) {
            System.out.println(name + ": Creating configuration file...");
            config = defaultConfig();
            writeConfig();
        }
        catch (IOException e) {
            System.out.println(name + "s: An error occured reading configuration, using defaults.");
            config = defaultConfig();
        }

        return config;
    }

    private static void writeConfig() {
        File f = new File(CONFIG_FILE);
        if (f.getParentFile().mkdirs()) {
            try {
                FileWriter fw = new FileWriter(f);
                fw.write("# vPlayersOnline configuration file\r\n");
                fw.write("# \r\n");
                fw.write("# Color codes:\r\n");
                fw.write("# &0 black\r\n");
                fw.write("# &1 dark blue      &9 blue\r\n");
                fw.write("# &2 dark green     &a green\r\n");
                fw.write("# &3 dark aqua      &b aqua\r\n");
                fw.write("# &4 dark red       &c red\r\n");
                fw.write("# &5 dark pink      &d pink\r\n");
                fw.write("# &6 dark yellow    &e yellow\r\n");
                fw.write("# &7 light grey     &f white\r\n");
                fw.write("# &8 dark grey\r\n");
                fw.write("\r\n");
                fw.write("PlayersOnline = &aThere are %d players online\r\n");
                fw.write("PlayerList    = &cPlayer List &f(%s)\r\n");
                fw.write("TotalPlayers  = &cTotal Players: &a%d\r\n");
                fw.write("#1POnline      = &aThere is 1 player online.\r\n");
                fw.write("1POnline      = &cNo one else is online.\r\n");
                fw.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static Properties defaultConfig() {
        Properties config = new Properties();
        config.setProperty("PlayersOnline", "&aThere are %d players online");
        config.setProperty("PlayerList", "&cPlayer List &f(%s)");
        config.setProperty("TotalPlayers", "&cTotal Players: &a%d");
        config.setProperty("1POnline", "&cNo one else is online.");
        return config;
    }
}
