package tadeas_musil.ticketing_system.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import tadeas_musil.ticketing_system.entity.Ticket;
import tadeas_musil.ticketing_system.security.permission.GlobalPermissionEvaluator;

@ExtendWith(SpringExtension.class)
public class GlobalPermissionEvaluatorTest {

    private GlobalPermissionEvaluator globalPermissionEvaluator;

    @BeforeEach
    public void setUp() {
        globalPermissionEvaluator = new GlobalPermissionEvaluator(new ArrayList<>());
    }

    @Test
    @WithMockUser
    public void hasPermission_shouldReturnFalse_givenNullPermission() {
        boolean hasPermission = globalPermissionEvaluator
                .hasPermission(SecurityContextHolder.getContext().getAuthentication(), new Ticket(), null);

        assertThat(hasPermission).isFalse();
    }

    @Test
    @WithMockUser
    public void hasPermission_shouldReturnFalse_givenNullTicket() {

        boolean hasPermission = globalPermissionEvaluator
                .hasPermission(SecurityContextHolder.getContext().getAuthentication(), null, "read");

        assertThat(hasPermission).isFalse();
    }

    @Test
    public void hasPermission_shouldReturnFalse_givenAnonymousUser() {
        boolean hasPermission = globalPermissionEvaluator
                .hasPermission(SecurityContextHolder.getContext().getAuthentication(), new Ticket(), "read");

        assertThat(hasPermission).isFalse();
    }
}