/*
 * handels genre by saving and loading useing GenrePM
 */
package BLL;

import DAL.GenrePersistentsManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @authors Dennis, Niels, Alex and Tim.
 */
public class GenreManager {
    
    /**
     * Static and finals.
     */
    private static GenreManager instance;
    private static final String GENRE_FILENAME = "Genre.txt";
    
    /**
     * Instance variables.
     */
    private GenrePersistentsManager gpm;
    
    /**
    * Constructor.
    */
    private GenreManager() {
        
        //Set default values of instance variables.
        this.gpm = new GenrePersistentsManager(GENRE_FILENAME);
    }
    
    /**
     * Gets a instance of the manager.
     * @return a instance of the manager.
     */
    public static GenreManager getInstance() {
        if (instance == null) {
            instance = new GenreManager();
        }
        return instance;
    }
    
    /**
     * Save the given list of genres.
     * @param genre genre is the list of all the genre 
     */
    public void save (List<String> genre){
        try{
            gpm.save(genre);
        }catch(IOException ex){
            throw new MyTunesException("Failed to save the genres: " + ex.getMessage());
        }
    }
    
    /**
     * Adds the given genre to the list.
     * @param genre - Genre to add.
     * @throws MyTunesException
     */
    public void add(String genre) throws MyTunesException {
        try {
            //Genre list.
            List<String> genres = load();

            //Check if genre already is in.
            for (String g : genres) {
                if (g.toLowerCase().equals(genre.toLowerCase())) {
                    throw new MyTunesException("The genre already exists in the list.");
                }
            }

            //Add genre to list.
            genres.add(genre);
            
            //Save list.
            gpm.save(genres);
        
        } catch (MyTunesException | IOException ex) {
            
            throw new MyTunesException(ex.getMessage());
        }
    }
    
    /**
     * Load a list of genres.
     * @return the hole list of genre 
     */
    public List<String> load (){
        try{
            return gpm.load();
        }catch(IOException ex){
            return new ArrayList<>();
        }
    }
    
}
