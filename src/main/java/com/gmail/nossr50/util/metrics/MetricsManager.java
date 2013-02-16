package com.gmail.nossr50.util.metrics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.HiddenConfig;
import com.gmail.nossr50.locale.LocaleLoader;

import com.turt2live.metrics.EMetrics;
import com.turt2live.metrics.Metrics;
import com.turt2live.metrics.Metrics.Graph;
import com.turt2live.metrics.tracker.Tracker;
import com.turt2live.metrics.data.*;

public class MetricsManager {
    private static boolean setup = false;

    private static Tracker chimeraUseTracker;
    private static Tracker chimeraServerUseTracker;

    private static DataTracker tracker;
    private static EMetrics emetrics;

    public static void setup() {
        if (setup) {
            return;
        }

        if (Config.getInstance().getStatsTrackingEnabled()) {
            try {
                emetrics = new EMetrics(mcMMO.p);
                Metrics metrics = emetrics.getMetrics();

                // Timings Graph
                Graph timingsGraph = metrics.createGraph("Percentage of servers using timings");

                if (mcMMO.p.getServer().getPluginManager().useTimings()) {
                    timingsGraph.addPlotter(new Metrics.Plotter("Enabled") {
                        @Override
                        public int getValue() {
                            return 1;
                        }
                    });
                }
                else {
                    timingsGraph.addPlotter(new Metrics.Plotter("Disabled") {
                        @Override
                        public int getValue() {
                            return 1;
                        }
                    });
                }

                // Donut Version Graph
                Graph versionDonutGraph = metrics.createGraph("Donut Version");

                boolean haveVersionInformation = false;
                boolean isOfficialBuild = false;
                String officialKey = "e14cfacdd442a953343ebd8529138680";

                String version = mcMMO.p.getDescription().getVersion();

                InputStreamReader isr = new InputStreamReader(mcMMO.p.getResource(".jenkins"));
                BufferedReader br = new BufferedReader(isr);
                char[] key = new char[32];
                br.read(key);
                if (officialKey.equals(String.valueOf(key))) {
                    isOfficialBuild = true;
                }

                if (version.contains("-")) {
                    String majorVersion = version.substring(0, version.indexOf("-"));
                    String subVersion = "";
                    if (isOfficialBuild) {
                        int startIndex = version.indexOf("-");
                        if (version.substring(startIndex + 1).contains("-")) {
                            subVersion = version.substring(startIndex, version.indexOf("-", startIndex + 1));
                        } else {
                            subVersion = "-release";
                        }
                    } else {
                        subVersion = "-custom";
                    }

                    version = majorVersion + "~=~" + subVersion;
                    haveVersionInformation = true;
                } else {
                    haveVersionInformation = false;
                }

                if (haveVersionInformation) {
                    versionDonutGraph.addPlotter(new Metrics.Plotter(version) {
                        @Override
                        public int getValue() {
                            return 1;
                        }
                    });
                }

                // Official v Custom build Graph
                Graph officialGraph = metrics.createGraph("Built by official ci");

                if (isOfficialBuild) {
                    officialGraph.addPlotter(new Metrics.Plotter("Yes") {
                        @Override
                        public int getValue() {
                            return 1;
                        }
                    });
                }
                else {
                    officialGraph.addPlotter(new Metrics.Plotter("No") {
                        @Override
                        public int getValue() {
                            return 1;
                        }
                    });
                }

                // Chunkmeta enabled Graph
                Graph chunkmetaGraph = metrics.createGraph("Uses Chunkmeta");

                if (HiddenConfig.getInstance().getChunkletsEnabled()) {
                    chunkmetaGraph.addPlotter(new Metrics.Plotter("Yes") {
                        @Override
                        public int getValue() {
                            return 1;
                        }
                    });
                }
                else {
                    chunkmetaGraph.addPlotter(new Metrics.Plotter("No") {
                        @Override
                        public int getValue() {
                            return 1;
                        }
                    });
                }

                // Storage method Graph
                Graph storageGraph = metrics.createGraph("Storage method");

                if (Config.getInstance().getUseMySQL()) {
                    storageGraph.addPlotter(new Metrics.Plotter("SQL") {
                        @Override
                        public int getValue() {
                            return 1;
                        }
                    });
                }
                else {
                    storageGraph.addPlotter(new Metrics.Plotter("Flatfile") {
                        @Override
                        public int getValue() {
                            return 1;
                        }
                    });
                }

                // Locale Graph
                Graph localeGraph = metrics.createGraph("Locale");

                localeGraph.addPlotter(new Metrics.Plotter(LocaleLoader.getCurrentLocale().getDisplayLanguage(Locale.US)) {
                    @Override
                    public int getValue() {
                        return 1;
                    }
                });

                // GlobalMultiplier Graph
                Graph globalMultiplierGraph = metrics.createGraph("Global Multiplier Graph");

                globalMultiplierGraph.addPlotter(new Metrics.Plotter(Config.getInstance().getExperienceGainsGlobalMultiplier() + "") {
                    @Override
                    public int getValue() {
                        return 1;
                    }
                });

                // GlobalCurveModifier Graph
                Graph globalCurveModifierGraph = metrics.createGraph("Global Curve Modifier Graph");

                globalCurveModifierGraph.addPlotter(new Metrics.Plotter(Config.getInstance().getFormulaMultiplierCurve() + "") {
                    @Override
                    public int getValue() {
                        return 1;
                    }
                });

                // Chimera Wing Usage Trackers
                final String chimeraGraphName = "Chimera Wing Usage";

                chimeraUseTracker = EMetrics.createBasicTracker(chimeraGraphName, "Player use");
                chimeraServerUseTracker = EMetrics.createEnabledTracker(chimeraGraphName, "Server use");

                emetrics.addTracker(chimeraUseTracker);
                emetrics.addTracker(chimeraServerUseTracker);

                // Chimera Wing Enabled Graph
                Graph chimeraGraph = metrics.createGraph("Chimera Wing");

                if (Config.getInstance().getChimaeraEnabled()) {
                    chimeraGraph.addPlotter(new Metrics.Plotter("Enabled") {
                        @Override
                        public int getValue() {
                            return 1;
                        }
                    });
                }
                else {
                    chimeraGraph.addPlotter(new Metrics.Plotter("Disabled") {
                        @Override
                        public int getValue() {
                            return 1;
                        }
                    });
                }

                tracker = emetrics.getDataTracker();
                tracker.enable();
                tracker.setFilter(new DataEvent.DataType [] { DataEvent.DataType.SEND_DATA });

                emetrics.startMetrics();
            }
            catch (IOException e) {
                mcMMO.p.getLogger().warning("Failed to submit stats.");
            }
        }
    }

    public static void chimeraWingUsed() {
        chimeraUseTracker.increment();
        chimeraServerUseTracker.increment();

        debug();
    }

    private static void debug() {
        emetrics.getMetrics().flush();

        for (DataEvent event : tracker.getEvents()) {
            String graphName = event.getGraphName();
            String colName = event.getTrackerName();
            int value = event.getValueSent();

            System.out.println("Graph: " + graphName + ", Column: " + colName + ", Value: " + value);
        }
    }
}
