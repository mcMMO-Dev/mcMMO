/*
 * Copyright 2011 Tyler Blair. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and contributors and should not be interpreted as representing official policies,
 * either expressed or implied, of anybody else.
 */
package com.gmail.nossr50;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Tooling to post to metrics.griefcraft.com
 */
public class Metrics {

    /**
     * Interface used to collect custom data for a plugin
     */
    public static abstract class Plotter {

        /**
         * Get the column name for the plotted point
         *
         * @return the plotted point's column name
         */
        public abstract String getColumnName();

        /**
         * Get the current value for the plotted point
         *
         * @return
         */
        public abstract int getValue();

        /**
         * Called after the website graphs have been updated
         */
        public void reset() {
        }

        @Override
        public int hashCode() {
            return getColumnName().hashCode() + getValue();
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof Plotter)) {
                return false;
            }

            Plotter plotter = (Plotter) object;
            return plotter.getColumnName().equals(getColumnName()) && plotter.getValue() == getValue();
        }

    }

    /**
     * The metrics revision number
     */
    private final static int REVISION = 4;

    /**
     * The base url of the metrics domain
     */
    private static final String BASE_URL = "http://metrics.griefcraft.com";

    /**
     * The url used to report a server's status
     */
    private static final String REPORT_URL = "/report/%s";

    /**
     * The file where guid and opt out is stored in
     */
    private static final String CONFIG_FILE = "plugins/PluginMetrics/config.yml";

    /**
     * Interval of time to ping in minutes
     */
    private final static int PING_INTERVAL = 10;

    /**
     * A map of the custom data plotters for plugins
     */
    private Map<Plugin, Set<Plotter>> customData = Collections.synchronizedMap(new HashMap<Plugin, Set<Plotter>>());

    /**
     * The plugin configuration file
     */
    private final YamlConfiguration configuration;

    /**
     * Unique server id
     */
    private String guid;

    public Metrics() throws IOException {
        // load the config
        File file = new File(CONFIG_FILE);
        configuration = YamlConfiguration.loadConfiguration(file);

        // add some defaults
        configuration.addDefault("opt-out", false);
        configuration.addDefault("guid", UUID.randomUUID().toString());

        // Do we need to create the file?
        if (configuration.get("guid", null) == null) {
            configuration.options().header("http://metrics.griefcraft.com").copyDefaults(true);
            configuration.save(file);
        }

        // Load the guid then
        guid = configuration.getString("guid");
    }

    /**
     * Adds a custom data plotter for a given plugin
     *
     * @param plugin
     * @param plotter
     */
    public void addCustomData(Plugin plugin, Plotter plotter) {
        Set<Plotter> plotters = customData.get(plugin);

        if (plotters == null) {
            plotters = Collections.synchronizedSet(new LinkedHashSet<Plotter>());
            customData.put(plugin, plotters);
        }

        plotters.add(plotter);
    }

    /**
     * Begin measuring a plugin
     *
     * @param plugin
     */
    public void beginMeasuringPlugin(final Plugin plugin) throws IOException {
        // Did we opt out?
        if (configuration.getBoolean("opt-out", false)) {
            return;
        }

        // First tell the server about us
        postPlugin(plugin, false);

        // Ping the server in intervals
        plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {
            public void run() {
                try {
                    postPlugin(plugin, true);
                } catch (IOException e) {
                    System.out.println("[Metrics] " + e.getMessage());
                }
            }
        }, PING_INTERVAL * 1200, PING_INTERVAL * 1200);
    }

    /**
     * Generic method that posts a plugin to the metrics website
     *
     * @param plugin
     */
    private void postPlugin(Plugin plugin, boolean isPing) throws IOException {
        // Construct the post data
        String response = "ERR No response";
        String data = encode("guid") + '=' + encode(guid)
                + '&' + encode("version") + '=' + encode(plugin.getDescription().getVersion())
                + '&' + encode("server") + '=' + encode(Bukkit.getVersion())
                + '&' + encode("players") + '=' + encode(String.valueOf(Bukkit.getServer().getOnlinePlayers().length))
                + '&' + encode("revision") + '=' + encode(REVISION + "");

        // If we're pinging, append it
        if (isPing) {
            data += '&' + encode("ping") + '=' + encode("true");
        }

        // Add any custom data (if applicable)
        Set<Plotter> plotters = customData.get(plugin);

        if (plotters != null) {
            for (Plotter plotter : plotters) {
                data += "&" + encode("Custom" + plotter.getColumnName())
                        + "=" + encode(Integer.toString(plotter.getValue()));
            }
        }

        // Create the url
        URL url = new URL(BASE_URL + String.format(REPORT_URL, plugin.getDescription().getName()));

        // Connect to the website
        URLConnection connection;

        // Mineshafter creates a socks proxy, so we can safely bypass it
        if (isMineshafterPresent()) {
            connection = url.openConnection(Proxy.NO_PROXY);
        } else {
            connection = url.openConnection();
        }

        connection.setDoOutput(true);

        // Write the data
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(data);
        writer.flush();

        // Now read the response
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        response = reader.readLine();

        // close resources
        writer.close();
        reader.close();

        if (response.startsWith("ERR")) {
            throw new IOException(response); //Throw the exception
        } else {
            // Is this the first update this hour?
            if (response.contains("OK This is your first update this hour")) {
                if (plotters != null) {
                    for (Plotter plotter : plotters) {
                        plotter.reset();
                    }
                }
            }
        }
        //if (response.startsWith("OK")) - We should get "OK" followed by an optional description if everything goes right
    }

    /**
     * Check if mineshafter is present. If it is, we need to bypass it to send POST requests
     *
     * @return
     */
    private boolean isMineshafterPresent() {
        try {
            Class.forName("mineshafter.MineServer");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Encode text as UTF-8
     *
     * @param text
     * @return
     */
    private static String encode(String text) throws UnsupportedEncodingException {
        return URLEncoder.encode(text, "UTF-8");
    }

}