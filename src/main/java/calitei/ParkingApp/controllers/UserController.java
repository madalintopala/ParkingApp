package calitei.ParkingApp.controllers;

import calitei.ParkingApp.entities.User;
import calitei.ParkingApp.exceptions.UserAlreadyExistsException;
import calitei.ParkingApp.exceptions.UserNotFoundException;
import calitei.ParkingApp.services.UserService;
import jakarta.websocket.server.PathParam;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public void createUser(@RequestBody User user) throws UserAlreadyExistsException {
        userService.createUser(user);
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
