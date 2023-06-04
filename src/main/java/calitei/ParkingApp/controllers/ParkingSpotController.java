package calitei.ParkingApp.controllers;

import calitei.ParkingApp.entities.ParkingSpot;
import calitei.ParkingApp.entities.User;
import calitei.ParkingApp.exceptions.ParkingSpotNotFound;
import calitei.ParkingApp.exceptions.ParkingSpotNotFreeException;
import calitei.ParkingApp.exceptions.UserAlreadyExistsException;
import calitei.ParkingApp.exceptions.UserNotFoundException;
import calitei.ParkingApp.services.ParkingSpotService;
import calitei.ParkingApp.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
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
    public void createParkingSpot(@RequestBody ParkingSpot parkingSpot){
        parkingSpotService.createParkingSpot(parkingSpot);
    }


    @PostMapping
    @RequestMapping("/setOwner")
    public void setOwner(@RequestBody String jsonString) throws ParkingSpotNotFound, UserNotFoundException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonString);
        User owner = userService.getUserByEmail(jsonNode.get("email").asText());
        parkingSpotService.setOwner(owner, jsonNode.get("lotNumber").asInt());
    }

    @PostMapping
    @RequestMapping("/reserveExistingUser")
    public void reserveParkingSpotExistingUser(@RequestBody String jsonString) throws
            ParkingSpotNotFound, JsonProcessingException, UserNotFoundException, ParkingSpotNotFreeException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonString);
        String email = jsonNode.get("email").asText();
        int lotNumber = jsonNode.get("lotNumber").asInt();
        LocalDateTime reservedUntil = LocalDateTime.of(jsonNode.get("year").asInt(),
                jsonNode.get("month").asInt(),
                jsonNode.get("day").asInt(),
                jsonNode.get("hour").asInt(),
                jsonNode.get("minute").asInt(),
                jsonNode.get("seconds").asInt());
        User reservee = userService.getUserByEmail(email);
        parkingSpotService.reserveParkingSpot(reservee, lotNumber, reservedUntil);

    }

    @Transactional // if the parkingSpot is not successfully reserved, delete newly created user
    @PostMapping
    @RequestMapping("/reserveNewUser")
    public void reserveParkingSpotNewUser(@RequestBody String jsonString) throws
            ParkingSpotNotFound, JsonProcessingException, UserAlreadyExistsException, ParkingSpotNotFreeException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonString);
        User reservee = new User();
        reservee.setFirstName(jsonNode.get("firstName").asText());
        reservee.setLastName(jsonNode.get("lastName").asText());
        reservee.setEmail(jsonNode.get("email").asText());
        reservee.setPassword(jsonNode.get("password").asText());
        reservee.setPhoneNumber(jsonNode.get("phoneNumber").asText());
        int lotNumber = jsonNode.get("lotNumber").asInt();
        LocalDateTime reservedUntil = LocalDateTime.of(jsonNode.get("year").asInt(),
                jsonNode.get("month").asInt(),
                jsonNode.get("day").asInt(),
                jsonNode.get("hour").asInt(),
                jsonNode.get("minute").asInt(),
                jsonNode.get("seconds").asInt());
        userService.createUser(reservee);
        parkingSpotService.reserveParkingSpot(reservee, lotNumber, reservedUntil);

    }

    @PostMapping
    @RequestMapping("/setFree")
    public void setFreeParkingSpot(@RequestBody String jsonString) throws JsonProcessingException, ParkingSpotNotFound {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonString);
        int lotNumber = jsonNode.get("lotNumber").asInt();
        ParkingSpot parkingSpot = getParkingSpotByLotNumber(lotNumber);
        LocalDateTime freeUntil = null;
        try{
            freeUntil = LocalDateTime.of(jsonNode.get("year").asInt(),
                    jsonNode.get("month").asInt(),
                    jsonNode.get("day").asInt(),
                    jsonNode.get("hour").asInt(),
                    jsonNode.get("minute").asInt(),
                    jsonNode.get("seconds").asInt());
        }catch(NullPointerException e){
            freeUntil = parkingSpot.getAdvertiseAsFreeUntil();
        }

        parkingSpotService.setFreeParkingSpot(parkingSpot, freeUntil);
    }

    @PostMapping
    @RequestMapping("/advertiseByOwner")
    public void advertiseByOwner(@RequestBody String jsonString) throws JsonProcessingException, ParkingSpotNotFound {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonString);
        boolean advertisedAsFree = jsonNode.get("advertisedAsFree").asBoolean();
        int lotNumber = jsonNode.get("lotNumber").asInt();
        ParkingSpot parkingSpot = getParkingSpotByLotNumber(lotNumber);
        LocalDateTime freeUntil = null;
        if (advertisedAsFree) {
            freeUntil = LocalDateTime.of(jsonNode.get("year").asInt(),
                    jsonNode.get("month").asInt(),
                    jsonNode.get("day").asInt(),
                    jsonNode.get("hour").asInt(),
                    jsonNode.get("minute").asInt(),
                    jsonNode.get("seconds").asInt());
        }
        parkingSpotService.advertiseByOwner(parkingSpot, freeUntil, advertisedAsFree);
    }
}
