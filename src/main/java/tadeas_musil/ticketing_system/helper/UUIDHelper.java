package tadeas_musil.ticketing_system.helper;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class UUIDHelper {
    
    public String randomUUID() {
        return UUID.randomUUID().toString();
    }
}