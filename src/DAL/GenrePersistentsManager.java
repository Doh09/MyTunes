/*
 * this class saves and loads all genre en a file that it gets from the class that 
 * calls it
 */
package DAL;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Denis, Niels, Alex and Tim.
 */
public class GenrePersistentsManager {
    
    /**
     * Instance variables.
     */
    private final String path;
    
    /**
     * Constructor.
     * @param path - Path to were to save the genre information.
     */
    public GenrePersistentsManager(String path) {
        
        //Set default values of instance variables.
        this.path = path;
    }
    
    /**
     * Saves a list of genres to the file the manager is using.
     * @param genre the hole list of genre that it have to save
     * @throws IOException if anything happens it will throw a IOException
     */
    public void save(List<String> genre) throws IOException{
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(path))){
            for (String genre1 : genre) {
               bw.write(genre1+"\r\n");
            }
  
        }
    }
    
    /**
     * Load a list of genre from the file the manager is using.
     * @return returns the list of genre 
     * @throws IOException if anything happens it will throw a IOException
     */
    public List<String> load()throws IOException{
        List<String> loadgenre = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(path))){
            while (br.ready()) {                
                loadgenre.add(br.readLine());
            }
        }
        return loadgenre;
    }
    
}
