package tadeas_musil.ticketing_system.controller;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tadeas_musil.exception.InvalidTicketTokenException;
import tadeas_musil.ticketing_system.entity.TicketToken;
import tadeas_musil.ticketing_system.service.TicketService;
import tadeas_musil.ticketing_system.service.TokenService;
import tadeas_musil.ticketing_system.validation.TicketAccessForm;

@Controller
@RequestMapping("/access-form")
public class TicketAccessFormController {

  @Autowired
  private TicketService ticketService;

  @Autowired
  private TokenService<TicketToken> ticketTokenService;

  // Validates that the given ticketId and email are
  // matching and sends a link to the given email
  @PostMapping
  public String processTicketAccessRequest(@Valid @ModelAttribute TicketAccessForm ticketAccessForm,
      BindingResult bindingResult, RedirectAttributes redirectAttributes) throws MessagingException {
    if (bindingResult.hasErrors()) {
      return "ticket-access-form";
    }
    ticketService.sendTicketAccessEmail(ticketAccessForm.getTicketId(), ticketAccessForm.getAuthorEmail());
    redirectAttributes.addFlashAttribute("ticketLinkSuccessfullySent", true);
    return "redirect:/access-form";
  }

  @GetMapping
  public String showTicketAccessForm(Model model) {
    model.addAttribute("ticketAccessForm", new TicketAccessForm());
    return "ticket-access-form";
  }

  @GetMapping(value = "/ticket/{ticket.id}", params = "token")
  public String showTicket(TicketToken token, Model model) {
    if (ticketTokenService.validateToken(token)) {
      model.addAttribute("ticket", ticketService.getById(token.getTicket()
                                                              .getId()));
      return "ticket";
    } else {
      throw new InvalidTicketTokenException();
    }
  }
}
