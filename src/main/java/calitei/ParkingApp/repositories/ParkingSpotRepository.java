package calitei.ParkingApp.repositories;

import calitei.ParkingApp.entities.ParkingSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Integer> {

    public Optional<ParkingSpot> findByLotNumber(int lotNumber);
}
