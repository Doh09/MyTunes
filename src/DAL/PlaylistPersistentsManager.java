/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAL;

import BE.Playlist;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves and loads playlists
 * @author Dennis, Alex, Niels and Tim.
 */
public class PlaylistPersistentsManager {

    /**
     * Instance variables.
     */
    private String path;

    /**
     * Constructor
     * @param path
     */
    public PlaylistPersistentsManager(String path)
    {
        //Set default value of instance variables.
        this.path = path;
    }
    
    /**
     * Loads all the playlist from the dist and gives a list of the playlists.
     * @return List of the playlists on disk.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public List<Playlist> load() throws IOException, ClassNotFoundException
    {
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))){
          return (ArrayList<Playlist>) ois.readObject();
        }
    }

    /**
     * Saves all the playlists to disk.
     * @param playlists - List of playlists to save on disk.
     * @throws IOException
     */
    public void save(List<Playlist> playlists) throws IOException
    {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path)))
        {
            oos.writeObject(playlists);
            oos.close();
        }
        
    }
    
}
