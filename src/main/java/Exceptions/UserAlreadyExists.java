package Exceptions;

public class UserAlreadyExists extends Exception{

    /**
     * Constructor for UserAlreadyExists
     * @param message the message to be displayed
     */
    public UserAlreadyExists(String message){
        super(message);
        System.err.println(message);
    }
}
