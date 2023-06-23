package calitei.parking.api.error.exceptions;

public class UserAlreadyExistsException extends AlreadyExistsException{

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
