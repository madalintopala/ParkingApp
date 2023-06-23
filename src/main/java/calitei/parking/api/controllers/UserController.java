package calitei.parking.api.controllers;

import calitei.parking.api.entities.User;
import calitei.parking.api.error.exceptions.UserAlreadyExistsException;
import calitei.parking.api.error.exceptions.UserNotFoundException;
import calitei.parking.api.models.user.LoginRequest;
import calitei.parking.api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600, methods = {RequestMethod.GET, RequestMethod.POST})
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/create")
    public void createUser(@RequestBody User user) throws UserAlreadyExistsException {
        userService.createUser(user);
    }

    @PostMapping("/login")
    public User logInUserByEmailOrPhoneNumber(@RequestBody LoginRequest loginRequest) throws UserNotFoundException {
        return userService.logInUserByEmailOrPhoneNumber(loginRequest.getEmailOrPhoneNumber(), loginRequest.getPassword());
    }

    @GetMapping("/getAll")
    public List<User> getUsers(){
        return userService.getUsers();
    }

    @GetMapping("/getByLastName/{lastName}")
    @ResponseBody
    public User getUserByLastName(@PathVariable String lastName) throws UserNotFoundException {
        return userService.getUserByLastName(lastName);
    }

    @GetMapping("/getById/{id}")
    @ResponseBody
    public User getUserById(@PathVariable String id){
        return userService.getUserById(Integer.parseInt(id));
    }
}
