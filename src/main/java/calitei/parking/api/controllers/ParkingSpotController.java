package calitei.parking.api.controllers;

import calitei.parking.api.entities.ParkingSpot;
import calitei.parking.api.entities.User;
import calitei.parking.api.error.exceptions.*;
import calitei.parking.api.models.parkingSpot.RequestParkingSpotUntil;
import calitei.parking.api.services.ParkingSpotService;
import calitei.parking.api.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600, methods = {RequestMethod.GET, RequestMethod.POST})
@RequestMapping("/parkingspot")
public class ParkingSpotController {
    private final ParkingSpotService parkingSpotService;
    private final UserService userService;

    public ParkingSpotController(ParkingSpotService parkingSpotService, UserService userService) {
        this.parkingSpotService = parkingSpotService;
        this.userService = userService;
    }



    @GetMapping
    @RequestMapping("/all")
    public List<ParkingSpot> getAllParkingSpots(){
        return parkingSpotService.getAllParkingSpots();
    }

    @GetMapping
    @RequestMapping("/{lotNumber}")
    public ParkingSpot getParkingSpotByLotNumber(@PathVariable int lotNumber) throws ParkingSpotNotFound {
        return parkingSpotService.getParkingSpotByLotNumber(lotNumber);
    }

    @PostMapping
    @RequestMapping("/create")
    public void createParkingSpot(@RequestBody ParkingSpot parkingSpot) throws AlreadyExistsException {
        parkingSpotService.createParkingSpot(parkingSpot);
    }


    @PostMapping
    @RequestMapping("/setOwner/{lotNumber}")
    public void setOwner(@RequestBody String email, @PathVariable int lotNumber) throws ParkingSpotNotFound, UserNotFoundException, JsonProcessingException, AlreadyExistsException {
        User owner = userService.getUserByEmail(email);
        parkingSpotService.setOwner(owner, lotNumber);
    }


    @PostMapping
    @RequestMapping("/reserveExistingUser")
    public void reserveParkingSpotExistingUser(@RequestBody RequestParkingSpotUntil requestParkingSpotUntil) throws
            ParkingSpotNotFound, UserNotFoundException, ParkingSpotNotFreeException {
        parkingSpotService.reserveParkingSpot(requestParkingSpotUntil);

    }



    @PostMapping
    @RequestMapping("/setFree")
    public void setFreeParkingSpot(@RequestBody int lotNumber) throws ParkingSpotNotFound, ParkingSpotException {
        parkingSpotService.setFreeParkingSpot(lotNumber);
    }

    @PostMapping
    @RequestMapping("/advertiseByOwner")
    public void advertiseByOwner(@RequestBody RequestParkingSpotUntil requestParkingSpotUntil) throws ParkingSpotNotFound, NotOwnerException {
        parkingSpotService.advertiseByOwner(requestParkingSpotUntil);
    }

    @GetMapping
    @RequestMapping("/freeParkingSpots")
    public List<ParkingSpot> getFreeParkingSpots(){
        return parkingSpotService.getFreeParkingSpots();
    }

    @PostMapping
    @RequestMapping("/takeBackByOwner")
    public void takeBackByOwner(@RequestBody int lotNumber) throws ParkingSpotNotFound, NotOwnerException {
        parkingSpotService.ownerOverride(lotNumber);
    }
}
