package tadeas_musil.ticketing_system.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tadeas_musil.ticketing_system.entity.enums.TicketEventType;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class TicketEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    private TicketEventType type;

    private String author;
    
    @NotBlank
    private String content;

    @CreationTimestamp
    private LocalDateTime created;

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
        TicketEvent other = (TicketEvent) obj;
        return id != null &&
               id.equals(other.getId());
    }

    public TicketEvent(String author) {
        this.author = author;
    }

    public TicketEvent(String author, TicketEventType type) {
        this.type = type;
        this.author = author;
    }
}
