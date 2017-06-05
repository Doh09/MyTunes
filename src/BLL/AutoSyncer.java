/*
 * AutoSyncer.
 * This class checks for changes listed through the Synchronizer class.
 * if any changes are detected, appropriate action is taken in accordance with the change.
 * A notice is sent to the songsManager class and the songlist is updated.
 * Updates can be if a file was created, deleted or modified in the folder
 * which the Synchronizer class is keeping an eye on.
 */
package BLL;

import BE.Song;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 *
 * @authors Alex, Dennis, Tim & Niels
 */
public class AutoSyncer implements Runnable
{

    /**
     * Instance variables
     */
    private LoadFromMusicManager lfmm;
    private PreferenceManager preferenceManager;
    private SongsManager songsManager;
    private Synchronizer sync;
    private Thread thread;

    /**
     * Constructor, initializes the Manager classes and the Synchronizer.
     */
    public AutoSyncer()
    {
        lfmm = LoadFromMusicManager.getInstance();
        preferenceManager = PreferenceManager.getInstance();
        songsManager = SongsManager.getInstance();
        sync = Synchronizer.getInstance();
    }

    /**
     * Initializes the thread in which this class runs and sets the thread to be
     * daemon. The thread is also started after being set daemon.
     */
    public void start()
    {
        if (thread == null)
        {
            thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
        }
    }

    /**
     * Looping method that automatically is called when the class is created.
     * This method gets the list of changes from the Synchronizer class and
     * treats the changes by calling the handleChangedFiles(ArrayList<Path>,
     * String) method. The method has a built in timer so it occurs every
     * second, this is to limit CPU use.
     */
    @Override
    public void run()
    {
        //initiate loop
        while (true)
        {
            try
            {
                TimeUnit.SECONDS.sleep(1); //pause 1 second
                if (preferenceManager.getSettingViaIndex("SYNCHRONIZE_ACTIVATED").equalsIgnoreCase("true")) //check if synchronization is active.
                {
                    ArrayList<Path> changes = sync.getCreated();
                    handleChangedFiles(changes, "created"); //sync created files, unfinished, doesn't add genre, artist and correct song name.
                    changes = sync.getDeleted();
                    handleChangedFiles(changes, "deleted"); //sync deleted files.
                    changes = sync.getModified();
                    handleChangedFiles(changes, "modified"); //sync modified files
                }
            }
            catch (Exception ex)
            {
                throw new MyTunesException("ERROR - unable to autoSync");
            }
        }
    }

    /**
     * Method that handles an ArrayList<Path> according to the String mode it is
     * set to. The method loops through each index in the list, if the path is
     * to an .mp3 file type, One of three things can happen depending on the
     * mode set. - The file was created, it is added to the song library
     * SongsManager already holds. - The file was modified, the old file is
     * replaced with the new edited version. - If the file was deleted, it is
     * also deleted from the SongsManager song list.
     *
     * @param changes
     * @param mode
     * @throws MyTunesException
     */
    private void handleChangedFiles(ArrayList<Path> changes, String mode) throws MyTunesException
    {
        if (changes.size() > 0) //if any changes found
        {
            for (Path p : changes) //for each change
            {
                String filePath = p.toString();

                if (filePath.endsWith(".mp3")) //if it was an .mp3 file that was changed
                {
                    //string together the full path of the changed file.
                    String fullPath = preferenceManager.getSettingViaIndex("DEFAULT_DIRECTORY") + "\\" + filePath;

                    if (!mode.equalsIgnoreCase("deleted")) //if it was not a file deletion.
                    {

                        String title = lfmm.loadASong(fullPath, "title");
                        String genre = lfmm.loadASong(fullPath, "mp3.id3tag.genre");
                        String author = lfmm.loadASong(fullPath, "author");
                        String fileTime = lfmm.loadASong(fullPath, "duration"); //get the time length of the song
                        Song newSong = new Song(title, genre, author, new Time(fileTime), fullPath); //construct the song from the information found.

                        if (mode.equalsIgnoreCase("created")) //if it was a newly created file.
                        {
                            if (songsManager.getAllSongs().size() == 0)
                            {
                                songsManager.add(newSong);
                            }
                            for (Song s : songsManager.getAllSongs())
                            {
                                int counter = 0;
                                if (s.getTitle().equals(newSong.getTitle()) && s.getArtistName().equals(newSong.getArtistName()) || s.getPath().equalsIgnoreCase(newSong.getPath()))
                                {
                                    counter = 1;
                                }
                                if (counter == 0)
                                {
                                    songsManager.add(newSong);
                                }
                            }
                        }
                        else if (mode.equalsIgnoreCase("modified")) //if it was a modified file, replace the old file with the new file in MyTunes.
                        {
                            for (Song s : songsManager.getAllSongs())
                            {
                                int counter = 0;
                                if (s.getPath().equalsIgnoreCase(newSong.getPath()) && !s.getTitle().equals(newSong.getTitle()) 
                                        || s.getPath().equalsIgnoreCase(newSong.getPath()) && !s.getArtistName().equals(newSong.getArtistName())
                                        )
                                
                                {
                                    songsManager.remove(s);
                                    songsManager.add(newSong);
                                    counter = 1;
                                }
                                
                            }
                        }
                    }
                    //if it was a file deletion.
                    else if (mode.equalsIgnoreCase("deleted"))
                    {
                        for (Song s : songsManager.getAllSongs())
                        {
                            if (s.getPath().equalsIgnoreCase(fullPath))
                            {
                                songsManager.remove(s);
                            }
                        }
                    }//soundManager.stop(); //skal muligvis bruges??
                }
            }
        }
        songsManager.save();
    }
}
