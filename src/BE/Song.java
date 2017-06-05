/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BE;

import java.io.Serializable;
import BLL.Time;

/**
 * Business Entity for a Song. This class is designed to contain information
 * about a song.
 * 
 * @author Denis, Niels, Alex and Tim.
 */
public class Song implements Serializable{
    
    /**
     * Instance variables.
     */
    private final int id;
    private String title;
    private String genre;
    private String artistName;
    private Time time;
    private String path;
    
    /**
     * Constructor.
     * @param id - Id for the song.
     * @param title - Name of the song.
     * @param genre - Genre of the song.
     * @param artistName - Name of the artist.
     * @param time - Time of the file.
     * @param path - Path for the file.
     */
    public Song(int id, String title, String genre, String artistName, Time time, String path) {
        
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.artistName = artistName;
        this.time = time;
        this.path = path;
    }
    
    /**
     * Constructor.
     * @param title - Name of the song.
     * @param genre - Genre of the song.
     * @param artistName - Name of the artist.
     * @param time - Time of the file.
     * @param path - Path for the file.
     */
    public Song(String title, String genre, String artistName, Time time, String path) {
        
        this.id = -1;
        this.title = title;
        this.genre = genre;
        this.artistName = artistName;
        this.time = time;
        this.path = path;
    }

    /**
     * @return the id.
     */
    public int getId() {
        return id;
    }
    
    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the songName to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the genre
     */
    public String getGenre() {
        return genre;
    }

    /**
     * @param genre the genre to set
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * @return the artistName
     */
    public String getArtistName() {
        return artistName;
    }

    /**
     * @param artistName the artistName to set
     */
    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    /**
     * @return the time
     */
    public Time getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(Time time) {
        this.time = time;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }
    
    @Override
    public boolean equals(Object obj) {
        
        if (obj == null) return false;
        
        return ((Song)obj).getId() == id;
    }
}
