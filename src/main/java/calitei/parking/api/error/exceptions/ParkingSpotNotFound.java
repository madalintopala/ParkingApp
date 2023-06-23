package calitei.parking.api.error.exceptions;

public class ParkingSpotNotFound extends NotFoundException {

    public ParkingSpotNotFound(String message) {
        super(message);
    }
}
