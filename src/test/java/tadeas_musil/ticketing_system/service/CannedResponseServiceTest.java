package tadeas_musil.ticketing_system.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tadeas_musil.ticketing_system.entity.CannedResponse;
import tadeas_musil.ticketing_system.repository.CannedResponseRepository;
import tadeas_musil.ticketing_system.service.impl.CannedResponseServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CannedResponseServiceTest {

  @Mock
  private CannedResponseRepository cannedResponseRepository;

  private CannedResponseService cannedResponseService;

  @BeforeEach
  private void setUp() {
    cannedResponseService = new CannedResponseServiceImpl(cannedResponseRepository);
  }

  @Test
  public void getResponseByName_shouldThrowException_givenNonExistingName() {
    when(cannedResponseRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> cannedResponseService.getResponseById(Long.valueOf(1)));
  }

  @Test
  public void getResponseByName_shouldReturnCorrectResponse_givenExistingCannedResponse() {
    CannedResponse response = new CannedResponse();
    response.setName("name");

    when(cannedResponseRepository.findById(anyLong())).thenReturn(Optional.of(response));

    CannedResponse cannedResponse = cannedResponseService.getResponseById(Long.valueOf(1));

    assertThat(cannedResponse).isEqualTo(response);
  }
}