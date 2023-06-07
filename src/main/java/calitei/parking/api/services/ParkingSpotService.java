package calitei.parking.api.services;

import calitei.parking.api.entities.ParkingSpot;
import calitei.parking.api.entities.User;
import calitei.parking.api.exceptions.ParkingSpotNotFound;
import calitei.parking.api.exceptions.ParkingSpotNotFreeException;
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

    public void createParkingSpot(ParkingSpot parkingSpot){
        parkingSpotRepository.save(parkingSpot);
    }


    public void setFreeParkingSpot(ParkingSpot parkingSpot, LocalDateTime freeUntil) throws ParkingSpotNotFound {

        // TODO validate that the user who sets free this lot is the one who reserved it or the owner
        // Principal currentUser = request.getUserPrincipal();  RequestService request to be inserted into controller and service method
        // if(currentUser.getId() == parkingSpot.getOwner().getId()) { ownerOverride(parkingSpot) }else{
        // if(!currentUser.getId() == parkingSpot.getReservedBy().getId()) { //you are not the one who reserved the lot}

        if (parkingSpot.isFreeToReserve()) {
            // parking lot is already free
        } else {
            parkingSpot.setFreeToReserve(true);
            parkingSpot.setFreeToReserveUntil(freeUntil);
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
                .orElseThrow(()-> new ParkingSpotNotFound("ParkingSpot with id: "+ lotNumber +" was not found!"));
    }

    public void reserveParkingSpot(User reservee, int lotNumber, LocalDateTime reservedUntil) throws ParkingSpotNotFound, ParkingSpotNotFreeException {
        ParkingSpot parkingSpot = getParkingSpotByLotNumber(lotNumber);
        if(!parkingSpot.isFreeToReserve()) {
            throw new ParkingSpotNotFreeException(
                    String.format("The parking spot with lot number %s is not free!", parkingSpot.getLotNumber()));
        }
        parkingSpot.setReservedBy(reservee);
        parkingSpot.setFreeToReserve(false);
        parkingSpot.setFreeToReserveUntil(reservedUntil);
        parkingSpot.setReservedByName(reservee.getFirstName() + " " + reservee.getLastName());
        parkingSpotRepository.save(parkingSpot);
    }

    public void setOwner(User owner, int lotNumber) throws ParkingSpotNotFound {
        ParkingSpot parkingSpot = getParkingSpotByLotNumber(lotNumber);
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

    public void advertiseByOwner(ParkingSpot parkingSpot, LocalDateTime freeUntil, boolean advertiseByOwner){
        // TODO validate that the user who advertises as free is the owner
        // Principal currentUser = request.getUserPrincipal();  RequestService request to be inserted into controller and service method
        // if(!(currentUser.getId() == parkingSpot.getOwner().getId())) { //you are not the owner }else{
        parkingSpot.setAdvertiseAsFreeByOwner(advertiseByOwner);
        parkingSpot.setAdvertiseAsFreeUntil(freeUntil);
        parkingSpot.setFreeToReserve(advertiseByOwner);
        parkingSpot.setFreeToReserveUntil(freeUntil);
        parkingSpot.setReservedBy(null);
        parkingSpot.setReservedByName(null);
        parkingSpotRepository.save(parkingSpot);
    }

    public List<ParkingSpot> getFreeParkingSpots(){
        return parkingSpotRepository.getParkingSpotByFreeToReserve(true);
    }
}
