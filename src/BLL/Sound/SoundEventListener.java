/*
 * 
 */
package BLL.Sound;

import BE.Song;

/**
 * Abstract SoundEventLsitener.
 * @authors Dennis, Niels, Alex and Tim.
 */
public abstract class SoundEventListener implements ISoundEventListener {
    
    /**
     * Event for listening when a song is stopped playing.
     */
    @Override
    public abstract void onStopped();
    
    /**
     * Event for listening when a song is paused playing.
     */
    @Override
    public abstract void onPaused();
    
    /**
     * Event for listening when a song starts playing.
     * @param index - Index of the song in the playlist.
     * @param song - Song object which is playing.
     */
    @Override
    public abstract void onPlaying(int index, Song song);
}
