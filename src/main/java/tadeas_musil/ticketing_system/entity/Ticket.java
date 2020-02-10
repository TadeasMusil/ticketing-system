package tadeas_musil.ticketing_system.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tadeas_musil.ticketing_system.entity.enums.Priority;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @NotBlank 
    private String subject;

    private boolean isClosed;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @ManyToOne
    @JoinColumn(name = "department_name")
    private Department department;
    
    @NotBlank
    private String author;

    private String owner;

    @CreationTimestamp
    private LocalDateTime created;
    
    @Valid 
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<TicketEvent> events = new ArrayList<>();

    public void addEvent(TicketEvent event){
        this.events.add(event);
        event.setTicket(this);
    }
    public void removeEvent(TicketEvent event){
        this.events.remove(event);
        event.setTicket(null);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Ticket other = (Ticket) obj;
        return id != null &&
               id.equals(other.getId());
    }

    public Ticket(Long id) {
        this.id = id;
    }

    public Ticket(@NotBlank String owner, boolean isClosed) {
        this.isClosed = isClosed;
        this.owner = owner;
    }

    
}