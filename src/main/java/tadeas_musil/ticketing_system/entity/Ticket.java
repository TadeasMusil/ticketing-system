package tadeas_musil.ticketing_system.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @NotBlank 
    private String subject;

    @NotBlank 
    private String content;

    private boolean isClosed;

    @Column(columnDefinition = "CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH'))")
    private String priority;

    @ManyToOne
    @JoinColumn(name = "department")
    private Department department;

    @Valid
    @ManyToOne
    @JoinColumn(name = "ticket_category_id")
    private TicketCategory category; 
    
    @NotBlank
    private String author;

    private String owner;

    @CreationTimestamp
    private LocalDateTime date;
    
    @Valid
    @OneToMany( cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,
            CascadeType.REFRESH })
    private List<TicketEvent> events = new ArrayList<>();

    public void addEvent(TicketEvent event){
        this.events.add(event);
        event.setTicket(this);
    }
    public void removeEvent(TicketEvent event){
        this.events.remove(event);
        event.setTicket(null);
    }
}