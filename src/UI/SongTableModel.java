/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import BE.Song;
import BLL.Time;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * the model for the song table
 * @author Dennis, Alex, Niels and Tim.
 */
public class SongTableModel extends AbstractTableModel{
    
    /**
     * Static and finals.
     */
    private static final String[] HEADER =
    {
        "Title", "Artist", "Genre", "Length"
    };
    private static final Class[] COLUMN_TYPE =
    {
        String.class, String.class, String.class, Time.class
    };
    
    /**
     * Instance variables.
     */
    private List<Song> songs;

    /**
     * Constructor
     * @param songs 
     */
    public SongTableModel(List<Song> songs) {
        this.songs = songs;
        for (Song s : songs)
        {
            System.out.println(s.getPath());
        }
    }

    @Override
    public int getRowCount() {
        return songs.size();
    }

    @Override
    public int getColumnCount() {
        return HEADER.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        
        Song song = songs.get(rowIndex);
        
        if (song == null) return null;
  
        switch(columnIndex){
            case 0:
                return song.getTitle();                
            case 1:
                return song.getArtistName();
            case 2:
                return song.getGenre();
            case 3:
                return song.getTime().toString();
        }
        return null;
    }
    
    @Override
    public String getColumnName(int col){
        return HEADER[col];
    }

    @Override
    public Class<?> getColumnClass(int col){
        return COLUMN_TYPE[col];
    }

    /**
     * gets the index of a song and returns that song back
     * @param index
     * @return 
     */
    public Song getSong(int index) {
        return songs.get(index);
    }
    
    /**
     * gets a song and adds it to the list
     * @param song 
     */
    public void addSong(Song song){
        songs.add(song);
        fireTableDataChanged();
    }
    
    
    /**
     * Changes the list to show in the ui.
     * @param songs 
     */
    public void setList(List<Song> songs) {
        
        //Update the songs list and tell ui there is new data.
        this.songs = songs;
        fireTableDataChanged();
    }
    
    /**
     * Gets the list of the model.
     * @return the list.
     */
    public List<Song> getList() {
        return songs;
    }
}
