package tadeas_musil.ticketing_system.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(LoginController.class)
public class LoginControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
     @Test
     public void shouldReturnLoginPage() throws Exception{
        mockMvc.perform(get("/login"))
        	.andExpect(status().isOk())
        	.andExpect(view().name("login"));
    }
}