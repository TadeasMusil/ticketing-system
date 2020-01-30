package tadeas_musil.ticketing_system.controller;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tadeas_musil.exception.InvalidTicketTokenException;
import tadeas_musil.ticketing_system.service.TicketService;
import tadeas_musil.ticketing_system.service.TicketTokenService;
import tadeas_musil.ticketing_system.validation.TicketAccessForm;

@Controller
@RequestMapping("/accessForm")
public class TicketAccessFormController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketTokenService ticketTokenService;

    // Validates that the given ticketId and email are
    // matching and sends a link to the given email
    @PostMapping
    public String processTicketAccessRequest(Authentication auth, @Valid @ModelAttribute TicketAccessForm ticketAccessForm,
            BindingResult bindingResult, RedirectAttributes redirectAttributes) throws MessagingException {
        if (bindingResult.hasErrors()) {
            return "ticket-access-form";
        }
        ticketService.sendTicketAccessEmail(ticketAccessForm.getTicketId(), ticketAccessForm.getAuthorEmail());
        redirectAttributes.addAttribute("ticketLinkSuccessfullySent", true);
        return "redirect:/index";
    }
    @GetMapping
    public String showTicketAccessForm(Model model) {
        model.addAttribute("ticketAccessForm", new TicketAccessForm());
        return "ticket-access-form";
    }

    @PostMapping(value = "/ticket/{ticketId}", params = "token")
    public String showTicket(@PathVariable Long ticketId, @RequestParam String token, Model model) {
        if (ticketTokenService.validateToken(ticketId, token)) {
            model.addAttribute("ticket", ticketService.getById(ticketId));
            return "ticket";
        } else {
            throw new InvalidTicketTokenException();
        }
    }
}