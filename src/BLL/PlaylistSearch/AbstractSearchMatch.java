/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL.PlaylistSearch;

/**
 * AbstractSearchMatch class, this class is designed to be inherited from and to
 * give the basic functionality of a SearchMatch.
 * @author Dennis, Alex, Niels and Tim.
 */
public abstract class AbstractSearchMatch implements ISearchMatch {
    
    /**
     * Instance variables.
     */
    protected String query;
    
    /**
     * Constructor.
     * @param query - Query to match against.
     */
    public AbstractSearchMatch(String query) {
        
        //Set default values of instance variables.
        this.query = query.toLowerCase(); //search without case sensitivity, value set to same in relevant class.
    }
    
    /**
     * Matching the given value against the query.
     * @param value - Value to match with query.
     * @return True or false, if the match is correct.
     */
    @Override
    public abstract boolean match(String value);
}
