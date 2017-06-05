/*
 * playlist is a list of some songs
 */
package BE;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * holds songs, and makes playlists editable
 *
 * @author Denis, Niels, Alex and Tim.
 */
public class Playlist implements Serializable
{

    /**
     * Instance variables.
     */
    private List<Song> songs;
    private String name;

    /**
     * Constructor.
     *
     * @param name - Name of the playlist.
     * @param songs - List of songs in playlist.
     */
    public Playlist(String name, List<Song> songs)
    {
        this.name = name;
        this.songs = songs;

    }

    /**
     * Gets the list of songs the playlist contains.
     *
     * @return the playlist
     */
    public List<Song> getSongList()
    {
        return songs;
    }

    /**
     * Sets the songs list in the playlist.
     *
     * @param playlist the playlist to set
     */
    public void setSonglist(List<Song> playlist)
    {
        this.songs = playlist;
    }

    /**
     * Gets the name of the playlist.
     *
     * @return the playlist name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of the playlist.
     *
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Adds the given song to the song list in the playlist.
     *
     * @param song - Song object to insert in playlist.
     */
    public void addSong(Song song)
    {
        //Check if song not is null.
        if (song == null)
            throw new IllegalArgumentException("Song object cannot be null.");
        
        songs.add(song);
    }

    /**
     * Removes the all the duplicates of a song in the playlist, so the
     * song object is not in the playlist anymore.
     *
     * @param song - Song object to remove.
     */
    public void removeAllSong(Song song)
    {
        //Check if song not is null.
        if (song == null)
            throw new IllegalArgumentException("Song object cannot be null.");
        
        int index;
        
        while ((index = songs.indexOf(song)) != -1)
            songs.remove(index);
    }
    
    /**
     * Removes the song at the given index in the playlist.
     *
     * @param index - Index of the song object to remove.
     */
    public void removeSong(int index)
    {
        //Check if song not is null.
        if (index < 0 || index >= songs.size())
            throw new IllegalArgumentException(
                    "Song index cannot be smaller or higher than the number of "
                            + "songs in the playlist"
            );
        
        songs.remove(index);
    }
    
    /**
     * Moves the given song one down in the list.
     * @param index - Index of song to move.
     */
    public void moveDown(int index) {
        if (index != -1 && songs.size() > index + 1) {
            Collections.swap(songs, index, index + 1);
        }
    }
    
    /**
     * Moves the given song one up in the list.
     * @param index - Index to move up.
     */
    public void moveUp(int index) {
        if (index != -1 && index - 1 >= 0) {
            Collections.swap(songs, index, index - 1);
        }
    }
    
    /**
     * Updates the song in playlist with the new information given through the
     * given song object.
     * @param song - Song to update in playlist.
     */
    public void updateSong(Song song) {
        
        //Check if song not is null.
        if (song == null)
            throw new IllegalArgumentException("Song object cannot be null.");
        
        //Loop through the list to change song information.
        for (int i = 0; i < songs.size(); i++) {

            if (songs.get(i).equals(song)) {
                songs.set(i, song);
            }
        }
    }

    /**
     * Finds the time of the playlist
     *
     * @return
     */
    public String getTime()
    {

        int timeS = 0;
        int timeM = 0;
        int timeH = 0;
        for (Song song : songs)
        {
            timeS += song.getTime().getSeconds();
            timeM += song.getTime().getMinuts();
            timeH += song.getTime().getHours();
            if (timeS > 59)
            {
                timeM++;
                timeS -= 60;
            }
            if (timeM > 59)
            {
                timeH++;
                timeM -= 60;
            }
        }

        String alltime = String.valueOf(timeH) + ":" + String.valueOf(timeM) + ":" + String.valueOf(timeS);
        return alltime;
    }
    
    
    @Override
    public boolean equals(Object obj) {
        
        //Check if object is a playlist object.
        if (obj instanceof Playlist) {
            
            //Check if names is the same.
            if (name.equals(((Playlist)obj).getName())) return true;
        }
        
        return false;
    }
    
/**
 * Checks whether a song is already in the playlist.
 * If it is, returns false, if it is not, returns true.
 * @param song
 * @return 
 */
    public boolean notInPlaylistAlready(Song song)
    {
        for (Song sInPlaylist : songs)
        {
            //check if song with same path is already in playlist
            if (sInPlaylist.equals(song))
            {
                return false;
            }
        }
        return true;
    }
}
