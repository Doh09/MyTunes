/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI.Verifier;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * Verifier for verifiyng simple text field for being empty and son.
 * @author Dennis, Alxed, Niels and Tim.
 */
public class TextVerifier extends InputVerifier {

    @Override
    public boolean verify(JComponent input) {
        
        //Get text field.
        JTextField txtField = (JTextField) input;
        
        if (txtField.getText().trim().length() <= 0) {
            JOptionPane.showMessageDialog(input,
                    "This field cannot be empty !",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            txtField.requestFocus();;
            return false;
        }
        
        return true;
    }
}
