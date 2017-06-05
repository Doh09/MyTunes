/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL.PlaylistSearch;

/**
 * ISearchType interface, this interface is a interface over a SearchType.
 * @author Dennis, Alex, Niels and Tim.
 */
public interface ISearchType {
    
    /**
     * Gets the string value from the given object, returns it.
     * @param obj - Object to get string value from.
     * @return the string value from the given object.
     */
    public String get(Object obj);
}
