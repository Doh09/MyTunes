/*
 * SoundManager for playing music
 */
package BLL.Sound;

import BE.Playlist;
import BE.Song;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

/**
 * 
 * @author Dennis, Alex, Niels and Tim.
 */
public class SoundManager {
  
    /**
     * Enum of states for SoundManager.
     */
    public enum State {
        PLAYING, PAUSED, STOPPED, NOT_INITIALIZED
    }
    
    /**
     * Enum state of notifier type.
     */
    private enum Notify {
        onPlaying, onPaused, onStopped
    }
    
    /**
     * Finals.
     */
    private static final int DEFAULT_CURRENT = -1;
    private static final int DEFAULT_VOLUME = 100;
    private static final State DEFAULT_STATE = State.NOT_INITIALIZED;
    
    /**
     * Static.
     */
    private static SoundManager instance;
    
    /**
     * Instance variables.
     */
    private List<ISoundEventListener> listeners;
    private BasicPlayer player;
    private Playlist playlist;
    private int current;
    private int volume;
    private State state;
    private boolean isClosed;
    private BasicPlayerListener listener;
    /**
     * Constructor.
     */
    private SoundManager() {
        
        //Set default values of instance variables.
        this.listeners = new ArrayList<>();
        this.current = DEFAULT_CURRENT;
        this.volume = DEFAULT_VOLUME;
        this.state = DEFAULT_STATE;
        this.isClosed = false;
        
        //Basic listener.
        listener = new BasicPlayerListener() {

            @Override
            public void opened(Object o, Map map) {

            }

            @Override
            public void progress(int i, long l, byte[] bytes, Map map) {

            }

            @Override
            public void stateUpdated(BasicPlayerEvent bpe) {

                //Check if the song is finish playing.
                if (bpe.getCode() == BasicPlayerEvent.STOPPED) {

                    try {
                        
                        //Notify the listeners of the sound manager.
                        notifyListeners(Notify.onStopped);
                        
                        //Begin playing next song.
                        if (!isClosed) {
                            next();
                        }

                    } catch (SoundManagerException ex) {

                        //NO CODE....
                    }
                }
            }

            @Override
            public void setController(BasicController bc) {

            }

        };
    }
    
    /**
     * Gets the instance of the manager.
     * @return instance of the manager.
     */
    public static SoundManager getInstance() {
        if (instance == null)
            instance = new SoundManager();
        return instance;
    }
    
    /**
     * Create a player from the basic player library, from the given file.
     * @param path - Path to the file to play.
     */
    private void createPlayer(String path) throws SoundManagerException {
        
        //Check if player not exist already.
        if (player != null)
            destroyPlayer();
         
        //Create a file object.
        File file = new File(path);
        
        //Check if the file exist.
        if (file.exists()) {
            
            try {
                
                //Create player.
                if (player == null)
                    player = new BasicPlayer();
                
                //Open the file in player.
                player.open(file);
                
                //Add the listener so we can listen for events.
                player.addBasicPlayerListener(listener);
                
                //Update state.
                state = State.STOPPED;
            
            } catch (BasicPlayerException ex) {
                
                //Throw error.
                throw new SoundManagerException("Failed to create a player object: " + ex.getMessage());
            }
        }
    }
    
    /**
     * Destroys and stops the music there is playing.
     * @throws BLL.Sound.SoundManagerException
     */
    public void destroyPlayer() throws SoundManagerException {
        
        //Check if player exist.
        if (player != null) {
            
            try {
                
                //Stop the music and remove the reference to the player.
                player.removeBasicPlayerListener(listener);
                player.stop();
                
                //Update state.
                state = State.NOT_INITIALIZED;
            
            } catch (BasicPlayerException ex) {
                
                //Throw error.
                throw new SoundManagerException("Failed to stop the music: " + ex.getMessage());
            }
        }
    }
    
    /**
     * Changes the playing song to the given index song in the playlist.
     * @param index - Index of song to play in playlist.
     * @throws BLL.Sound.SoundManagerException
     */
    public void next(int index) throws SoundManagerException {
        
        //Check if a playlist is set.
        if (playlist != null) {
            
            //Check if we can go on forward.
            if (playlist.getSongList().size() > index) {
 
                //Create a player from the current song.
                createPlayer(playlist.getSongList().get(index).getPath());
                
                //Set current to the player song.
                current = index;
                
                //Start playing.
                play();
                
                //Update state.
                state = State.PLAYING;
            
            } else if (playlist.getSongList().size() > 0) {
                
                //Create a player from the current song.
                createPlayer(playlist.getSongList().get(0).getPath());
                
                //Set current to the player song.
                current = 0;
                
                //Start playing.
                play();
                
                //Update state.
                state = State.PLAYING;
            } 
        }
    }
    
    /**
     * Changes the playing song to the next song in the playlist.
     * @throws BLL.Sound.SoundManagerException
     */
    public void next() throws SoundManagerException {
        
        //Play next song in the playlist.
        next(current + 1);
    }
    
    /**
     * Changes the playing song to the previous song in the playlist.
     * @throws BLL.Sound.SoundManagerException
     */
    public void previous() throws SoundManagerException {
        
        //Check if a playlist is set.
        if (playlist != null) {
            
            //Check if we can go on forward.
            if (playlist.getSongList().size() > current - 1 && current - 1 >= 0) {
                
                //Create a player from the current song.
                createPlayer(playlist.getSongList().get(current - 1).getPath());
                
                //Set current to the player song.
                current--;
                
                //Start playing.
                play();
                
                //Update state.
                state = State.PLAYING;
            
            } else if (playlist.getSongList().size() > 0) {
                
                //Create a player from the current song.
                createPlayer(playlist.getSongList().get(playlist.getSongList().size() - 1).getPath());
                
                //Set current to the player song.
                current = playlist.getSongList().size() - 1;
                
                //Start playing.
                play();
                
                //Update state.
                state = State.PLAYING;
            }
        }
    }
    
    /**
     * Pauses the playing music.
     * @throws BLL.Sound.SoundManagerException
     */
    public void pause() throws SoundManagerException {
        
        //Check if player exist.
        if (player != null) {
            
            try {
                
                //Pause the sound.
                player.pause();
                
                //Update state.
                state = State.PAUSED;
                
                //Notify listeners.
                notifyListeners(Notify.onPaused);
                
            } catch (BasicPlayerException ex) {
               
                //Throw error.
                throw new SoundManagerException("Failed to pause music: " + ex.getMessage());
            }
        }
    }
    
    /**
     * Plays the music if it is stopped or paused.
     * @throws BLL.Sound.SoundManagerException
     */
    public void play() throws SoundManagerException {
       
        if (current != DEFAULT_CURRENT) {

            //Check if player exist.
            if (player != null) {

                try {

                    //Check state of the sound manager.
                    if (state == State.STOPPED) {
                        player.play();
                    } else if (state == State.PAUSED) {
                        player.resume();
                    }

                    //Set volume.
                    setVolume(volume);

                    //Notify the observs about the song begin playing.
                    notifyListeners(Notify.onPlaying, current, playlist.getSongList().get(current));

                    //Update state.
                    state = State.PLAYING;

                } catch (BasicPlayerException ex) {

                    //Throw error.
                    throw new SoundManagerException("Failed to play music: " + ex.getMessage());
                }

            }
        } else {
            
            //No song is current play next song.
            next();
        }
    }
    
    /**
     * Stops the player from playing music and setting the current to the first
     * song in the playlist.
     */
    public void stop() {
        
        try {
            
            //Destroy the player.
            destroyPlayer();
        
            //Set current to default.
            current = DEFAULT_CURRENT;
            
            //Stop.
            state = State.STOPPED;
            
            //Notify the listeners.
            notifyListeners(Notify.onStopped);
            
        } catch (SoundManagerException ex) {
            
            //NO CODE...
        }

    }
    
    /**
     * Set the playlist in SoundManager to play from.
     * @param playlist - Playlist to play from.
     * @throws BLL.Sound.SoundManagerException
     */
    public void setPlaylist(Playlist playlist) throws SoundManagerException {
        
        //Check if playlist not is null.
        if (playlist == null)
            throw new SoundManagerException("Playlist can not be null.");
        
        //Stop player.
        stop();
        
        //Set paylist.
        this.playlist = playlist;
    }
    
    /**
     * Sets the volume of the music.
     * @param volume - Volume to change to.
     */
    public void setVolume(int volume) throws SoundManagerException {
        
        if (volume < 0 || volume > 100)
            throw new SoundManagerException("Volume can not be under 0 and over 100.");
        
        if (player == null) return;
        if (!player.hasGainControl()) return;
        
        try {
            
            //Change volume.
            player.setGain(0.01 * volume);
            
            //Save volume.
            this.volume = volume;
        
        } catch (BasicPlayerException ex) {
            
            //Throw error.
            throw new SoundManagerException("Volume change failed: " + ex.getMessage());
        }
    }
    
    /**
     * Adds the given listener to listen for events on soundManager.
     * @param sel - SoundEventListener to use.
     * @throws BLL.Sound.SoundManagerException
     */
    public void addListener(ISoundEventListener sel) throws SoundManagerException {
        
        if (sel == null)
            throw new SoundManagerException("The given SoundEventListener can not be null.");
        
        //Add listener to listeners.
        listeners.add(sel);
    }
    
    /**
     * Notifies the listernes of the notify type has happened. Ex if the notify
     * type is onPlaying the onPlaying event is called on the listeners.
     * @param type - Notify type.
     * @param objects - Objects belonging to notify.
     */
    private void notifyListeners(Notify type, Object... objects) {
        for (ISoundEventListener listener : listeners) {
            switch (type) {
                case onPlaying:
                    listener.onPlaying((int)objects[0], (Song)objects[1]);
                    break;
                case onPaused:
                    listener.onPaused();
                    break;
                case onStopped:
                    listener.onStopped();
                    break;
            }
        }
    }
    
    /**
     * Gets the state of sound manager.
     * @return the state of the sound manager.
     */
    public State getState() {
        return state;
    }
    
    /**
     * Closes the soundManager.
     */
    public void close() {
        
        try {
            
            //Stops playing music and destroy.
            isClosed = true;
            destroyPlayer();
        
        } catch (SoundManagerException ex) {
            
            //NO CODE...
        }
    }
    
    public int getCurrent()
    {
    return current;
    }
}
