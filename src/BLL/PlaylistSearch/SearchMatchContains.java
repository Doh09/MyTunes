/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL.PlaylistSearch;

/**
 * SearchMatchContains class. For more information look in 
 * AbstractSearchMatch class.
 * @author Dennis, Alex, Niels and Tim.
 */
public class SearchMatchContains extends AbstractSearchMatch {
    
    /**
     * Constructor.
     * @param query - Query to match against.
     */
    public SearchMatchContains(String query) {
        
        //Call parent constructor.
        super(query);
    }
    
    /**
     * For more information look in AbstractSearchMatch class. 
     */
    @Override
    public boolean match(String value) {
        value = value.toLowerCase(); //search without case sensitivity, query set to same in relevant class.
        if (value.equals(query)) return true;
        if (value.contains(query)) return true;
        return false;
    }
}
