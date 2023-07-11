package calitei.parking.api.entities;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
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
@AllArgsConstructor
@ToString
@Builder
public class User implements UserDetails {

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

    @Enumerated(EnumType.STRING)
    private Role role;



//    public User(String firstName, String lastName, String email, String password, String phoneNumber) {
//
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.email = email;
//        this.password = password;
//        this.phoneNumber = phoneNumber;
//    }

    public void reserveParkingSpot(ParkingSpot parkingSpot){
        parkingSpotsReserved.add(parkingSpot);
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
