package tadeas_musil.ticketing_system.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import tadeas_musil.ticketing_system.service.TicketEventService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/activity")
public class ActivityController {

	private final TicketEventService ticketEventService;

	@RequestMapping("/user")
	public String showMyActivity(Authentication auth, Model model, @RequestParam(defaultValue = "0") int page) {
		model.addAttribute("slice", ticketEventService.getEventsByAuthor(auth.getName(), page));
		return "activity/user-activity";
	}

	@PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
	@RequestMapping("/departments")
	public String showActivityOfMyDepartments(Authentication auth, Model model,
			@RequestParam(defaultValue = "0") int page) {
		model.addAttribute("slice", ticketEventService.getEventsByUsersDepartments(auth.getName(), page));
		return "activity/departments-activity";
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping("/all")
	public String showAllActivity(Authentication auth, Model model, @RequestParam(defaultValue = "0") int page) {
		model.addAttribute("slice", ticketEventService.getAllEvents(page));
		return "activity/all-activity";
	}
}