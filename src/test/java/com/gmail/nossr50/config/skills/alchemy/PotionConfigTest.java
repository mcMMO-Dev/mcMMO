//package com.gmail.nossr50.config.skills.alchemy;
//
//import com.gmail.nossr50.MMOTestEnvironment;
//import org.bukkit.inventory.meta.ItemMeta;
//import org.bukkit.inventory.meta.PotionMeta;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.io.File;
//import java.net.URL;
//import java.util.logging.Logger;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//class PotionConfigTest extends MMOTestEnvironment {
//
//    public static final String POTION_LEGACY_POTION_YML = "potion/legacy_potion.yml";
//    public static final String POTION_MODERN_YML = "potion/modern_potion.yml";
//    public static final Logger logger = Logger.getLogger(PotionConfigTest.class.getName());
//
//    @BeforeEach
//    void setUp() {
//        mockBaseEnvironment(logger);
//        final PotionMeta potionMeta = mock(PotionMeta.class);
//        when(itemFactory.getItemMeta(any())).thenReturn(potionMeta);
//    }
//
//    @AfterEach
//    void tearDown() {
//        cleanupBaseEnvironment();
//    }
//
//    @Test
//    void testLoadLegacyConfig() {
//        final PotionConfig potionConfig = getPotionConfig(POTION_LEGACY_POTION_YML);
//        assertNotNull(potionConfig);
//
//        potionConfig.loadConcoctions();
//        int loaded = potionConfig.loadPotionMap();
//        System.out.println("Loaded " + loaded + " potions");
//    }
//
//    @Test
//    void testModernConfig() {
//        final PotionConfig potionConfig = getPotionConfig(POTION_MODERN_YML);
//        assertNotNull(potionConfig);
//
//        potionConfig.loadConcoctions();
//        int loaded = potionConfig.loadPotionMap();
//        System.out.println("Loaded " + loaded + " potions");
//    }
//
//    private PotionConfig getPotionConfig(String path) {
//        // Get the file URL using the class loader
//        final URL resource = getClass().getClassLoader().getResource(path);
//        if (resource == null) {
//            throw new IllegalArgumentException("file not found!");
//        } else {
//            // Convert URL to a File object
//            final File potionFile = new File(resource.getFile());
//            System.out.println("File path: " + potionFile.getAbsolutePath());
//            return new PotionConfig(potionFile);
//        }
//    }
//}