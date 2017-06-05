/*
 * PreferenceManager.
 * Class acting as a connection between PreferencePersistentManager
 * and the classes using this (PreferenceManager) class.
 * Also initiates the singleton pattern for the Settings class as it
 * is able to load in the settings needed from the DAL layer.
 */
package BLL;

import DAL.PreferencePersistentManager;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author Alex, Dennis, Tim & Niels
 */
public class PreferenceManager
{

    /**
     * final & static variables.
     */
    private static final String FILE_NAME = "preferences.txt";
    private static PreferenceManager instance;
    /**
     * Instance variables
     */
    private PreferencePersistentManager ppm;
    private Map<String, String> settings;

    /**
     * Constructor for the PreferenceManager. Initiates the
 PreferencePersistentManager. Also initiates the settings variable with
 information From the PreferencePersistentManager.
     */
    private PreferenceManager()
    {
        ppm = new PreferencePersistentManager(FILE_NAME);
        settings = ppm.loadSettingsFromFileTxt();
        validateContent();
    }

    /**
     * Returns an instance of the object itself following the singleton pattern.
     *
     * @return
     */
    public static PreferenceManager getInstance()
    {
        if (instance == null)
        {
            instance = new PreferenceManager();
        }
        return instance;
    }

    /**
     * Saves the settings held by the Settings class through the
 PreferencePersistentManager to a .txt file.
     *
     * @throws IOException
     */
    public void saveSettings() throws IOException
    {
        ppm.saveSettingsToFileTxt(settings);
    }

    /**
     * Method called when the program GUI is shut down. Ensures that the program
     * settings are saved upon shutdown.
     *
     * @throws IOException
     */
    public void close() throws IOException
    {
        saveSettings();
    }

    /**
     * Returns a Map<String, String> holding the settings preferences from the
     * last time they were edited.
     *
     * @return
     */
    public Map<String, String> loadSettingsFromFile()
    {
        return ppm.loadSettingsFromFileTxt();
    }

    //
    //
    //Administrate settings hashmap
    //
    //
    /**
     * Sets the setting String for a single key index in the settings Map.
     *
     * @param key
     * @param setting
     */
    public void setSettingForIndex(String key, String setting)
    {
        if (settings.containsKey(key))
        {
            settings.replace(key, setting);
        }
        else
        {
            settings.put(key, setting);
        }
    }

    /**
     * Returns the setting String for a single key index in the settings Map If
     * the key index doesn't exist in the map, null is returned.
     *
     * @param command
     * @return
     */
    public String getSettingViaIndex(String command)
    {
        if (settings.containsKey(command))
        {
            return settings.get(command);
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns the entire settings Map.
     *
     * @return
     */
    public Map<String, String> getAllSettings()
    {
        return settings;
    }

    /**
     * Method that checks if the settings loaded from file match expected
     * settings information. If not, the setting is set to default.
     */
    private void validateContent()
    {
        //Validate WINDOW_STARTUP
        if (settings.containsKey("WINDOW_STARTUP"))
        {
            if (settings.get("WINDOW_STARTUP") == null || settings.get("WINDOW_STARTUP").equalsIgnoreCase("null"))
            {
                settings.put("WINDOW_STARTUP", "true");
            }
        }
        else
        {
            settings.put("WINDOW_STARTUP", "true");
        }
        //Validate SONGDELETION_CONFIRMATION
        if (settings.containsKey("SONGDELETION_CONFIRMATION"))
        {
            if (settings.get("SONGDELETION_CONFIRMATION") == null || settings.get("SONGDELETION_CONFIRMATION").equalsIgnoreCase("null"))
            {
                settings.put("SONGDELETION_CONFIRMATION", "false");
            }
        }
        else
        {
            settings.put("SONGDELETION_CONFIRMATION", "false");
        }

        //Validate SONGDELETIONHDDYes_CONFIRMATION
        if (settings.containsKey("SONGDELETIONHDDYes_CONFIRMATION"))
        {
            if (settings.get("SONGDELETIONHDDYes_CONFIRMATION") == null || settings.get("SONGDELETIONHDDYes_CONFIRMATION").equalsIgnoreCase("null"))
            {
                settings.put("SONGDELETIONHDDYes_CONFIRMATION", "false");
            }
        }
        else
        {
            settings.put("SONGDELETIONHDDYes_CONFIRMATION", "false");
        }

        //Validate SONGDELETIONHDDNo_CONFIRMATION
        if (settings.containsKey("SONGDELETIONHDDNo_CONFIRMATION"))
        {
            if (settings.get("SONGDELETIONHDDNo_CONFIRMATION") == null || settings.get("SONGDELETIONHDDNo_CONFIRMATION").equalsIgnoreCase("null"))
            {
                settings.put("SONGDELETIONHDDNo_CONFIRMATION", "false");
            }
        }
        else
        {
            settings.put("SONGDELETIONHDDNo_CONFIRMATION", "false");
        }

        //Validate SYNCHRONIZE_ACTIVATED
        if (settings.containsKey("SYNCHRONIZE_ACTIVATED"))
        {
            if (settings.get("SYNCHRONIZE_ACTIVATED") == null || settings.get("SYNCHRONIZE_ACTIVATED").equalsIgnoreCase("null"))
            {
                settings.put("SYNCHRONIZE_ACTIVATED", "false");
            }
        }
        else
        {
            settings.put("SYNCHRONIZE_ACTIVATED", "false");
        }

        //Validate DEFAULT_DIRECTORY
        if (settings.containsKey("DEFAULT_DIRECTORY"))
        {
            if (settings.get("DEFAULT_DIRECTORY") == null || settings.get("DEFAULT_DIRECTORY").equalsIgnoreCase("null"))
            {
                settings.put("DEFAULT_DIRECTORY", settings.get("DEFAULT_DIRECTORY"));
            }
        }
        else
        {
            settings.put("DEFAULT_DIRECTORY", System.getProperty("user.home") + File.separatorChar + "Music");
        }
    }
}
