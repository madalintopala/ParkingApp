package calitei.ParkingApp.repositories;

import calitei.ParkingApp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    public Optional<User> getUserByLastName(String lastName);
    public Optional<User> getUserByEmail(String email);
    public boolean existsByEmail(String email);
}
