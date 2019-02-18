//package com.gmail.nossr50.config.mods;
//
//public class BlockConfigManager {
//    //TODO: Commented out until modded servers appear again
//    /*public BlockConfigManager() {
//        Pattern middlePattern = Pattern.compile("blocks\\.(?:.+)\\.yml");
//        Pattern startPattern = Pattern.compile("(?:.+)\\.blocks\\.yml");
//        //File dataFolder = new File(McmmoCore.getModDataFolderPath());
//        File dataFolder = new File(mcMMO.getModDirectory());
//        File vanilla = new File(dataFolder, "blocks.default.yml");
//        ModManager modManager = mcMMO.getModManager();
//
//        if (!vanilla.exists()) {
//            mcMMO.p.saveResource(vanilla.getParentFile().getName() + File.separator + "blocks.default.yml", false);
//        }
//
//        for (String fileName : dataFolder.list()) {
//            if (!middlePattern.matcher(fileName).matches() && !startPattern.matcher(fileName).matches()) {
//                continue;
//            }
//
//            File file = new File(dataFolder, fileName);
//
//            if (file.isDirectory()) {
//                continue;
//            }
//
//            modManager.registerCustomBlocks(new CustomBlockConfig(fileName));
//        }
//    }*/
//}
