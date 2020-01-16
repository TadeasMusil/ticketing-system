package tadeas_musil.ticketing_system.controller;

import java.util.List;

import javax.mail.MessagingException;
import javax.validation.Valid;

import com.fasterxml.jackson.databind.node.TextNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tadeas_musil.exception.InvalidTicketTokenException;
import tadeas_musil.ticketing_system.entity.Department;
import tadeas_musil.ticketing_system.entity.Ticket;
import tadeas_musil.ticketing_system.entity.TicketCategory;
import tadeas_musil.ticketing_system.entity.enums.Priority;
import tadeas_musil.ticketing_system.service.DepartmentService;
import tadeas_musil.ticketing_system.service.TicketCategoryService;
import tadeas_musil.ticketing_system.service.TicketService;
import tadeas_musil.ticketing_system.service.TicketTokenService;
import tadeas_musil.ticketing_system.validation.TicketAccessForm;

@Controller
@RequestMapping("/ticket")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketCategoryService ticketCategoryService;

    @Autowired
    private TicketTokenService ticketTokenService;

    @Autowired
    private DepartmentService departmentService;

    // Validates that the given ticketId and email are
    // matching and sends a link to the given email
    @PostMapping("/accessForm")
    public String processTicketAccessRequest(@Valid @ModelAttribute TicketAccessForm ticketAccessForm,
            BindingResult bindingResult, RedirectAttributes redirectAttributes) throws MessagingException {
        if (bindingResult.hasErrors()) {
            return "ticket-access-form";
        }
        ticketService.sendTicketAccessEmail(ticketAccessForm.getTicketId(), ticketAccessForm.getAuthorEmail());
        redirectAttributes.addAttribute("ticketLinkSuccessfullySent", true);
        return "redirect:/index";
    }

    @GetMapping
    public String showTicketForm(Model model) {
        Ticket ticket = new Ticket();
        List<TicketCategory> categories = ticketCategoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("ticket", ticket);
        return "create-ticket";
    }

    @PostMapping
    public String createTicket(@Valid @ModelAttribute Ticket ticket, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "create-ticket";
        }
        ticketService.createTicket(ticket);
        return "index";

    }

    @GetMapping("/accessForm")
    public String showTicketAccessForm(Model model) {
        model.addAttribute("ticketAccessForm", new TicketAccessForm());
        return "ticket-access-form";
    }

    @GetMapping(value = "/{ticketId}", params = "token")
    public String showTicket(@PathVariable Long ticketId, @RequestParam String token, Model model) {
        if (ticketTokenService.validateToken(ticketId, token)) {
            model.addAttribute("ticket", ticketService.getById(ticketId));
            return "ticket";
        } 
        else {
            throw new InvalidTicketTokenException();
        }

        
    }

    @GetMapping("/{ticketId}")
    @PreAuthorize("hasPermission(@ticketServiceImpl.getById(#ticketId), 'read')")
    public String showTicket(@PathVariable Long ticketId, Model model) {
        model.addAttribute("ticket", ticketService.getById(ticketId));
        model.addAttribute("categories", ticketCategoryService.getAllCategories());
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "ticket";
    }

    @PatchMapping(value = "/{ticketId}/priority", params = "priority")
    @ResponseBody
    @PreAuthorize("hasPermission(@ticketServiceImpl.getById(#ticketId), 'edit')")
    public void updatePriority(@PathVariable Long ticketId, @RequestParam Priority priority){
        ticketService.updatePriority(ticketId, priority);
    }

    @PatchMapping(value = "/{ticketId}/category")
    @ResponseBody
    @PreAuthorize("hasPermission(@ticketServiceImpl.getById(#ticketId), 'edit')")
    public void updatePriority(@PathVariable Long ticketId, @RequestBody TicketCategory category){
        ticketService.updateCategory(ticketId, category);
    }

    @PatchMapping(value = "/{ticketId}/department")
    @ResponseBody
    @PreAuthorize("hasPermission(@ticketServiceImpl.getById(#ticketId), 'edit')")
    public void updateDepartment(@PathVariable Long ticketId, @RequestBody Department department){
        ticketService.updateDepartment(ticketId, department);
    }

    @PatchMapping(value = "/{ticketId}/owner")
    @ResponseBody
    @PreAuthorize("hasPermission(@ticketServiceImpl.getById(#ticketId), 'edit')")
    public void updateOwner(@PathVariable Long ticketId, @RequestBody TextNode owner){
        ticketService.updateOwner(ticketId, owner.asText());
    }
}