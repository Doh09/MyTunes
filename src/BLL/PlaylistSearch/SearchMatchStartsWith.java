/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL.PlaylistSearch;

/**
 * SearchMatchStartsWith class. For more information look in 
 * AbstractSearchMatch class.
 * @author Dennis, Alex, Niels and Tim.
 */
public class SearchMatchStartsWith extends AbstractSearchMatch {
    
    /**
     * Constructor.
     * @param query - Query to match against.
     */
    public SearchMatchStartsWith(String query) {
        
        //Call parent constructor.
        super(query);
    }
    
    @Override
    public boolean match(String value) {
        return value.startsWith(query);
    }
}
