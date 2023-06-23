package calitei.parking.api.services;

import calitei.parking.api.entities.ParkingSpot;
import calitei.parking.api.entities.User;
import calitei.parking.api.error.ExceptionUtility;
import calitei.parking.api.error.exceptions.AlreadyExistsException;
import calitei.parking.api.error.exceptions.ParkingSpotNotFound;
import calitei.parking.api.error.exceptions.ParkingSpotNotFreeException;
import calitei.parking.api.models.parkingSpot.RequestParkingSpotUntil;
import calitei.parking.api.repositories.MethodType;
import calitei.parking.api.repositories.ParkingSpotRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ParkingSpotService {
    private final ParkingSpotRepository parkingSpotRepository;


    public ParkingSpotService(ParkingSpotRepository parkingSpotRepository) {
        this.parkingSpotRepository = parkingSpotRepository;

    }

    public void createParkingSpot(ParkingSpot parkingSpot) throws AlreadyExistsException {
        if(parkingSpotRepository.existsByLotNumber(parkingSpot.getLotNumber())){
            throw new AlreadyExistsException(ExceptionUtility
                    .createErrorMessage("ParkingSpot", Integer.toString(parkingSpot.getLotNumber()), MethodType.CREATE));
        }
        parkingSpotRepository.save(parkingSpot);
    }


    public void setFreeParkingSpot(int lotNumber) throws ParkingSpotNotFound {

        // TODO validate that the user who sets free this lot is the one who reserved it or the owner
        // Principal currentUser = request.getUserPrincipal();  RequestService request to be inserted into controller and service method
        // if(currentUser.getId() == parkingSpot.getOwner().getId()) { ownerOverride(parkingSpot) }else{
        // if(!currentUser.getId() == parkingSpot.getReservedBy().getId()) { //you are not the one who reserved the lot}
        ParkingSpot parkingSpot = getParkingSpotByLotNumber(lotNumber);
        if (parkingSpot.isFreeToReserve()) {
            // parking lot is already free
        } else {
            parkingSpot.setFreeToReserve(true);
            parkingSpot.setFreeToReserveUntil(parkingSpot.getAdvertiseAsFreeUntil());
            parkingSpot.setReservedBy(null);
            parkingSpot.setReservedByName(null);
            parkingSpotRepository.save(parkingSpot);
        }
        //    }


    }

    public List<ParkingSpot> getParkingSpots(){
        return parkingSpotRepository.findAll();
    }

    public ParkingSpot getParkingSpotByLotNumber(int lotNumber) throws ParkingSpotNotFound {
        return parkingSpotRepository.findByLotNumber(lotNumber)
                .orElseThrow(()-> new ParkingSpotNotFound(ExceptionUtility
                        .createErrorMessage("ParkingSpot", Integer.toString(lotNumber), MethodType.UPDATE)));
    }

    public void reserveParkingSpot(User reservee, RequestParkingSpotUntil requestParkingSpotUntil) throws ParkingSpotNotFound, ParkingSpotNotFreeException {
        ParkingSpot parkingSpot = getParkingSpotByLotNumber(requestParkingSpotUntil.getLotNumber());
        requestParkingSpotUntil.setUntilWhen();
        if(!parkingSpot.isFreeToReserve()) {
            throw new ParkingSpotNotFreeException(
                    String.format("The parking spot with lot number %s is not free!", parkingSpot.getLotNumber()));
        }
        parkingSpot.setReservedBy(reservee);
        parkingSpot.setFreeToReserve(false);
        parkingSpot.setFreeToReserveUntil(requestParkingSpotUntil.getUntilWhen());
        parkingSpot.setReservedByName(reservee.getFirstName() + " " + reservee.getLastName());
        parkingSpotRepository.save(parkingSpot);
    }

    public void setOwner(User owner, int lotNumber) throws ParkingSpotNotFound, AlreadyExistsException {
        ParkingSpot parkingSpot = getParkingSpotByLotNumber(lotNumber);
        if(parkingSpot.getOwner() != null) {
            throw new AlreadyExistsException("ParkingSpot already has owner");
        }
        parkingSpot.setFreeToReserve(false);
        parkingSpot.setFreeToReserveUntil(null);
        parkingSpot.setAdvertiseAsFreeByOwner(false);
        parkingSpot.setAdvertiseAsFreeUntil(null);
        parkingSpot.setOwner(owner);
        parkingSpot.setOwnerName(owner.getFirstName() + " " + owner.getLastName());
        parkingSpotRepository.save(parkingSpot);
    }

    public void ownerOverride(ParkingSpot parkingSpot){
        parkingSpot.setFreeToReserve(false);
        parkingSpot.setFreeToReserveUntil(null);
        parkingSpot.setAdvertiseAsFreeByOwner(false);
        parkingSpot.setAdvertiseAsFreeUntil(null);
        parkingSpotRepository.save(parkingSpot);
    }

    public void advertiseByOwner(RequestParkingSpotUntil requestParkingSpotUntil) throws ParkingSpotNotFound {
        // TODO validate that the user who advertises as free is the owner
        // Principal currentUser = request.getUserPrincipal();  RequestService request to be inserted into controller and service method
        // if(!(currentUser.getId() == parkingSpot.getOwner().getId())) { //you are not the owner }else{
        ParkingSpot parkingSpot = getParkingSpotByLotNumber(requestParkingSpotUntil.getLotNumber());
        parkingSpot.setAdvertiseAsFreeByOwner(true);
        requestParkingSpotUntil.setUntilWhen();
        parkingSpot.setAdvertiseAsFreeUntil(requestParkingSpotUntil.getUntilWhen());
        parkingSpot.setFreeToReserve(true);
        parkingSpot.setFreeToReserveUntil(requestParkingSpotUntil.getUntilWhen());
        parkingSpot.setReservedBy(null);
        parkingSpot.setReservedByName(null);
        parkingSpotRepository.save(parkingSpot);
    }

    public List<ParkingSpot> getFreeParkingSpots(){
        return parkingSpotRepository.getParkingSpotByFreeToReserve(true);
    }
}
