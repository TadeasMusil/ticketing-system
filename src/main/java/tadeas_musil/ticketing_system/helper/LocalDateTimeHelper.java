package tadeas_musil.ticketing_system.helper;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LocalDateTimeHelper {
    
    @Value("${app.timezone}")
    private String timezone;
    
    public LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now(ZoneId.of(timezone));
    }
}