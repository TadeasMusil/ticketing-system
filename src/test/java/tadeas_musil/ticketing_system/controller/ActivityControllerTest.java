package tadeas_musil.ticketing_system.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import tadeas_musil.ticketing_system.service.TicketEventService;


@WebMvcTest(ActivityController.class)
public class ActivityControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private TicketEventService ticketEventService;
    
    @Test
    @WithMockUser
    public void showingMyActivity_shouldShowActivity_givenAuthenticatedUser() throws Exception {
        Page page = new PageImpl<>(new ArrayList<>());
        when(ticketEventService.getEventsByAuthor(anyString(), anyInt())).thenReturn(page);
        
    	mockMvc.perform(get("/activity/user"))
    	.andExpect(status().isOk())
    	.andExpect(model().attributeExists("slice"))
    	.andExpect(view().name("activity/user-activity"));
    }

    @Test
    public void showingMyActivity_shouldGetRedirected_givenNotAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/activity/user"))
    	.andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(authorities = "USER")
    public void showingMyDepartmentsActivity_shouldReturnForbidden_givenRoleUSER() throws Exception {
        mockMvc.perform(get("/activity/departments"))
    	.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "STAFF")
    public void showingMyDepartmentsActivity_shouldShowActivity_givenRoleSTAFF() throws Exception {
        Page page = new PageImpl<>(new ArrayList<>());
        when(ticketEventService.getEventsByUsersDepartments(anyString(), anyInt())).thenReturn(page);
        
    	mockMvc.perform(get("/activity/departments"))
    	.andExpect(status().isOk())
    	.andExpect(model().attributeExists("slice"))
    	.andExpect(view().name("activity/departments-activity"));
    }

    @Test
    @WithMockUser(authorities = "STAFF")
    public void showingAllActivity_shouldReturnForbidden_givenRoleSTAFF() throws Exception {
        mockMvc.perform(get("/activity/all"))
    	.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void showingAllActivity_shouldShowActivity_givenRoleADMIN() throws Exception {
        Page page = new PageImpl<>(new ArrayList<>());
        when(ticketEventService.getAllEvents(anyInt())).thenReturn(page);
        
    	mockMvc.perform(get("/activity/all"))
    	.andExpect(status().isOk())
    	.andExpect(model().attributeExists("slice"))
    	.andExpect(view().name("activity/all-activity"));
    }
}