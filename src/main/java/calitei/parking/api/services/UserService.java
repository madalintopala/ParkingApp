package calitei.parking.api.services;


import calitei.parking.api.entities.User;
import calitei.parking.api.error.ExceptionUtility;
import calitei.parking.api.error.exceptions.UserAlreadyExistsException;
import calitei.parking.api.error.exceptions.UserNotFoundException;
import calitei.parking.api.repositories.MethodType;
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
            throw new UserAlreadyExistsException(ExceptionUtility
                    .createErrorMessage("User", user.getEmail(), MethodType.CREATE));
        }
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public List<User> getUsers(){
        return userRepository.findAll();
    }

    public User getUserByLastName(String lastName) throws UserNotFoundException {
        return userRepository.getUserByLastName(lastName)
                .orElseThrow(() -> new UserNotFoundException(ExceptionUtility
                        .createErrorMessage("User", lastName, MethodType.UPDATE)));
    }

    public User getUserByEmail(String email) throws UserNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException(ExceptionUtility.createErrorMessage("User", email, MethodType.UPDATE)));
    }

    public User getUserByPhoneNumber(String phoneNumber) throws UserNotFoundException {
        return userRepository.getUserByPhoneNumber(phoneNumber).orElseThrow(() ->
                new UserNotFoundException(ExceptionUtility.createErrorMessage("User", phoneNumber, MethodType.UPDATE)));
    }

    public User logInUserByEmailOrPhoneNumber(String emailOrPhoneNumber, String password) throws UserNotFoundException {
        Pattern phonePattern = Pattern.compile(
                "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"
                        + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$"
                        + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$");

        Pattern emailRegexPattern = Pattern.compile("^(.+)@(\\S+)$");

        User user = null;
        if (phonePattern.matcher(emailOrPhoneNumber).matches()) {
            user = userRepository.getUserByPhoneNumber(emailOrPhoneNumber)
                    .orElseThrow(() -> new UserNotFoundException(ExceptionUtility
                            .createErrorMessage("User", emailOrPhoneNumber, MethodType.UPDATE)));

        } else if (emailRegexPattern.matcher(emailOrPhoneNumber).matches()) {
            user = userRepository.findByEmail(emailOrPhoneNumber)
                    .orElseThrow(() -> new UserNotFoundException(ExceptionUtility
                    .createErrorMessage("User", emailOrPhoneNumber, MethodType.UPDATE)));
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
