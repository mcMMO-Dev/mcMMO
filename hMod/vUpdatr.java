//Thanks to Yogoda for the code!
import java.io.*;
import java.util.logging.*;
public class vUpdatr {

    static final String pluginName = "vMinecraft";
    static final String version = "0.1";
    static final String updatrUrl = "http://dl.dropbox.com/u/18212134/vMinecraft.updatr";
    static final String updatrFileUrl = "http://dl.dropbox.com/u/18212134/vMinecraft.jar";
    static final String updatrNotes = "Added Updatr support!";
    private static volatile vUpdatr instance;
    
    public static Logger logger = Logger.getLogger("Minecraft");

    public void createUpdatrFile(){

        try {
    
            File updatrDir = new File("Updatr");

            //create Updatr directory if it does not exsits already
            if(updatrDir.exists()){
            
                File updatrFile = new File("Updatr" + File.separator + pluginName + ".updatr");
                
                //Updatr file does not exist, create it
                if(!updatrFile.exists()){
                    updatrFile.createNewFile();
                    BufferedWriter writer = new BufferedWriter(new FileWriter(updatrFile));
                    writer.write("name = " + pluginName); writer.newLine();
                    writer.write("version = " + version); writer.newLine();
                    writer.write("url = " + updatrUrl); writer.newLine();
                    writer.write("file = " + updatrFileUrl); writer.newLine();
                    writer.write("notes = " + updatrNotes); writer.newLine();
                    writer.close();
                }
            }
        } catch (IOException e) {
            vUpdatr.logger.log(Level.SEVERE, null, e);
        }
    }
public static vUpdatr getInstance(){
    if (instance == null){
        instance = new vUpdatr();
    }
    return instance;
}
}