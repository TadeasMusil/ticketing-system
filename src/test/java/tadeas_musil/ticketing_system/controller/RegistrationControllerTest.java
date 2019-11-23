package tadeas_musil.ticketing_system.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import tadeas_musil.ticketing_system.repository.UserRepository;
import tadeas_musil.ticketing_system.service.UserService;


@SpringBootTest
@AutoConfigureMockMvc
public class RegistrationControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;
    
    @Test
    public void shouldReturnRegistrationPage() throws Exception {
    	mockMvc.perform(get("/registration"))
    	.andExpect(status().isOk())
    	.andExpect(model().attributeExists("user"))
    	.andExpect(view().name("registration"));
    }

    @Test
    public void processingRegistration_shouldReturnIndexPage_givenSuccessfulRegistration() throws Exception {
        mockMvc.perform(post("/processRegistration")
            .param("username", "joe@email.com")
            .param("password", "password")
            .param("confirmPassword", "password")
            .with(csrf()))
        .andExpect(status().isOk())
    	.andExpect(view().name("registration"));
    }
    
    @Test 
    public void processingRegistration_shouldHaveError_givenNonMatchingPasswords() throws Exception {
        mockMvc.perform(post("/processRegistration")
            .param("firstName", "FirstName")
            .param("lastName", "LastName")
            .param("username", "user@email.com")
            .param("password", "password")
            .param("confirmPassword", "differentPassword")
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(model().attributeHasFieldErrors("user", "password"))
    	.andExpect(view().name("registration"));
    }

    @Test
    public void processingRegistration_shouldHaveError_whenRegisteringExistingUser() throws Exception{
        when(userRepository.existsByUsername(anyString())).thenReturn(true);
        
        mockMvc.perform(post("/processRegistration")
            .param("username", "username")
            .param("password", "password")
            .param("confirmPassword", "password")
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(model().attributeHasErrors("user"))
        .andExpect(model().attributeHasFieldErrors("user", "username"))
    	.andExpect(view().name("registration"));
    }
}