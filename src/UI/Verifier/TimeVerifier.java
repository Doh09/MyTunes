/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI.Verifier;


import BLL.MyTunesException;
import BLL.Time;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * Verifier for verifying the input to make sure it is in the time format.
 * mm:ss
 * @author Dennis, Alex, Niels and Tim.
 */
public class TimeVerifier extends InputVerifier {

    @Override
    public boolean verify(JComponent input) {
        
        //Get text field.
        JTextField txtField = (JTextField) input;
        
        try {
            
            //Get time from string.
            Time time = new Time(txtField.getText());
            
        } catch (MyTunesException e) {
            JOptionPane.showMessageDialog(input,
                    "Please type in a valid time format: hh:mm:ss",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            txtField.requestFocus();
            return false;
        }
        
        return true;
    }
}
