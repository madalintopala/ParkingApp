package calitei.parking.api.entities;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;


@Entity(name = "User")
@Table(
        name ="users",
        uniqueConstraints = {
                @UniqueConstraint(name = "user_email_unique", columnNames = "email")
        }
)
@Getter
@Setter
@NoArgsConstructor
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(
            name = "email",
            columnDefinition = "TEXT"
    )
    private String email;
    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "password")
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "reservedBy", fetch=FetchType.EAGER)
    @JsonManagedReference(value = "reservedBy")
    private List<ParkingSpot> parkingSpotsReserved;

    @OneToMany(mappedBy = "owner", fetch=FetchType.EAGER)
    @JsonManagedReference(value = "parkingSpotOwner")
    private List<ParkingSpot> parkingSpotsOwned;



    public User(String firstName, String lastName, String email, String password, String phoneNumber) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    public void reserveParkingSpot(ParkingSpot parkingSpot){
        parkingSpotsReserved.add(parkingSpot);
    }




}
