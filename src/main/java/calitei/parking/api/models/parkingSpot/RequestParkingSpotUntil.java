package calitei.parking.api.models.parkingSpot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class RequestParkingSpotUntil {
    private int lotNumber;
    private LocalDateTime untilWhen;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int seconds;

    public void setUntilWhen(){
        untilWhen = LocalDateTime.of(year, month, day, hour, minute, seconds);
    }
}
