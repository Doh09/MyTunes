/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @authors Dennis, Niels, Alex and Tim.
 */
public class SongFileFilter extends FileFilter {
    
    /**
     * Instance variables.
     */
    private String[] extensions;
    private String description;
    
    /**
     * Constructor.
     * @param extensions - Extensions to support.
     * @param description - Description of extensions.
     */
    public SongFileFilter(String[] extensions, String description) {
        
        //Set default value of instance variables.
        this.extensions = extensions;
        this.description = description;
    }

    @Override
    public boolean accept(File f) {
        if (f.isDirectory())
            return true;
        for (String extension : extensions)
            if (f.getName().toLowerCase().endsWith(extension))
                return true;
        return false;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
