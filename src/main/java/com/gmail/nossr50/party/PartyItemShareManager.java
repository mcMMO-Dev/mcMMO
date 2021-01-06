//package com.gmail.nossr50.party;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class PartyItemShareManager {
//
//    private ShareMode itemShareMode = ShareMode.NONE;
//
//    private boolean shareLootDrops        = true;
//    private boolean shareMiningDrops      = true;
//    private boolean shareHerbalismDrops   = true;
//    private boolean shareWoodcuttingDrops = true;
//    private boolean shareMiscDrops        = true;
//
//    public void setItemShareMode(ShareMode itemShareMode) {
//        this.itemShareMode = itemShareMode;
//    }
//
//    public ShareMode getItemShareMode() {
//        return itemShareMode;
//    }
//
//    public boolean sharingDrops(ItemShareType shareType) {
//        switch (shareType) {
//            case HERBALISM:
//                return shareHerbalismDrops;
//
//            case LOOT:
//                return shareLootDrops;
//
//            case MINING:
//                return shareMiningDrops;
//
//            case MISC:
//                return shareMiscDrops;
//
//            case WOODCUTTING:
//                return shareWoodcuttingDrops;
//
//            default:
//                return false;
//        }
//    }
//
//    public void setSharingDrops(ItemShareType shareType, boolean enabled) {
//        switch (shareType) {
//            case HERBALISM:
//                shareHerbalismDrops = enabled;
//                break;
//
//            case LOOT:
//                shareLootDrops = enabled;
//                break;
//
//            case MINING:
//                shareMiningDrops = enabled;
//                break;
//
//            case MISC:
//                shareMiscDrops = enabled;
//                break;
//
//            case WOODCUTTING:
//                shareWoodcuttingDrops = enabled;
//                break;
//
//            default:
//        }
//    }
//
//    public List<String> getItemShareCategories() {
//        List<String> shareCategories = new ArrayList<>();
//
//        for (ItemShareType shareType : ItemShareType.values()) {
//            if (sharingDrops(shareType)) {
//                shareCategories.add(shareType.getLocaleString());
//            }
//        }
//
//        return shareCategories;
//    }
//
//
//}
