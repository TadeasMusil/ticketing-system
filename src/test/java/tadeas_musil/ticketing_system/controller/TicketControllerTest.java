package tadeas_musil.ticketing_system.controller;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import tadeas_musil.exception.InvalidTicketTokenException;
import tadeas_musil.ticketing_system.entity.Department;
import tadeas_musil.ticketing_system.entity.Ticket;
import tadeas_musil.ticketing_system.entity.enums.Priority;
import tadeas_musil.ticketing_system.repository.TicketRepository;
import tadeas_musil.ticketing_system.service.DepartmentService;
import tadeas_musil.ticketing_system.service.TicketService;
import tadeas_musil.ticketing_system.service.TicketTokenService;
import tadeas_musil.ticketing_system.validation.TicketAccessForm;

@SpringBootTest
@AutoConfigureMockMvc
public class TicketControllerTest { 
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketService ticketService;

    @MockBean
    private TicketRepository ticketRepository;

    @MockBean
    private TicketTokenService ticketTokenService;

    @MockBean
    private DepartmentService departmentService;
    
    @Test
    public void processingAccessRequest_shouldHaveErrors_givenInvalidForm() throws Exception{
        TicketAccessForm form = new TicketAccessForm();
        form.setAuthorEmail("name@email.com");
        form.setTicketId(Long.valueOf(5));  
        when(ticketRepository.existsById(anyLong())).thenReturn(false);

        mockMvc.perform(post("/ticket/accessForm")
                    .flashAttr("ticketAccessForm", form)
                    .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(model().hasErrors())
            .andExpect(view().name("ticket-access-form"));
    }

    @Test
    public void processingAccessRequest_shouldRedirectWithoutErrors_givenValidForm() throws Exception{
        TicketAccessForm form = new TicketAccessForm();
        form.setAuthorEmail("name@email.com");
        form.setTicketId(Long.valueOf(5));
        
        Ticket ticket = new Ticket();
        ticket.setId(Long.valueOf(5));
        ticket.setAuthor("name@email.com");
        when(ticketRepository.existsById(anyLong())).thenReturn(true);
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.of(ticket));
       
       mockMvc.perform(post("/ticket/accessForm")
                        .flashAttr("ticketAccessForm", form)
                        .with(csrf()))
           .andExpect(status().is3xxRedirection())
           .andExpect(model().hasNoErrors())
           .andExpect(redirectedUrl("/index?ticketLinkSuccessfullySent=true"));
           
    }

    @Test
    @WithMockUser
    public void showCreateTicketForm_shouldReturnCorrectView() throws Exception{
        Department department = new Department();
        department.setName("departmentName");
        List<Department> departments = List.of(department);
        
        when(departmentService.getAllDepartments()).thenReturn(departments);
       
       mockMvc.perform(get("/ticket"))
           .andExpect(status().isOk())
           .andExpect(model().attribute("departments", departments))
           .andExpect(model().attributeExists("ticket"))
           .andExpect(view().name("create-ticket"));
           
    }

    @Test
    @WithMockUser
    public void creatingTicket_shouldCreateTicket_givenValidTicket() throws Exception{
        
        Ticket validTicket = new Ticket();
        validTicket.setAuthor("author");
        validTicket.setContent("content");
        validTicket.setSubject("subject");
        
       
        mockMvc.perform(post("/ticket")
                .flashAttr("ticket", validTicket)
                .with(csrf()))
           .andExpect(status().isOk())
           .andExpect(model().hasNoErrors())
           .andExpect(view().name("index"));

        verify(ticketService).createTicket(validTicket);
           
    }

    @Test
    @WithMockUser
    public void creatingTicket_shouldShowError_givenInvalidTicket() throws Exception{
        mockMvc.perform(post("/ticket")
                .flashAttr("ticket", new Ticket())
                .with(csrf()))
           .andExpect(status().isOk())
           .andExpect(model().hasErrors())
           .andExpect(view().name("create-ticket"));
    }

    @Test
    @WithMockUser
    public void checkTicketStatus_shouldShowTicket_givenValidToken() throws Exception{
        when(ticketTokenService.validateToken(Long.valueOf(1), "uuidToken")).thenReturn(true);
        Ticket ticket = new Ticket();
        ticket.setId(Long.valueOf(1));
        
        Department department = new Department();
        department.setName("departmentName");
        ticket.setDepartment(department);
        
        when(ticketService.getById(anyLong())).thenReturn(ticket);
        
        mockMvc.perform(get("/ticket/1")
                .param("token", "uuidToken"))
           .andExpect(status().isOk())
           .andExpect(model().hasNoErrors())
           .andExpect(view().name("ticket"));
    }

    @Test
    @WithMockUser
    public void checkTicketStatus_shouldShowError_givenInvalidToken() throws Exception{
        when(ticketTokenService.validateToken(anyLong(), anyString())).thenReturn(false);

        
        assertThatThrownBy(() -> mockMvc.perform(get("/ticket/1")
        .param("token", "uuidToken"))).hasCauseExactlyInstanceOf(InvalidTicketTokenException.class);
        
        
           
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void showTicket_shouldShowTicket_givenUserHasPermission() throws Exception{
        when(ticketService.getById(anyLong())).thenReturn(new Ticket());
        
        mockMvc.perform(get("/ticket/1"))
           .andExpect(status().isOk())
           .andExpect(model().attributeExists("ticket", "departments"))
           .andExpect(view().name("ticket"));
    }

    @Test
    @WithAnonymousUser
    public void showTicket_shouldGetRedirected_givenUserDoesNotHavePermission() throws Exception{
        when(ticketService.getById(anyLong())).thenReturn(new Ticket());
        
        mockMvc.perform(get("/ticket/1"))
           .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void updatePriority_shouldUpdatePriority_givenUserDoesHavePermissison() throws Exception{
        when(ticketService.getById(anyLong())).thenReturn(new Ticket());
        
        mockMvc.perform(patch("/ticket/1/priority")
                        .param("priority", Priority.LOW.toString())
                        .with(csrf()))
                .andExpect(status().isOk());
        verify(ticketService).updatePriority(Long.valueOf(1), Priority.LOW);
    }

    @Test
    public void updatePriority_shouldGetRedirected_givenUserDoesNotHavePermissison() throws Exception{
        when(ticketService.getById(anyLong())).thenReturn(new Ticket());
        
        mockMvc.perform(patch("/ticket/1/priority")
                        .param("priority", Priority.LOW.name())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void updateDepartment_shouldUpdateDepartment_givenUserDoesHavePermissison() throws Exception{
        when(ticketService.getById(anyLong())).thenReturn(new Ticket());
        Department department = new Department();
        department.setName("departmentName");
    
        String jsonDepartment = new ObjectMapper().writeValueAsString(department);

        mockMvc.perform(patch("/ticket/1/department")  
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDepartment)
                        .with(csrf()))
                .andExpect(status().isOk());
        
                verify(ticketService).updateDepartment(anyLong(), any());
    }

    @Test
    public void updateDepartment_shouldGetRedirected_givenUserDoesNotHavePermissison() throws Exception{
        when(ticketService.getById(anyLong())).thenReturn(new Ticket());
        Department department = new Department();
    department.setName("departmentName");

        String jsonDepartment = new ObjectMapper().writeValueAsString(department);
        
        mockMvc.perform(patch("/ticket/1/department")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDepartment)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void updateOwner_shouldGetRedirected_givenUserDoesNotHavePermissison() throws Exception{
        when(ticketService.getById(anyLong())).thenReturn(new Ticket());
        String owner = "newOwner@email.com";

        String jsonOwner = new ObjectMapper().writeValueAsString(owner);
        
        mockMvc.perform(patch("/ticket/1/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonOwner)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void updateOwner_shouldUpdateOwner_givenUserDoesHavePermissison() throws Exception{
        when(ticketService.getById(anyLong())).thenReturn(new Ticket());
        String owner = "newOwner@email.com";

        String jsonOwner = new ObjectMapper().writeValueAsString(owner);
        
        mockMvc.perform(patch("/ticket/1/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonOwner)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(ticketService).updateOwner(Long.valueOf(1), owner);
    }

    @Test
    public void updateStatus_shouldGetRedirected_givenUserDoesNotHavePermissison() throws Exception{
        when(ticketService.getById(anyLong())).thenReturn(new Ticket());
        boolean isClosed = true;

        String jsonIsClosed = new ObjectMapper().writeValueAsString(isClosed);
        
        mockMvc.perform(patch("/ticket/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonIsClosed)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void updateStatus_shouldUpdateStatus_givenUserDoesHavePermissison() throws Exception{
        when(ticketService.getById(anyLong())).thenReturn(new Ticket());
        boolean isClosed = true;

        String jsonOwner = new ObjectMapper().writeValueAsString(isClosed);
        
        mockMvc.perform(patch("/ticket/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonOwner)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(ticketService).updateStatus(Long.valueOf(1), isClosed);
    }
}