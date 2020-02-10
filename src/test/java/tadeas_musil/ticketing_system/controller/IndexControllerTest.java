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
import tadeas_musil.ticketing_system.service.UserStatisticsService;

@WebMvcTest(IndexController.class)
public class IndexControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketEventService ticketEventService;

    @MockBean
    private UserStatisticsService userStatisticsService;

    @Test
    @WithMockUser
    public void shouldReturnIndexPageWithAttributes_GivenAuthenticatedUser() throws Exception {
        Page page = new PageImpl<>(new ArrayList<>());
        when(ticketEventService.getEventsByAuthor(anyString(), anyInt())).thenReturn(page);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("slice", "closedTickets", "assignedTickets", "createdTickets"))
                .andExpect(view().name("index"));
    }

    @Test
    public void shouldReturnIndexPage_GivenNoAuthentication() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().size(0))
                .andExpect(view().name("index"));
    }
}