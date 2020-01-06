package tadeas_musil.ticketing_system.validation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EmailAndIdMatch(id = "ticketId", email = "authorEmail")
public class TicketAccessForm {

    private Long ticketId;

    private String authorEmail;
}