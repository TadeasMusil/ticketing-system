package tadeas_musil.ticketing_system.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import tadeas_musil.ticketing_system.service.TicketEventService;
import tadeas_musil.ticketing_system.service.UserStatisticsService;

@Controller
@RequiredArgsConstructor
public class IndexController {

	private final TicketEventService ticketEventService;

	private final UserStatisticsService userStatisticsService;

	@RequestMapping("/")
	public String showHomepage(Authentication auth, Model model, @RequestParam(defaultValue = "0") int page) {
		if (auth != null) {
			model.addAttribute("slice", ticketEventService.getEventsByAuthor(auth.getName(), page));
			model.addAttribute("closedTickets", userStatisticsService.getNumberOfClosedTickets(auth.getName()));
			model.addAttribute("assignedTickets", userStatisticsService.getNumberOfAssignedTickets(auth.getName()));
			model.addAttribute("createdTickets", userStatisticsService.getNumberOfCreatedTickets(auth.getName()));
		}
		return "index";
	}
}