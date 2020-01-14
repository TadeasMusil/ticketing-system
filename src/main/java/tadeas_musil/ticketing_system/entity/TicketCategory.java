package tadeas_musil.ticketing_system.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class TicketCategory {
    
    @NotBlank 
    @Id
    private String name;
    
    
}