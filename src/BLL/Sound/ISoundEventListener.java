/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL.Sound;

import BE.Song;

/**
 * Interface over an OnSongFinish listener.
 * @author Denis, Alex, Niels and Tim.
 */
public interface ISoundEventListener {
    
    /**
     * Event for listening when a song is stopped playing.
     */
    public abstract void onStopped();
    
    /**
     * Event for listening when a song is paused playing.
     */
    public void onPaused();
    
    /**
     * Event for listening when a song starts playing.
     * @param index - Index of the song in the playlist.
     * @param song - Song object which is playing.
     */
    public void onPlaying(int index, Song song);
}
