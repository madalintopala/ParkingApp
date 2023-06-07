package calitei.ParkingApp.controllers;

import calitei.ParkingApp.entities.User;
import calitei.ParkingApp.exceptions.UserNotFoundException;
import calitei.ParkingApp.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/main")
public class MainController {
    private final UserService userService;

    public MainController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User logInUserByEmailOrPhoneNumber(@RequestBody String jsonString) throws UserNotFoundException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonString);
        String emailOrPhoneNumber = jsonNode.get("emailOrPhoneNumber").asText();
        String password = jsonNode.get("password").asText();
        return userService.logInUserByEmailOrPhoneNumber(emailOrPhoneNumber, password);
    }
}
