/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL.PlaylistSearch;

import BE.Song;

/**
 * SearchTypeTitle class. For more information look in the
 * AbstractSearchType class.
 * @author Dennis, Alex, Niels and Tim.
 */
public class SearchTypeTitle extends AbstractSearchType {
    
    /**
     * Constructor.
     */
    public SearchTypeTitle() {
        
        //Call parent constructor.
        super();
    }
    
    /**
     * For more information look in the AbstractSearchType class.
     */
    @Override
    public String get(Object obj) {
        
        if (obj instanceof Song)
            return ((Song)obj).getTitle();
        else
            return "";
    }
}
