/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL.PlaylistSearch;

import BE.Playlist;
import BE.Song;
import java.util.ArrayList;
import java.util.List;

/**
 * PlaylistSearch class, this class is designed to search through a playlist
 * there are containing songs.
 * @author Dennis, Alex, Niels and Tim.
 */
public class PlaylistSearch {
    
    /**
     * Static and finals.
     */
    private static PlaylistSearch instance;
    
    /**
     * Constructor.
     */
    private PlaylistSearch() {}
    
    /**
     * Gets the instance of PlaylistSearch singletone.
     */
    public static PlaylistSearch getInstance() {
        if (instance == null)
            instance = new PlaylistSearch();
        return instance;
    }
    
    /**
     * Searches through the playlist and collects songs there is matching the
     * search query.
     * @param playlist - Playlist to search through.
     * @param searchType - SearchType to use in the search.
     * @param matcher - SearchMatch to tuse in the search.
     * @return a list of songs, which match the query.
     */
    public List<Song> search(Playlist playlist, ISearchType searchType, ISearchMatch matcher) {
        
        //Temporary list for the result.
        List<Song> result = new ArrayList<>();
        
        //Loop through each song in the playlist.
        for (Song song : playlist.getSongList()) {
            
            //Get the search type value from the song.
            String searchTypeValue = searchType.get(song);
            
            //Check if searchTypeValue equals the search query from the matcher.
            if (matcher.match(searchTypeValue)) {
                
                //Add song to result if not already added.
                if (!result.contains(song)) {
                    
                    result.add(song);
                }
            }
        }
        
        return result;
    }
    
    /**
     * Searches through the playlist and collects songs there is matching the
     * search query.
     * @param playlist - Playlist to search through.
     * @param searchType - SearchType to use in the search.
     * @param matchers - Multiple Matchers to use in search.
     * @return a list of songs, which match the query.
     */
    public List<Song> search(Playlist playlist, ISearchType searchType, ISearchMatch... matchers) {
        
        //List of songs which matched the given query.
        List<Song> result = new ArrayList<>();
        
        //Loop throug each matcher.
        for (ISearchMatch matcher : matchers) {
            
            //Search with the current matcher.
            List<Song> searchResult = search(playlist, searchType, matcher);
            
            //Loop through each song in the search result list to add to result
            //list.
            for (Song song : searchResult) {
                
                //Add song if do not already exist.
                if (!result.contains(song)) {
                    
                    result.add(song);
                }
            }
        }
        
        return result;
    }

    /**
     * mixes two lists togheter so one song cant appear twice.
     * @param list1
     * @param list2
     * @return
     */
    public List<Song> mixList(List<Song> list1, List<Song> list2)
    {
        List<Song> results = new ArrayList(list1);
        for(Song s: list2)
        {
            if(!results.contains(s))
            {
                results.add(s);
            }
        }
        return results;
    }
}
