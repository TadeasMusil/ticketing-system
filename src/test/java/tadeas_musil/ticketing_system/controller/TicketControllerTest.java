package tadeas_musil.ticketing_system.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import tadeas_musil.ticketing_system.entity.Department;
import tadeas_musil.ticketing_system.entity.Ticket;
import tadeas_musil.ticketing_system.entity.TicketEvent;
import tadeas_musil.ticketing_system.entity.enums.Priority;
import tadeas_musil.ticketing_system.service.DepartmentService;
import tadeas_musil.ticketing_system.service.TicketService;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class TicketControllerTest { 
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketService ticketService;

    @MockBean
    private DepartmentService departmentService;

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
        validTicket.setId(Long.valueOf(1));
        validTicket.setAuthor("author");
        validTicket.setSubject("subject");
        

        TicketEvent event = new TicketEvent();
        event.setContent("content");
        validTicket.addEvent(event);

        when(ticketService.createTicket(any())).thenReturn(validTicket);

        mockMvc.perform(post("/ticket")
                .flashAttr("ticket", validTicket)
                .with(csrf()))
           .andExpect(model().hasNoErrors())
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrl("/ticket/" + validTicket.getId()));


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
    @WithMockUser(authorities = "ADMIN")
    public void showTicket_shouldShowTicket_givenUserHasPermission() throws Exception{
        when(ticketService.getById(anyLong())).thenReturn(new Ticket());
        
        mockMvc.perform(get("/ticket/1"))
           .andExpect(status().isOk())
           .andExpect(model().attributeExists("ticket", "departments"))
           .andExpect(view().name("ticket"));
    }

    @Test
    public void showTicket_shouldGetRedirected_givenUserDoesNotHavePermission() throws Exception{
        when(ticketService.getById(anyLong())).thenReturn(new Ticket());
        
        mockMvc.perform(get("/ticket/1"))
           .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void updatePriority_shouldUpdatePriority_givenUserDoesHavePermissison() throws Exception{
        when(ticketService.getById(anyLong())).thenReturn(new Ticket());

        String jsonPriority = new ObjectMapper().writeValueAsString(Priority.LOW);
        
        mockMvc.perform(patch("/ticket/1/priority")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPriority)
                        .with(csrf()))
                .andExpect(status().isOk());
        verify(ticketService).updatePriority(Long.valueOf(1), Priority.LOW);
    }

    @Test
    public void updatePriority_shouldGetRedirected_givenUserDoesNotHavePermissison() throws Exception{
        when(ticketService.getById(anyLong())).thenReturn(new Ticket());
        
        mockMvc.perform(patch("/ticket/1/priority")
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
        System.out.println(mockingDetails(ticketService).printInvocations());
        String jsonOwner = new ObjectMapper().writeValueAsString(isClosed);
        
        mockMvc.perform(patch("/ticket/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonOwner)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(ticketService).updateStatus(Long.valueOf(1), isClosed);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void creatingResponse_shouldCreateNewResponse_givenValidResponse() throws Exception{
        
        TicketEvent response = new TicketEvent();
        when(ticketService.getById(anyLong())).thenReturn(new Ticket());
        
        mockMvc.perform(post("/ticket/1/response")
                .flashAttr("response", response)
                .with(csrf()))
           .andExpect(status().is3xxRedirection())
           .andExpect(model().hasNoErrors());
           
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void creatingResponse_shouldShowError_givenInvalidTicket() throws Exception{
        when(ticketService.getById(anyLong())).thenReturn(new Ticket());
		TicketEvent response = new TicketEvent();
	
        mockMvc.perform(post("/ticket/1/response")
                .flashAttr("response", response)
                .with(csrf()))
           .andExpect(status().is3xxRedirection())
           .andExpect(flash().attributeCount(2));
    }

    @Test
    public void creatingResponse_shouldGetRedirected_givenAnonymousUser() throws Exception{
            when(ticketService.getById(anyLong())).thenReturn(new Ticket());
		 TicketEvent response = new TicketEvent();
        response.setContent("content");
		
        mockMvc.perform(post("/ticket/1/response")
                .flashAttr("response", response)
                .with(csrf()))
           .andExpect(status().is3xxRedirection());
           
    }
}