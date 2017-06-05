/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import BE.Song;
import BLL.Time;
import UI.DragAndDrop.DragAndDropEvent;
import UI.DragAndDrop.Reorderable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @authors Dennis, Niels, Alex and Tim.
 */
public class PlaylistTableModel extends AbstractTableModel implements Reorderable
{
    /**
     * Static and finals.
     */
    private static final String[] HEADER =
    {
        "Number","Title", "Artist", "Genre", "Length"
    };
    
    private static final Class[] COLUMN_TYPE =
    {
        String.class, String.class, String.class, String.class, Time.class
    };
    
    /**
     * Instance variables.
     */
    private List<Song> songs;
    private List<DragAndDropEvent>  listeners;
    
    public PlaylistTableModel(List<Song> songs) {
        this.songs = songs;
        this.listeners = new ArrayList<>();
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
                return String.valueOf(rowIndex + 1);
            case 1:
                return song.getTitle();                
            case 2:
                return song.getArtistName();
            case 3:
                return song.getGenre();
            case 4:
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
     * this method gets called, by the drag and drop system 
     * @param fromIndex
     * @param toIndex
     */
    @Override
    public void reorder(int fromIndex, int toIndex)
    {
        toIndex--;
        List<Song> listx = new ArrayList<>();
        if(toIndex == -1)
        {
            listx.add(songs.get(fromIndex));
           
            
        }
        for(int i = 0; i < songs.size(); i++)
        {
            if(i != fromIndex)
            {
                listx.add(songs.get(i));
            }
            if(i == toIndex)
            {
                listx.add(songs.get(fromIndex));
            }
        }
        songs = listx;
        for (DragAndDropEvent e : listeners)
        {
            e.onDrag();
        }
        
    }
    /**
     * adds a drag and drop event to the arraylist "listners"
     * @param e 
     */
    public void addDragAndDropListener(DragAndDropEvent e)
    {
        listeners.add(e);
    }
    /**
     * returns the arraylist songs
     * @return 
     */
    public List<Song> getList()
    {
        return songs;
    }
}
