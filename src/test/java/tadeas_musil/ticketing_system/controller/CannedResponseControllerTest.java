package tadeas_musil.ticketing_system.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import tadeas_musil.ticketing_system.entity.CannedResponse;
import tadeas_musil.ticketing_system.repository.CannedResponseRepository;
import tadeas_musil.ticketing_system.service.CannedResponseService;


@WebMvcTest(CannedResponseController.class)
public class CannedResponseControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private CannedResponseService cannedResponseService;

    @MockBean
    private CannedResponseRepository cannedResponseRepository;
    
    @Test
    @WithMockUser
    public void shouldShowCannedResponseForm() throws Exception {
    	mockMvc.perform(get("/cannedResponse"))
    	.andExpect(status().isOk())
    	.andExpect(model().attributeExists("cannedResponses", "cannedResponse"))
    	.andExpect(view().name("canned-response-form"));
    }

    @Test
    @WithMockUser  
    public void getResponse_shouldReturnCorrectCannedResponse() throws Exception {
        CannedResponse cannedResponse = new CannedResponse();
        cannedResponse.setName("responseName");
        cannedResponse.setContent("content");
        cannedResponse.setId(Long.valueOf(1));
        
        when(cannedResponseService.getResponseById(anyLong())).thenReturn(cannedResponse);
        
        mockMvc.perform(get("/cannedResponse/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(cannedResponse.getName()))
        .andExpect(jsonPath("$.content").value(cannedResponse.getContent()))
        .andExpect(jsonPath("$.id").value(cannedResponse.getId()));
    }

    @Test
    @WithMockUser
    public void processingResponseForm_shouldReturnFormWithErrors_givenInvalidCannedResponse() throws Exception {
        CannedResponse response = new CannedResponse();
        response.setContent("content");
        response.setName("name");
        when(cannedResponseRepository.existsByName(anyString())).thenReturn(true);
        
        mockMvc.perform(post("/cannedResponse")
            .param("action", "save")
            .flashAttr("cannedResponse", response)
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(model().hasErrors())
        .andExpect(view().name("canned-response-form"));
        
    }

    @Test 
      @WithMockUser
      public void processingResponseForm_shouldRedirectToForm_givenValidCannedResponse() throws Exception {
          CannedResponse cannedResponse = new CannedResponse();
          cannedResponse.setContent("content");
          cannedResponse.setName("name");
          when(cannedResponseRepository.existsByName(anyString())).thenReturn(false);
          
          mockMvc.perform(post("/cannedResponse")
                .param("action", "save")
              .flashAttr("cannedResponse", cannedResponse)
              .with(csrf()))
          .andExpect(status().is3xxRedirection())
          .andExpect(redirectedUrl("/cannedResponse"));
          
          verify(cannedResponseService).saveResponse(cannedResponse);
      }
    
      @Test 
      @WithMockUser
      public void deleteResponse_shouldDeleteResponse_givenValidCannedResponse() throws Exception {
          CannedResponse cannedResponse = new CannedResponse();
          
          mockMvc.perform(post("/cannedResponse")
                .param("action", "delete")
              .flashAttr("cannedResponse", cannedResponse)
              .with(csrf()))
          .andExpect(status().is3xxRedirection())
          .andExpect(redirectedUrl("/cannedResponse"));

          verify(cannedResponseService).deleteResponse(cannedResponse);
          
      }
}