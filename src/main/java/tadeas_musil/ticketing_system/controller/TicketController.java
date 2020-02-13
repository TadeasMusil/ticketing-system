package tadeas_musil.ticketing_system.controller;

import java.security.Principal;

import javax.validation.Valid;

import com.fasterxml.jackson.databind.node.TextNode;

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

import lombok.RequiredArgsConstructor;
import tadeas_musil.ticketing_system.entity.Department;
import tadeas_musil.ticketing_system.entity.Ticket;
import tadeas_musil.ticketing_system.entity.TicketEvent;
import tadeas_musil.ticketing_system.entity.enums.Priority;
import tadeas_musil.ticketing_system.service.CannedResponseService;
import tadeas_musil.ticketing_system.service.DepartmentService;
import tadeas_musil.ticketing_system.service.TicketService;
import tadeas_musil.ticketing_system.service.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ticket")
public class TicketController {

    private final TicketService ticketService;

    private final DepartmentService departmentService;

    private final UserService userService;

    private final CannedResponseService cannedResponseService;

    @GetMapping
    public String showTicketForm(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());

        if (!model.containsAttribute("ticket")) {
            Ticket ticket = new Ticket();
            ticket.addEvent(new TicketEvent());
            model.addAttribute("ticket", ticket);
        }

        return "create-ticket";
    }

    @PostMapping
    public String createTicket(@Valid @ModelAttribute Ticket ticket, BindingResult bindingResult, Principal principal,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.ticket", bindingResult);
            redirectAttributes.addFlashAttribute("ticket", ticket);
            return "redirect:/ticket";
        }
        if (principal != null) {
            ticket.setAuthor(principal.getName());
        }
        Ticket createdTicket = ticketService.createTicket(ticket);
        return "redirect:/ticket/" + createdTicket.getId();

    }

    @GetMapping("/{ticketId}")
    @PreAuthorize("hasPermission(@ticketServiceImpl.getById(#ticketId), 'read')")
    public String showTicket(@PathVariable Long ticketId, Model model) {
        model.addAttribute("ticket", ticketService.getById(ticketId));
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("staffMembers", userService.getAllStaffMembers());
        model.addAttribute("cannedResponses", cannedResponseService.getAllResponses());

        if (!model.containsAttribute("response")) {
            model.addAttribute("response", new TicketEvent());
        }
        return "ticket";
    }

    @PatchMapping(value = "/{ticketId}/priority")
    @ResponseBody
    @PreAuthorize("hasPermission(@ticketServiceImpl.getById(#ticketId), 'edit')")
    public void updatePriority(@PathVariable Long ticketId, @RequestBody Priority priority) {
        ticketService.updatePriority(ticketId, priority);
    }

    @PatchMapping(value = "/{ticketId}/department")
    @ResponseBody
    @PreAuthorize("hasPermission(@ticketServiceImpl.getById(#ticketId), 'edit')")
    public void updateDepartment(@PathVariable Long ticketId, @RequestBody Department department) {
        ticketService.updateDepartment(ticketId, department);
    }

    @PatchMapping(value = "/{ticketId}/owner")
    @ResponseBody
    @PreAuthorize("hasPermission(@ticketServiceImpl.getById(#ticketId), 'edit')")
    public void updateOwner(@PathVariable Long ticketId, @RequestBody(required = false) TextNode owner) {
        ticketService.updateOwner(ticketId, owner.textValue());
    }

    @PatchMapping(value = "/{ticketId}/status")
    @ResponseBody
    @PreAuthorize("hasPermission(@ticketServiceImpl.getById(#ticketId), 'edit')")
    public void updateStatus(@PathVariable Long ticketId, @RequestBody boolean isClosed) {
        ticketService.updateStatus(ticketId, isClosed);
    }

    @PostMapping(value = "/{ticketId}/response")
    @PreAuthorize("hasPermission(@ticketServiceImpl.getById(#ticketId), 'respond')")
    public String createResponse(@PathVariable Long ticketId, @Valid @ModelAttribute("response") TicketEvent response,
            BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.response",
                    bindingResult);
            redirectAttributes.addFlashAttribute("response", response);
        } else {
            ticketService.createResponse(ticketId, response.getContent());
        }
        return "redirect:" + "/ticket/" + ticketId;
    }

    @GetMapping("/assigned")
    @PreAuthorize("hasAnyAuthority('STAFF', 'ADMIN')")
    public String showAssignedTickets(Principal principal, Model model, @RequestParam(defaultValue = "0") int page) {
        model.addAttribute("slice", ticketService.getAssignedTickets(principal.getName(), page));
        return "ticket-list/assigned";
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String showAssignedTickets(Model model, @RequestParam(defaultValue = "0") int page) {
        model.addAttribute("slice", ticketService.getAllTickets(page));
        return "ticket-list/all";
    }

     @GetMapping("/my-tickets")
    public String showMyTickets(Principal principal, Model model, @RequestParam(defaultValue = "0") int page) {
        model.addAttribute("slice", ticketService.getByAuthor(principal.getName(), page));
        return "ticket-list/my-tickets";
    }
}