/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL.PlaylistSearch;

/**
 * ISearchMatch interface, this interface is a interface over a SearchMatch.
 * @author Dennis, Alex, Niels and Tim.
 */
public interface ISearchMatch {
    
    /**
     * Matching the given value against the query.
     * @param value - Value to match with query.
     * @return True or false, if the match is correct.
     */
    public boolean match(String value);
}
