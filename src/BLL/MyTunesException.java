/*
 * our exception for mytunes
 */
package BLL;

/**
 *
 * @author Denis, Niels, Alex and Tim.
 */
public class MyTunesException extends RuntimeException
{
    /**
     * Constructor.
     * @param message - Message to exception.
     */
    public MyTunesException(String message)
    {
        super(message);
    }
    
}
