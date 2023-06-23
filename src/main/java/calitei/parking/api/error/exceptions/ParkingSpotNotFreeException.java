package calitei.parking.api.error.exceptions;

public class ParkingSpotNotFreeException extends Exception{
    public ParkingSpotNotFreeException(String message) {
        super(message);
    }
}
