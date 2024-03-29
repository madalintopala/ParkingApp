package calitei.parking.api.repositories;

import calitei.parking.api.entities.ParkingSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Integer> {

    public Optional<ParkingSpot> findByLotNumber(int lotNumber);
    public boolean existsByLotNumber(int lotNumber);
    public List<ParkingSpot> getParkingSpotByFreeToReserve(boolean free);
}
