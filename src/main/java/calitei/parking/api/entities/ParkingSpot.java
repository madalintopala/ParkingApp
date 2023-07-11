package calitei.parking.api.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


import java.time.LocalDateTime;

@Entity(name = "ParkingSpot")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ParkingSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;
    @Column(name="lot_number")
    private int lotNumber;
    @Column(name="free_to_reserve")
    private boolean freeToReserve = true;
    @Column(name="free_to_reserve_until")
    private LocalDateTime freeToReserveUntil;
    @Column(name="owner_name")
    private String ownerName;
    @Column(name="reserved_by_name")
    private String reservedByName;
    @Column(name="advertise_as_free_by_owner")
    private boolean advertiseAsFreeByOwner = true;
    @Column(name="advertise_as_free_until")
    private LocalDateTime advertiseAsFreeUntil;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @JsonBackReference(value = "parkingSpotOwner")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "reserved_by_id")
    @JsonBackReference(value = "reservedBy")
    private User reservedBy;

    public ParkingSpot(int lotNumber) {
        this.lotNumber = lotNumber;
    }

    public ParkingSpot(int lotNumber, User owner) {
        this.lotNumber = lotNumber;
        this.owner = owner;
        this.ownerName =  owner.getFirstName() + " " + owner.getLastName();
    }

}
