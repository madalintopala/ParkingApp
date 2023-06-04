package calitei.ParkingApp.services;


import calitei.ParkingApp.entities.User;
import calitei.ParkingApp.exceptions.UserAlreadyExistsException;
import calitei.ParkingApp.exceptions.UserNotFoundException;
import calitei.ParkingApp.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createUser(User user) throws UserAlreadyExistsException {
        if(userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException(String.format("User with email %s already exists!", user.getEmail()));
        }
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public List<User> getUsers(){
        return userRepository.findAll();
    }

    public User getUserByLastName(String lastName) throws UserNotFoundException {
        return userRepository.getUserByLastName(lastName).orElseThrow(() -> new UserNotFoundException("User not found!"));
    }

    public User getUserByEmail(String email) throws UserNotFoundException {
        return userRepository.getUserByEmail(email).orElseThrow(() ->
                new UserNotFoundException(String.format("User with email %s not found!", email)));
    }

    public User getUserById(Integer id){
        return userRepository.getReferenceById(id);
    }






}
