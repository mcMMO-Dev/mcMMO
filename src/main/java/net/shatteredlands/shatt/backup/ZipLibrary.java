package net.shatteredlands.shatt.backup;

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

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;

public class ZipLibrary {

    private static String BackupDirectory = mcMMO.mainDirectory + "backup";
    private static File BackupDir = new File(BackupDirectory);
    private static File FlatFileDirectory = new File(mcMMO.flatFileDirectory);
    private static File UsersFile = new File(mcMMO.usersFile);
    private static File ConfigFile = new File(mcMMO.mainDirectory + "config.yml");
    private static File Leaderboards = new File(mcMMO.leaderboardDirectory);

    public static void mcMMObackup() throws IOException {
    	
    	if (Config.getInstance().getUseMySQL()) {
    		System.out.println("No Backup performed, in SQL Mode.");
    		return;
    	}
    	
    	try {
    	if (BackupDir.mkdir()) {
                mcMMO.p.getLogger().info("Created Backup Directory.");
    	}
            } catch (Exception e) {
                mcMMO.p.getLogger().severe(e.toString());
            }
 

        //Generate the proper date for the backup filename
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        File fileZip = new File(BackupDirectory + File.separator + dateFormat.format(date) + ".zip");

        //Create the Source List, and add directories/etc to the file.
        List<File> sources = new ArrayList<File>();
        sources.add(FlatFileDirectory);
        sources.add(UsersFile);
        sources.add(ConfigFile);
        sources.add(Leaderboards);

        //Actually do something
        System.out.println("Backing up your mcMMO Configuration... ");

        packZip(fileZip, sources);
    }

    private static void packZip(File output, List<File> sources) throws IOException
    {
        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(output));
        zipOut.setLevel(Deflater.DEFAULT_COMPRESSION);

        for (File source : sources) {
            if (source.isDirectory()) {
                zipDir(zipOut, "", source);
            }
            else {
                zipFile(zipOut, "", source);
            }
        }

        zipOut.flush();
        zipOut.close();
        System.out.println("Backup Completed.");
    }

    private static String buildPath(String path, String file) {
        if (path == null || path.isEmpty()) {
            return file;
        }
        else {
            return path + File.separator + file;
        }
    }

    private static void zipDir(ZipOutputStream zos, String path, File dir) throws IOException {
        if (!dir.canRead()) {
            System.out.println("Cannot read " + dir.getCanonicalPath() + " (maybe because of permissions)");
            return;
        }

        File[] files = dir.listFiles();
        path = buildPath(path, dir.getName());

        for (File source : files) {
            if (source.isDirectory()) {
                zipDir(zos, path, source);
            }
            else {
                zipFile(zos, path, source);
            }
        }
    }

    private static void zipFile(ZipOutputStream zos, String path, File file) throws IOException {
        if (!file.canRead()) {
            System.out.println("Cannot read " + file.getCanonicalPath() + "(File Permissions?)");
            return;
        }

        zos.putNextEntry(new ZipEntry(buildPath(path, file.getName())));

        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[4092];
        int byteCount = 0;
        
        while ((byteCount = fis.read(buffer)) != -1) {
            zos.write(buffer, 0, byteCount);
        }

        fis.close();
        zos.closeEntry();
    }
}
