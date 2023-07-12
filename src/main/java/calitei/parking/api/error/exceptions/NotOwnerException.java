package calitei.parking.api.error.exceptions;

public class NotOwnerException extends Exception{

    public NotOwnerException(String message){
        super(message);
    }
}
