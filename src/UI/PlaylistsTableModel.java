/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import BE.Playlist;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @authors Dennis, Niels, Alex and Tim.
 */
public class PlaylistsTableModel extends AbstractTableModel
{
     private static final String[] HEADER =
    {
        "Name", "Songs", "Time"
    };
     private static final Class[] COLUMN_TYPE =
     {
         String.class, Integer.class, String.class
     };
     
     private List<Playlist> playlists;

    public PlaylistsTableModel(List<Playlist> playlist)
    {
        this.playlists = playlist;
    }
     
     

    @Override
    public int getRowCount()
    {
       return playlists.size();
    }

    @Override
    public int getColumnCount()
    {
        return HEADER.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        Playlist playlist = playlists.get(rowIndex);
        switch(columnIndex){
            
             case 0: return playlist.getName();
              
             case 1: return playlist.getSongList().size();
               
             case 2: return playlist.getTime();
          
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
    public Playlist getPlaylist(int index) {
        return playlists.get(index);
    }
    
    /**
     * Changes the list to show in the ui.
     * @param playlists 
     */
    public void setList(List<Playlist> playlists) {
        
        //Update the songs list and tell ui there is new data.
        this.playlists = playlists;
        fireTableDataChanged();
    }
   
}
