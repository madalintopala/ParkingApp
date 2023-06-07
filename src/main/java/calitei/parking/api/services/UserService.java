package calitei.parking.api.services;


import calitei.parking.api.entities.User;
import calitei.parking.api.exceptions.UserAlreadyExistsException;
import calitei.parking.api.exceptions.UserNotFoundException;
import calitei.parking.api.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;


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

    public User getUserByPhoneNumber(String phoneNumber) throws UserNotFoundException {
        return userRepository.getUserByPhoneNumber(phoneNumber).orElseThrow(() ->
                new UserNotFoundException(String.format("User with phone number %s not found!", phoneNumber)));
    }

    public User logInUserByEmailOrPhoneNumber(String emailOrPhoneNumber, String password) throws UserNotFoundException {
        Pattern phonePattern = Pattern.compile(
                "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"
                        + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$"
                        + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$");

        Pattern emailRegexPattern = Pattern.compile("^(.+)@(\\S+)$");

        User user = null;
        if (phonePattern.matcher(emailOrPhoneNumber).matches()) {
            user = userRepository.getUserByPhoneNumber(emailOrPhoneNumber).orElseThrow(() -> new UserNotFoundException("User not found!"));

        } else if (emailRegexPattern.matcher(emailOrPhoneNumber).matches()) {
            user = userRepository.getUserByEmail(emailOrPhoneNumber).orElseThrow(() -> new UserNotFoundException("User not found!"));
        }
        if (user.getPassword().equals(password)) {
            return user;
        }
        // TODO send error stating that email/phone number/password is invalid
        return null;
    }

    public User getUserById(Integer id){
        return userRepository.getReferenceById(id);
    }






}
