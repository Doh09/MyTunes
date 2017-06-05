/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAL;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.tritonus.share.sampled.file.TAudioFileFormat;

/**
 *  this class is used to read all information about a song and can 
 *  look a folder and return all songs or it can take a path
 *  and look inoformation about that song
 * @author Dennis, Alex, Niels and Tim.
 */
public class LoadFromMusicPersistentsManager {
    
    /**
     * Instance variables.
     */
    private final String MUSIC_FOLDER;

    /**
     * constructer.
     */
    public LoadFromMusicPersistentsManager(String MUSIC_FOLDER) {
        this.MUSIC_FOLDER = MUSIC_FOLDER;
    }
    
    /**
     * Static and finals.
     */
    public List<String> loadAllSongs() throws IOException{

        List<String> information = new ArrayList<>();
        Files.walk(Paths.get(MUSIC_FOLDER)).forEach(filePath -> {
                information.add(songInfo(filePath.toString(), "title"));
                information.add(songInfo(filePath.toString(), "mp3.id3tag.genre"));
                information.add(songInfo(filePath.toString(),"author"));
                information.add(songInfo(filePath.toString(),"duration"));
                information.add(filePath.toString());

        });
        return information;
    }
    
    /**
     * this method take a String of the path to the file you want to see 
     * and a string of what key value you want to look at
     * and returns that key information.
     * @param filePath
     * @param key
     * @return 
     */
    public String songInfo(String filePath,String key){
        
        Path path = Paths.get(filePath);
        //Tjeks if the path is mp3
        if(filePath.endsWith(".mp3")){
            File file = path.toFile();
            AudioFileFormat baseFileFormat = null;
            try {
            baseFileFormat = AudioSystem.getAudioFileFormat(file);
            } catch (UnsupportedAudioFileException | IOException ex) {
            }
            // TAudioFileFormat properties
            if (baseFileFormat instanceof TAudioFileFormat)
            {
                // gets the map of all the properties
                Map properties = ((TAudioFileFormat)baseFileFormat).properties();
                //tjeks what key it is and reruns it
                if(key.equals("duration")){
                    //if the key is duration then it will convert it to sec 
                    if((Long) properties.get("duration") == null){
                        return "00:00:00";
                    }
                    Long longtimeS = (Long) properties.get("duration") / 1000000;
                    // returns the time value to the format of our time class
                    return("00:00:" + longtimeS.toString());
                }
                if((String) properties.get(key) != null){
                   return(String) properties.get(key); 
                }else{
                    return " ";
                }
                
            }
        }
        return null;
    }
    
}
