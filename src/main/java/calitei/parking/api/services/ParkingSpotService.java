package calitei.parking.api.services;

import calitei.parking.api.config.JwtService;
import calitei.parking.api.entities.ParkingSpot;
import calitei.parking.api.entities.User;
import calitei.parking.api.error.ExceptionUtility;
import calitei.parking.api.error.exceptions.*;
import calitei.parking.api.models.parkingSpot.RequestParkingSpotUntil;
import calitei.parking.api.repositories.MethodType;
import calitei.parking.api.repositories.ParkingSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


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


    public void setFreeParkingSpot(int lotNumber) throws ParkingSpotNotFound, ParkingSpotException {
        ParkingSpot parkingSpot = getParkingSpotByLotNumber(lotNumber);
        if (!parkingSpot.isAdvertiseAsFreeByOwner()) {
            throw new ParkingSpotException(ExceptionUtility
                    .createErrorMessage("ParkingSpot", Integer.toString(parkingSpot.getLotNumber()),
                            "is reserved by owner, can not set it as free!"));

        }
        if (parkingSpot.isFreeToReserve()) {
            throw new ParkingSpotException(ExceptionUtility
                    .createErrorMessage("ParkingSpot", Integer.toString(parkingSpot.getLotNumber()),
                            "is already free!"));
        }
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(parkingSpot.getReservedBy() != null && !currentUser.getEmail().equals(parkingSpot.getReservedBy().getEmail())) {
            throw new ParkingSpotException(ExceptionUtility
                    .createErrorMessage("ParkingSpot", Integer.toString(parkingSpot.getLotNumber()),
                            "is not reserved by you!"));
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
            throw new AlreadyExistsException("ParkingSpot already has an owner");
        }
        parkingSpot.setFreeToReserve(false);
        parkingSpot.setFreeToReserveUntil(null);
        parkingSpot.setAdvertiseAsFreeByOwner(false);
        parkingSpot.setAdvertiseAsFreeUntil(null);
        parkingSpot.setReservedBy(owner);
        parkingSpot.setOwner(owner);
        parkingSpot.setOwnerName(owner.getFirstName() + " " + owner.getLastName());
        parkingSpotRepository.save(parkingSpot);
    }

    public void ownerOverride(int lotNumber) throws ParkingSpotNotFound, NotOwnerException {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ParkingSpot parkingSpot = getParkingSpotByLotNumber(lotNumber);
        if(ownerCheck(parkingSpot, currentUser)) {
            parkingSpot.setFreeToReserve(false);
            parkingSpot.setFreeToReserveUntil(null);
            parkingSpot.setAdvertiseAsFreeByOwner(false);
            parkingSpot.setAdvertiseAsFreeUntil(null);
            parkingSpot.setReservedBy(currentUser);
            parkingSpotRepository.save(parkingSpot);
        }
    }

    public void advertiseByOwner(RequestParkingSpotUntil requestParkingSpotUntil) throws ParkingSpotNotFound, NotOwnerException {

        User owner = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ParkingSpot parkingSpot = getParkingSpotByLotNumber(requestParkingSpotUntil.getLotNumber());
        if(ownerCheck(parkingSpot, owner)) {
            parkingSpot.setAdvertiseAsFreeByOwner(true);
            requestParkingSpotUntil.setUntilWhen();
            parkingSpot.setAdvertiseAsFreeUntil(requestParkingSpotUntil.getUntilWhen());
            parkingSpot.setFreeToReserve(true);
            parkingSpot.setFreeToReserveUntil(requestParkingSpotUntil.getUntilWhen());
            parkingSpot.setReservedBy(null);
            parkingSpot.setReservedByName(null);
            parkingSpotRepository.save(parkingSpot);
        }
    }

    public List<ParkingSpot> getFreeParkingSpots(){
        return parkingSpotRepository.getParkingSpotByFreeToReserve(true);
    }

    public boolean ownerCheck(ParkingSpot parkingSpot, User owner) throws NotOwnerException {
        if ( parkingSpot.getOwner() == null ) {
            throw new NotOwnerException(ExceptionUtility
                    .createErrorMessage("ParkingSpot", Integer.toString(parkingSpot.getLotNumber()),
                            "does not have an owner!"));
        }
        if ( !parkingSpot.getOwner().getEmail().equals(owner.getEmail())){
            throw new NotOwnerException(ExceptionUtility
                    .createErrorMessage("ParkingSpot", Integer.toString(parkingSpot.getLotNumber()),
                            "has a different owner!"));

        }
        return true;
    }
}
