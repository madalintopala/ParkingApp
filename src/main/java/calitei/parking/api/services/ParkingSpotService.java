package calitei.parking.api.services;

import calitei.parking.api.config.JwtService;
import calitei.parking.api.entities.ParkingSpot;
import calitei.parking.api.entities.User;
import calitei.parking.api.error.ExceptionUtility;
import calitei.parking.api.error.exceptions.AlreadyExistsException;
import calitei.parking.api.error.exceptions.ParkingSpotNotFound;
import calitei.parking.api.error.exceptions.ParkingSpotNotFreeException;
import calitei.parking.api.models.parkingSpot.RequestParkingSpotUntil;
import calitei.parking.api.repositories.MethodType;
import calitei.parking.api.repositories.ParkingSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingSpotService {
    private final ParkingSpotRepository parkingSpotRepository;
    private final JwtService jwtService;

    public void createParkingSpot(ParkingSpot parkingSpot) throws AlreadyExistsException {
        if(parkingSpotRepository.existsByLotNumber(parkingSpot.getLotNumber())){
            throw new AlreadyExistsException(ExceptionUtility
                    .createErrorMessage("ParkingSpot", Integer.toString(parkingSpot.getLotNumber()), MethodType.CREATE));
        }
        parkingSpotRepository.save(parkingSpot);


    }


    public void setFreeParkingSpot(int lotNumber) throws ParkingSpotNotFound {
        ParkingSpot parkingSpot = getParkingSpotByLotNumber(lotNumber);
        if (!parkingSpot.isAdvertiseAsFreeByOwner()) {
            // parking lot is reserved by owner, can not set it as free.
            return;
        }
        if (parkingSpot.isFreeToReserve()) {
            // parking lot is already free
            return;
        }
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(parkingSpot.getOwner() != null && currentUser.getId() == parkingSpot.getOwner().getId()) {
            ownerOverride(parkingSpot);
            //TODO send email/sms to reservee that the parking spot has been taken back by owner
            return;
        }
        if(parkingSpot.getReservedBy() !=null && currentUser.getId() != parkingSpot.getReservedBy().getId()) {
            //this lot is not reserved by you
            return;
        }
        parkingSpot.setFreeToReserve(true);
        parkingSpot.setFreeToReserveUntil(parkingSpot.getAdvertiseAsFreeUntil());
        parkingSpot.setReservedBy(null);
        parkingSpot.setReservedByName(null);
        parkingSpotRepository.save(parkingSpot);
    }





    public List<ParkingSpot> getAllParkingSpots(){
        return parkingSpotRepository.findAll();
    }

    public ParkingSpot getParkingSpotByLotNumber(int lotNumber) throws ParkingSpotNotFound {
        return parkingSpotRepository.findByLotNumber(lotNumber)
                .orElseThrow(()-> new ParkingSpotNotFound(ExceptionUtility
                        .createErrorMessage("ParkingSpot", Integer.toString(lotNumber), MethodType.UPDATE)));
    }

    public void reserveParkingSpot(RequestParkingSpotUntil requestParkingSpotUntil) throws ParkingSpotNotFound, ParkingSpotNotFreeException {
        ParkingSpot parkingSpot = getParkingSpotByLotNumber(requestParkingSpotUntil.getLotNumber());
        User reservee = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        requestParkingSpotUntil.setUntilWhen();
        if(!parkingSpot.isFreeToReserve()) {
            throw new ParkingSpotNotFreeException(ExceptionUtility
                    .createErrorMessage("ParkingSpot ", Integer.toString(parkingSpot.getLotNumber()), "not free"));
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
