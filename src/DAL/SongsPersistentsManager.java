/*
 * saves and loads all the songs
 */
package DAL;

import BE.Song;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dennis, Alex, Niels and Tim.
 */
public class SongsPersistentsManager
{

    /**
     * Instance variables.
     */
    private final String path;

    /**
     * Constructor.
     *
     * @param path - Path to save songs list to.
     */
    public SongsPersistentsManager(String path)
    {

        //Set default values of instance variables.
        this.path = path;
    }

    /**
     * Gets a Songs list from the given file path.
     *
     * @return a Songs object.
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws FileNotFoundException
     */
    public List<Song> load() throws IOException, ClassNotFoundException, FileNotFoundException
    {
        //Get the Songs object from the song file through ObjectInputStream.
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path)))
        {

            //Read and return Songs object from file.
            return (ArrayList<Song>) ois.readObject();
        }
    }

    /**
     * Saves the given Songs list to the given path.
     *
     * @param songs - Songs object to save to file.
     * @throws IOException
     */
    public void save(List<Song> songs) throws IOException
    {
        //Write the given songs object to the give file.
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path)))
        {
            //Write song object to file.
            oos.writeObject(songs);
            oos.close();
        }
    }
}
