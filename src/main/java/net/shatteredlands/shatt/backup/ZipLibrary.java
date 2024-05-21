package net.shatteredlands.shatt.backup;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipLibrary {
    private static final String BACKUP_DIRECTORY = mcMMO.getMainDirectory() + "backup" + File.separator;
    private static final File BACKUP_DIR = new File(BACKUP_DIRECTORY);
    private static final File FLAT_FILE_DIRECTORY = new File(mcMMO.getFlatFileDirectory());
    private static final File MOD_FILE_DIRECTORY = new File(mcMMO.getModDirectory());
    private static final File CONFIG_FILE = new File(mcMMO.getMainDirectory() + "config.yml");
    private static final File EXPERIENCE_FILE = new File(mcMMO.getMainDirectory() + "experience.yml");
    private static final File TREASURE_FILE = new File(mcMMO.getMainDirectory() + "treasures.yml");
    private static final File ADVANCED_FILE = new File(mcMMO.getMainDirectory() + "advanced.yml");
    private static final File REPAIR_FILE = new File(mcMMO.getMainDirectory() + "repair.vanilla.yml");

    public static void mcMMOBackup() throws IOException {
        if (mcMMO.p.getGeneralConfig().getUseMySQL()) {
            LogUtils.debug(mcMMO.p.getLogger(), "This server is running in SQL Mode.");
            LogUtils.debug(mcMMO.p.getLogger(), "Only config files will be backed up.");
        }

        try {
            if (BACKUP_DIR.mkdir()) {
                LogUtils.debug(mcMMO.p.getLogger(), "Created Backup Directory.");
            }
        }
        catch (Exception e) {
            mcMMO.p.getLogger().severe(e.toString());
        }

        // Generate the proper date for the backup filename
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        File fileZip = new File(BACKUP_DIRECTORY + File.separator + dateFormat.format(date) + ".zip");

        // Create the Source List, and add directories/etc to the file.
        List<File> sources = new ArrayList<>();

        sources.add(FLAT_FILE_DIRECTORY);
        sources.add(CONFIG_FILE);
        sources.add(EXPERIENCE_FILE);
        sources.add(TREASURE_FILE);
        sources.add(ADVANCED_FILE);
        sources.add(REPAIR_FILE);

        if (MOD_FILE_DIRECTORY.exists()) {
            sources.add(MOD_FILE_DIRECTORY);
        }

        // Actually do something
        LogUtils.debug(mcMMO.p.getLogger(), "Backing up your mcMMO Configuration... ");

        packZip(fileZip, sources);
    }

    private static void packZip(File output, List<File> sources) throws IOException {
        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(output));
        zipOut.setLevel(Deflater.DEFAULT_COMPRESSION);

        for (File source : sources) {
            if (source.isDirectory()) {
                zipDir(zipOut, "", source);
            } else {
                zipFile(zipOut, "", source);
            }
        }

        zipOut.flush();
        zipOut.close();
        LogUtils.debug(mcMMO.p.getLogger(), "Backup Completed.");
    }

    private static String buildPath(String path, String file) {
        if (path == null || path.isEmpty()) {
            return file;
        }

        return path + File.separator + file;
    }

    private static void zipDir(ZipOutputStream zos, String path, File dir) throws IOException {
        if (!dir.canRead()) {
            mcMMO.p.getLogger().severe("Cannot read " + dir.getCanonicalPath() + " (Maybe because of permissions?)");
            return;
        }

        File[] files = dir.listFiles();
        path = buildPath(path, dir.getName());

        for (File source : files) {
            if (source.isDirectory()) {
                zipDir(zos, path, source);
            } else {
                zipFile(zos, path, source);
            }
        }
    }

    private static void zipFile(ZipOutputStream zos, String path, File file) throws IOException {
        if (!file.canRead()) {
            mcMMO.p.getLogger().severe("Cannot read " + file.getCanonicalPath() + "(File Permissions?)");
            return;
        }

        zos.putNextEntry(new ZipEntry(buildPath(path, file.getName())));

        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[4092];
        int byteCount;

        while ((byteCount = fis.read(buffer)) != -1) {
            zos.write(buffer, 0, byteCount);
        }

        fis.close();
        zos.closeEntry();
    }
}
