/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL.PlaylistSearch;

/**
 * AbstractSearchType class, this class is designed to be inherited from and to
 * give the basic functionality of a SearchType.
 * @author Dennis, Alex, Niels and Tim.
 */
public abstract class AbstractSearchType implements ISearchType {
    
    /**
     * Gets the string value from the given object, returns it.
     * @param obj - Object to get string value from.
     * @return the string value from the given object.
     */
    @Override
    public abstract String get(Object obj);
}
