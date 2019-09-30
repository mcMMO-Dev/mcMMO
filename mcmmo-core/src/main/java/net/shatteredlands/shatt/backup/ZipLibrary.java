package net.shatteredlands.shatt.backup;

import com.gmail.nossr50.mcMMO;

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
    private final mcMMO pluginRef;

    private String BACKUP_DIRECTORY;
    private File BACKUP_DIR;
    private File FLAT_FILE_DIRECTORY;
    private File MOD_FILE_DIRECTORY;

    public ZipLibrary(mcMMO pluginRef) {
        this.pluginRef = pluginRef;

        BACKUP_DIRECTORY = pluginRef.getMainDirectory() + "backup" + File.separator;
        BACKUP_DIR = new File(BACKUP_DIRECTORY);
        FLAT_FILE_DIRECTORY = new File(pluginRef.getFlatFileDirectory());
        MOD_FILE_DIRECTORY = new File(pluginRef.getModDirectory());
    }

    public void mcMMOBackup() throws IOException {
        if (pluginRef.getMySQLConfigSettings().isMySQLEnabled()) {
            pluginRef.debug("This server is running in SQL Mode.");
            pluginRef.debug("Only config files will be backed up.");
        }

        try {
            if (BACKUP_DIR.mkdir()) {
                pluginRef.debug("Created Backup Directory.");
            }
        } catch (Exception e) {
            pluginRef.getLogger().severe(e.toString());
        }

        // Generate the proper date for the backup filename
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        File fileZip = new File(BACKUP_DIRECTORY + File.separator + dateFormat.format(date) + ".zip");

        // Create the Source List, and add directories/etc to the file.
        List<File> sources = new ArrayList<>();

        sources.add(FLAT_FILE_DIRECTORY);
        sources.addAll(pluginRef.getConfigManager().getConfigFiles()); //Config File Backups

        if (MOD_FILE_DIRECTORY.exists()) {
            sources.add(MOD_FILE_DIRECTORY);
        }

        // Actually do something
        pluginRef.debug("Backing up your mcMMO Configuration... ");

        packZip(fileZip, sources);
    }

    private void packZip(File output, List<File> sources) throws IOException {
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
        pluginRef.debug("Backup Completed.");
    }

    private String buildPath(String path, String file) {
        if (path == null || path.isEmpty()) {
            return file;
        }

        return path + File.separator + file;
    }

    private void zipDir(ZipOutputStream zos, String path, File dir) throws IOException {
        if (!dir.canRead()) {
            pluginRef.getLogger().severe("Cannot read " + dir.getCanonicalPath() + " (Maybe because of permissions?)");
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

    private void zipFile(ZipOutputStream zos, String path, File file) throws IOException {
        if (!file.canRead()) {
            pluginRef.getLogger().severe("Cannot read " + file.getCanonicalPath() + "(File Permissions?)");
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
