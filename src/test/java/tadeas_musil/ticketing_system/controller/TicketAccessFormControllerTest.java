package tadeas_musil.ticketing_system.controller;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import tadeas_musil.exception.InvalidTicketTokenException;
import tadeas_musil.ticketing_system.entity.Department;
import tadeas_musil.ticketing_system.entity.Ticket;
import tadeas_musil.ticketing_system.repository.TicketRepository;
import tadeas_musil.ticketing_system.service.TicketService;
import tadeas_musil.ticketing_system.service.TicketTokenService;
import tadeas_musil.ticketing_system.validation.TicketAccessForm;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class TicketAccessFormControllerTest { 
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketService ticketService;

    @MockBean
    private TicketRepository ticketRepository;

    @MockBean
    private TicketTokenService ticketTokenService;
    
    @Test
    @WithMockUser
    public void processingAccessRequest_shouldHaveErrors_givenInvalidForm() throws Exception{
        TicketAccessForm form = new TicketAccessForm();
        form.setAuthorEmail("name@email.com");
        form.setTicketId(Long.valueOf(5));  
        when(ticketRepository.existsById(anyLong())).thenReturn(false);

        mockMvc.perform(post("/accessForm")
                    .flashAttr("ticketAccessForm", form)
                    .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(model().hasErrors())
            .andExpect(view().name("ticket-access-form"));
    }

    @Test
    @WithMockUser
    public void processingAccessRequest_shouldRedirectWithoutErrors_givenValidForm() throws Exception{
        TicketAccessForm form = new TicketAccessForm();
        form.setAuthorEmail("name@email.com");
        form.setTicketId(Long.valueOf(5));
        
        Ticket ticket = new Ticket();
        ticket.setId(Long.valueOf(5));
        ticket.setAuthor("name@email.com");
        when(ticketRepository.existsById(anyLong())).thenReturn(true);
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.of(ticket));
       
       mockMvc.perform(post("/accessForm")
                        .flashAttr("ticketAccessForm", form)
                        .with(csrf()))
           .andExpect(status().is3xxRedirection())
           .andExpect(model().hasNoErrors())
           .andExpect(redirectedUrl("/index?ticketLinkSuccessfullySent=true"));
           
    }

    

   


    @Test
    public void checkTicketStatus_shouldShowTicket_givenValidToken() throws Exception{
        when(ticketTokenService.validateToken(Long.valueOf(1), "uuidToken")).thenReturn(true);
        Ticket ticket = new Ticket();
        ticket.setId(Long.valueOf(1));
        
        Department department = new Department();
        department.setName("departmentName");
        ticket.setDepartment(department);
        
        when(ticketService.getById(anyLong())).thenReturn(ticket);
        
        mockMvc.perform(post("/accessForm/ticket/1")
                .param("token", "uuidToken")
                .with(csrf()))
           .andExpect(status().isOk())
           .andExpect(model().hasNoErrors())
           .andExpect(view().name("ticket"));
    }

    @Test
    @WithMockUser
    public void checkTicketStatus_shouldShowError_givenInvalidToken() throws Exception{
        when(ticketTokenService.validateToken(anyLong(), anyString())).thenReturn(false);

        assertThatThrownBy(() -> mockMvc.perform(post("/accessForm/ticket/1")
        .param("token", "uuidToken")
        .with(csrf())))
        .hasCauseExactlyInstanceOf(InvalidTicketTokenException.class);
    }

  
}