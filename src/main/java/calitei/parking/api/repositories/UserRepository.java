package calitei.parking.api.repositories;

import calitei.parking.api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    public Optional<User> getUserByLastName(String lastName);
    public Optional<User> findByEmail(String email);
    public Optional<User> getUserByPhoneNumber(String phoneNumber);
    public boolean existsByEmail(String email);
}
