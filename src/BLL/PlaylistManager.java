/*
 * handes the methods of PlaylistPersistentsManager
 */
package BLL;

import BE.Playlist;
import DAL.PlaylistPersistentsManager;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dennis, Alex, Niels and Tim.
 */
public class PlaylistManager {
    
    /**
     * Static and finals.
     */
    private static PlaylistManager instance;
    private static final String PLAYLISTS_FILE_NAME = "Playlists";
    private static final String PLAYLISTS_FILE_EXTENSION = ".playlists";
    
    /**
     * Instance variables.
     */
    private PlaylistPersistentsManager ppm;
    private List<Playlist> playlists;
  
    
    /**
     * Constructor.
     */
    private PlaylistManager()
    {
        //Set default value of instance variables.
        ppm = new PlaylistPersistentsManager(PLAYLISTS_FILE_NAME + PLAYLISTS_FILE_EXTENSION);
        
        try {
            
            //Load Playlists from file.
           playlists = ppm.load();
            
        } catch(FileNotFoundException ex) {
        
          playlists = new ArrayList<>();
          
        } catch (IOException | ClassNotFoundException ex) {
           
            //Throw error.
            throw new MyTunesException("Failed to load playlist: " + ex.getMessage());
        }
    }
    
    /**
     * Gets the instance of manager.
     * @return instance of the manager.
     */
    public static PlaylistManager getInstance() {
        if (instance == null)
            instance = new PlaylistManager();
        return instance;
    }
    
    /**
     * Adds the given playlist to the playlist list.
     * @param playlist - playlist to add to Playlists list.
     */
    public void addPlaylist(Playlist playlist) throws MyTunesException {

        //Check if the playlist given not is null.
        if (playlist == null)
            throw new IllegalArgumentException("Playlist cannot be null.");
        
        //Check if the playlist already is in list.
        if (playlists.indexOf(playlist) != -1)
            throw new MyTunesException("Playlist can not be added, because it already exist.");
        
        playlists.add(playlist);
        
        save();
    }
    
    /**
     * Removes the given playlist from the Songs List.
     * @param playlist - playlist to remove from Playlists list.
     */
    public void removePlaylist(Playlist playlist) throws MyTunesException {
        
        //Check if playlist not is null.
        if (playlist == null)
            throw new IllegalArgumentException("Playlist can not be null.");
        
        playlists.remove(playlist);
        
        save();
    }
    
    /**
     * Renames the playlist to the new name given as parameter.
     * @param playlist - Playlist to change name on.
     * @param name - New new name for the playlist.
     */
    public void rename(Playlist playlist, String name) {
        
        //Check if playlist and name not is null.
        if (playlist == null || name == null)
            throw new IllegalArgumentException("Playlist and name can not be null.");
        
        playlist.setName(name);
        
        save();
    }
    
    /**
     * Gets the playlist from the given index.
     * @param index - Index of the playlist.
     * @return Playlist at the given index.
     */
    public Playlist get(int index) {
        return playlists.get(index);
    }
    
    /**
     * Gets all the Playlist in the list.
     * @return a list of songs in Playlists list.
     */
    public List<Playlist> getAllPlaylists() {
        return new ArrayList<>(playlists);
    }
    
    /**
     * Saves the PlayList list into DAL.
     */
    public void save() {
       
        try {
            
            //Save the Playlists list.
            ppm.save(playlists);
        
        } catch (IOException ex) {
            
            //Throw error.
            throw new MyTunesException("Failed to save the Songs playlist: " + ex.getMessage());
        }
    }
    
    /**
     * Closes the manager and saves the Playlist list.
     */
    public void close() {
        save();
    }
}
