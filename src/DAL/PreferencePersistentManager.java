/*
 * PreferencePersistentManager.
 * The task of this class is to save and load the program preferences
 * as set by the user in the settings of the program or through
 * the user editing the .txt file holding the preferences.
 */
package DAL;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author Alex, Dennis, Niels and Tim.
 */
public class PreferencePersistentManager
{

    /**
     * Final variables
     */
    private final String fileName;

    /**
     * Constructor of the PreferencePersistenceManager. Initiates the fileName
     * variable so the class knows which file path to use.
     *
     * @param fileName
     */
    public PreferencePersistentManager(String fileName)
    {
        this.fileName = fileName;
    }

    /**
     * Saves the content of the Map given to a file. The file path saved to is
     * specified in the constructor of this class. A loop is used to ensure
     * method compatability with new content added to the Map.
     *
     * @param settingsToSave
     * @throws IOException
     */
    public void saveSettingsToFileTxt(Map<String, String> settingsToSave) throws IOException
    {
        //save the settings information
        Properties prop = new Properties();
        FileOutputStream fos = new FileOutputStream(fileName);

        //Loop through each map entry and save them.
        for (Map.Entry<String, String> kv : settingsToSave.entrySet())
        {
            if (kv.getValue() != null)
            {
                prop.setProperty(kv.getKey(), kv.getValue());
            }
            else
            {
                prop.setProperty(kv.getKey(), "null");
            }
        }
        //Store the content loaded using the outputstream to the file destination.
        prop.store(fos, fileName);
        //close the output stream.
        fos.close();
    }

    /**
     * This method reads a .txt file and stores the content in a HashMap which
     * is then returned.
     *
     * @return
     */
    public Map<String, String> loadSettingsFromFileTxt()
    {
        Map<String, String> settings = new HashMap<>();

        try
        {
            Properties prop = new Properties();
            FileInputStream fis = new FileInputStream(fileName);
            prop.load(fis);

            //Load in settings -> settings.put(index, content);
            //WINDOW_STARTUP setting
            settings.put("WINDOW_STARTUP", prop.getProperty("WINDOW_STARTUP"));
            //DEFAULT_DIRECTORY setting
            settings.put("DEFAULT_DIRECTORY", prop.getProperty("DEFAULT_DIRECTORY"));
            //SONGDELETION_CONFIRMATION setting
            settings.put("SONGDELETION_CONFIRMATION", prop.getProperty("SONGDELETION_CONFIRMATION"));
            //SONGDELETIONHDDYes_CONFIRMATION setting
            settings.put("SONGDELETIONHDDYes_CONFIRMATION", prop.getProperty("SONGDELETIONHDDYes_CONFIRMATION"));
            //SONGDELETIONHDDNo_CONFIRMATION setting
            settings.put("SONGDELETIONHDDNo_CONFIRMATION", prop.getProperty("SONGDELETIONHDDNo_CONFIRMATION"));
            //SYNCHRONIZE_ACTIVATED setting
            settings.put("SYNCHRONIZE_ACTIVATED", prop.getProperty("SYNCHRONIZE_ACTIVATED"));
            //if both yes and no to SONGDELETION is true, set one false.
            if (prop.getProperty("SONGDELETIONHDDNo_CONFIRMATION").equalsIgnoreCase("true") && prop.getProperty("SONGDELETIONHDDYes_CONFIRMATION").equalsIgnoreCase("true"))
            {
                settings.put("SONGDELETIONHDDNo_CONFIRMATION", "false");
            }
            //Close the input stream
            fis.close();
        }
        //Return a new HashMap if loading the file fails.
        catch (IOException ex)
        {
            return new HashMap<>();
        }
        //return the content loaded.
        return settings;
    }
}
