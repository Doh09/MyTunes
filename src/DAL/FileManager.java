/*
 * manges file system
 */
package DAL;

import BLL.MyTunesException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @authors Dennis, Niels, Alex and Tim.
 */
public class FileManager {
    
    /**
     * Static and finals.
     */
    private static FileManager instance;
    
    /**
     * Constructor.
     */
    private FileManager() {}
    
    /**
     * Gets the instance of the manager.
     * @return instance of the manager.
     */
    public static FileManager getInstance() {
        if (instance == null) {
            instance = new FileManager();
        }
        return instance;
    }
    
    /**
     * Delets a file from the file system on the computer.
     * @param path - Path of the file to delete.
     * @throws MyTunesException
     */
    public void delete(String path) throws MyTunesException {
        
        try {
           
            if (!Files.deleteIfExists(Paths.get(path))) {
                
                //Throw error failed to delete file.
                throw new MyTunesException("Failed to delete the following file: " + path);
            }
        
        } catch (IOException ex) {
            
            //Throw error.
            throw new MyTunesException("Failed to delete a file: " + ex.getMessage());
        }
    }
    
}
