package calitei.parking.api.controllers;

import calitei.parking.api.entities.ParkingSpot;
import calitei.parking.api.entities.User;
import calitei.parking.api.error.exceptions.*;
import calitei.parking.api.models.parkingSpot.RequestParkingSpotUntil;
import calitei.parking.api.services.ParkingSpotService;
import calitei.parking.api.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    public List<ParkingSpot> getParkingSpots(){
        return parkingSpotService.getParkingSpots();
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

//     TODO: need to implement action to get the currently logged in details first
//    @PostMapping
//    @RequestMapping("/reserveExistingUser")
//    public void reserveParkingSpotExistingUser(@RequestBody RequestParkingSpotUntil requestParkingSpotUntil) throws
//            ParkingSpotNotFound, UserNotFoundException, ParkingSpotNotFreeException {
//
////        User reservee = get currently logged in user
////        parkingSpotService.reserveParkingSpot(reservee, requestParkingSpotUntil);
//
//    }



    @PostMapping
    @RequestMapping("/setFree")
    public void setFreeParkingSpot(@RequestBody int lotNumber) throws ParkingSpotNotFound {
        parkingSpotService.setFreeParkingSpot(lotNumber);
    }

    @PostMapping
    @RequestMapping("/advertiseByOwner")
    public void advertiseByOwner(@RequestBody RequestParkingSpotUntil requestParkingSpotUntil) throws ParkingSpotNotFound {
        parkingSpotService.advertiseByOwner(requestParkingSpotUntil);
    }

    @GetMapping
    @RequestMapping("/freeParkingSpots")
    public List<ParkingSpot> getFreeParkingSpots(){
        return parkingSpotService.getFreeParkingSpots();
    }
}
