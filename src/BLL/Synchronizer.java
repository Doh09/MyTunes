/*
 * looks at a folder and checks for changes
 */
package BLL;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 *
 * @authors Dennis, Niels, Alex and Tim.
 */

public class Synchronizer implements Runnable
{

    /**
     * Instance variables
     */
    private LoadFromMusicManager lfmm;
    private PreferenceManager pm;
    private Path musicFolder;
    private Thread t;
    private ArrayList<Path> created;
    private ArrayList<Path> deleted;
    private ArrayList<Path> modified;
    private ArrayList<Path> temporary;
    /**
     * Statics
     */
    private static Synchronizer instance;

    /**
     * Constructor
     */
    public Synchronizer()
    {
        //Initiate variables
        lfmm = LoadFromMusicManager.getInstance();
        pm = PreferenceManager.getInstance();
        created = new ArrayList<>();
        deleted = new ArrayList<>();
        modified = new ArrayList<>();
    }

    /**
     * Singleton pattern.
     *
     * @return an object of the Synchronizer.
     */
    public static synchronized Synchronizer getInstance()
    {
        if (instance == null)
        {
            instance = new Synchronizer();
        }
        return instance;
    }

    /**
     * Initializes the thread in which this class runs and sets the thread to be
     * daemon. The thread is also started after being set daemon.
     */
    public void start()
    {
        if (t == null)
        {
            t = new Thread(this);
            t.setDaemon(true);
            t.start();
        }
    }

    /**
     * Looping method that automatically is called when the class is created.
     * This method checks for changes in the folder it is set to watch. 3 types
     * of changes are detected: - Newly created files - Deleted files - Modified
     * files
     *
     * Each type of change is added to its own list of changes so they can be
     * distinguished.
     */
    @Override
    public void run()
    {
        try (WatchService watcher = FileSystems.getDefault().newWatchService()) //create watcher and try
        {
            String musicFolderString = pm.getSettingViaIndex("DEFAULT_DIRECTORY"); //Get music directory string
            File checkIfFolder = new File(musicFolderString.trim()); //check if string contains a folder location

            if (checkIfFolder.isDirectory())
            {
                musicFolder = checkIfFolder.toPath(); //if a folder, create path object from the string
            }
            else
            {
                throw new MyTunesException("Path: " + checkIfFolder + " is not a folder");
            }

            musicFolder.register(watcher, //setup watcher to look for files created, deleted and modified in the folder.
                    ENTRY_CREATE,
                    ENTRY_DELETE,
                    ENTRY_MODIFY);
            WatchKey key;
            while (true) //initiate loop, probably needs to be in separate thread.
            {
                try
                {
                    //obtain key
                    key = watcher.poll(10, TimeUnit.MILLISECONDS); //time limit to reduce CPU usage.
                    if (key == null)
                    {
                        continue; // restart loop key has no content.
                    }
                    // key value can be null if no event was triggered
                }
                catch (Exception ex)
                {
                    throw new MyTunesException("ERROR - failed to initiate watcher key");
                }

                Kind<?> kind = null;

                //Setup for checking all event types.
                for (WatchEvent<?> watchEvent : key.pollEvents())//Loop through all events the key carries.
                {
                    kind = watchEvent.kind();//make kind object to compare with. //final Kind<?> 
                    Path newPath = ((WatchEvent<Path>) watchEvent).context(); //newPath is set to relative path between watcher and the file changed.
                    if (StandardWatchEventKinds.OVERFLOW == kind)// Overflow event, indicates an event was lost.
                    {
                        continue; // restart loop early if event was lost.
                    }
                    else if (ENTRY_CREATE == kind)
                    {
                        // Output
                        //System.out.println("New path created: " + newPath); //for testing
                        created.add(newPath);
                    }
                    else if (ENTRY_DELETE == kind)
                    {
                        // Output
                        //System.out.println("Path deleted: " + newPath); //for testing
                        deleted.add(newPath);
                    }
                    else if (ENTRY_MODIFY == kind)
                    {
                        // Output
                        //System.out.println("Path modified: " + newPath); //for testing
                        modified.add(newPath);
                    }

                }
                key.reset();
            }
        }
        catch (IOException ex)
        {
            throw new MyTunesException("Unable to create and/or use watcher for folder synchronization.");
        }
    }

    //Getters from here on down.
    /**
     *
     * @return an ArrayList holding Paths of files created in the folder being
     * watched. Resets the ArrayList returned as well, so changes won't be
     * forwarded twice.
     */
    public ArrayList<Path> getCreated()
    {
        temporary = new ArrayList<>(); //reset temporary
        temporary = created;
        created = new ArrayList<>(); //reset created
        return temporary;
    }

    /**
     * @return an ArrayList holding Paths of files deleted in the folder being
     * watched. Resets the ArrayList returned as well, so changes won't be
     * forwarded twice.
     */
    public ArrayList<Path> getDeleted()
    {
        temporary = new ArrayList<>(); //reset temporary
        temporary = deleted;
        deleted = new ArrayList<>(); //reset deleted
        return temporary;
    }

    /**
     * @return an ArrayList holding Paths of files modified in the folder being
     * watched. Resets the ArrayList returned as well, so changes won't be
     * forwarded twice.
     */
    public ArrayList<Path> getModified()
    {
        temporary = new ArrayList<>(); //reset temporary
        temporary = modified;
        modified = new ArrayList<>(); //reset modified
        return temporary;
    }
}
