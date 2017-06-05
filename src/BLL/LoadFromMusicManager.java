/*
 * handes the methods of LoadFromMusicPersistentsManager 
 */
package BLL;

import BE.Song;
import DAL.LoadFromMusicPersistentsManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @authors Alex, Dennis, Tim & Niels
 */
public class LoadFromMusicManager
{

    /**
     * Instance variables.
     */
    private PreferenceManager preferenceManager = PreferenceManager.getInstance();
    private final int TITLE = 0;
    private final int GENRE = 1;
    private final int ARTISTNAME = 2;
    private final int TIME = 3;
    private final int PATH = 4;
    private final int NUMBER_SONGINFO = 5;
 
    private static LoadFromMusicManager instance;

    private final String Path = preferenceManager.getSettingViaIndex("DEFAULT_DIRECTORY");
    private LoadFromMusicPersistentsManager LoadMusic;

    /**
     * Constructor.
     */
    private LoadFromMusicManager() {
        this.LoadMusic = new LoadFromMusicPersistentsManager(Path);
    }

    /**
     * Gets the instance of the manager.
     *
     * @return instance of the manager.
     */
    public static LoadFromMusicManager getInstance()
    {
        if (instance == null)
        {
            instance = new LoadFromMusicManager();
        }
        return instance;
    }
    
    /**
     * loads all songs from Path variables path and returns a list of songs from it.
     * @return 
     */
    public List<Song> Load(){
       List<Song> songs = new ArrayList<>();
       List<String> info = null;
        try {
            info = LoadMusic.loadAllSongs();
        }
        catch (IOException ex)
        {
            throw new MyTunesException(ex.getMessage());
        }
        
        if (info != null){
            for (int i = 0; i < info.size(); i++) {
                if(i%NUMBER_SONGINFO == 0){
                    if(info.get(i+TIME)!=null){
                        Song song = new Song(info.get(i+TITLE), info.get(i+GENRE), info.get(i+ARTISTNAME), new Time(info.get(i+TIME)), info.get(i+PATH));
                        if(!song.getTitle().equals(" ")){
                            songs.add(song);
                        }
                    }                      
                }
            }
        }
        return songs;
    }
    
    /**
     * takes the filepath and looks for the song, then returns the information from it.
     * the key
     * @param filePath
     * @param key
     * @return 
     */
    public String loadASong(String filePath,String key){
        return LoadMusic.songInfo(filePath, key);
    }
}
