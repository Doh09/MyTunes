/**
 * manges the methods from songsPersistensManager
 */
package BLL;

import BE.Song;
import DAL.SongsPersistentsManager;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dennis, Alex, Niels and Tim.
 */
public class SongsManager
{

    /**
     * Static and finals.
     */
    private static SongsManager instance;
    private static final String SONGS_FILE_NAME = "Songs";
    private static final String SONGS_FILE_EXTENSION = ".playlist";

    /**
     * Instance variables.
     */
    private SongsPersistentsManager spm;
    private List<Song> songs;

    /**
     * Constructor.
     */
    private SongsManager()
    {

        //Set default value of instance variables.
        spm = new SongsPersistentsManager(SONGS_FILE_NAME + SONGS_FILE_EXTENSION);

        try
        {

            //Load Songs from file.
            songs = spm.load();
        }
        catch (FileNotFoundException ex)
        {

            //Songs do not exist, create a new one.
            songs = new ArrayList<>();

        }
        catch (IOException | ClassNotFoundException ex)
        {

            //Throw error.
            throw new MyTunesException("Failed to load Songs playlist: " + ex.getMessage());
        }
    }

    /**
     * Gets the instance of manager.
     *
     * @return instance of the manager.
     */
    public static SongsManager getInstance()
    {
        if (instance == null)
        {
            instance = new SongsManager();
        }
        return instance;
    }

    /**
     * Adds the given song to the Songs list.
     *
     * @param song - Song to add to Songs list.
     */
    public void add(Song song) throws MyTunesException
    {
        //Check if the song object not is null.
        if (song == null)
            throw new IllegalArgumentException("Song object cannot be null.");
        
        //Check if the Song object not have an id, only a song which have not
        //been assigned an id can be added.
        if (song.getId() != -1)
            throw new IllegalArgumentException("Only a Song object which have not been assigned an id can be added.");
        
        //Id for the new song object.
        int id = song.getId();

        //Get last object in songs list.
        if (songs.size() > 0) {

            //Get id from the lsat song.
            id = songs.get(songs.size() - 1).getId() + 1;
            
        } else {
            
            //Assign the first id.
            id++;
        }

        //Add new id to song object.
        Song nSong = new Song(
                id,
                song.getTitle(),
                song.getGenre(),
                song.getArtistName(),
                song.getTime(),
                song.getPath()
        );
        
        //Add song object to songs list.
        songs.add(nSong);
        
    }

    /**
     * Removes the given song from the Songs List.
     *
     * @param song - Song to remove from song list.
     */
    public void remove(Song song) throws MyTunesException
    {
        //Check if song object is given.
        if (song == null)
            throw new IllegalArgumentException("Song object cannot be null.");
        
        //Check if the Song object not have an id, only a song which have not
        //been assigned an id can be added.
        if (song.getId() == -1)
            throw new IllegalArgumentException("Only a Song object which have not been assigned an id can be removed.");
        
        //Remove object from list.
        songs.remove(song);
        
        save();
    }
    
    /**
     * Updates the song in the songs list.
     * @param song - Song to update.
     */
    public void update(Song song) {
        
        //Check if song object is given.
        if (song == null)
            throw new IllegalArgumentException("Song object cannot be null.");
        
        //Check if the Song object not have an id, only a song which have not
        //been assigned an id can be added.
        if (song.getId() == -1)
            throw new IllegalArgumentException("Only a Song object which have not been assigned an id can be updated.");
        
        //Update the song object in the song list.
        int index = songs.indexOf(song);
        songs.set(index, song);
        
        save();
    }
    
    /**
     * Gets the song at the given index.
     * @param index - Index of the song.
     * @return Song at the given index.
     */
    public Song get(int index) {
        return songs.get(index);
    }

    /**
     * Gets all the songs in the list.
     *
     * @return a list of songs in songs list.
     */
    public List<Song> getAllSongs()
    {
        return new ArrayList<>(songs);
    }

    /**
     * Saves the Songs list into DAL.
     */
    public void save()
    {

        try
        {
            //Save the songs list.
            spm.save(songs);
            
        }
        catch (IOException ex)
        {

            //Throw error.
            throw new MyTunesException("Failed to save the Songs playlist: " + ex.getMessage());
        }
    }

    /**
     * Closes the manager and saves the songs list.
     */
    public void close()
    {
        save();
    }
}
