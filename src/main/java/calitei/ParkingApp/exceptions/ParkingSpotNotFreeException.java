package calitei.ParkingApp.exceptions;

public class ParkingSpotNotFreeException extends Exception{
    public ParkingSpotNotFreeException(String message) {
        super(message);
    }
}
