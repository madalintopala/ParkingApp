package calitei.parking.api.exceptions;

public class ParkingSpotNotFreeException extends Exception{
    public ParkingSpotNotFreeException(String message) {
        super(message);
    }
}
