package tadeas_musil.ticketing_system.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import tadeas_musil.ticketing_system.entity.Department;
import tadeas_musil.ticketing_system.entity.Ticket;
import tadeas_musil.ticketing_system.service.DepartmentService;


@ExtendWith(SpringExtension.class)
public class TicketPermissionEvaluatorTest {

    private TicketPermissionEvaluator ticketPermissionEvaluator;

    @Mock
    private DepartmentService departmentService;

    @BeforeEach
    public void setUp() {
        ticketPermissionEvaluator = new TicketPermissionEvaluator(departmentService);
    }

    @Test
    @WithMockUser
    public void hasPermission_shouldReturnFalse_givenNullPermission() {
        Ticket ticket = new Ticket();

        boolean hasPermission = ticketPermissionEvaluator
                .hasPermission(SecurityContextHolder.getContext().getAuthentication(), ticket, null);

        assertThat(hasPermission).isFalse();
    }

    @Test
    @WithMockUser
    public void hasPermission_shouldReturnFalse_givenNullTicket() {

        boolean hasPermission = ticketPermissionEvaluator
                .hasPermission(SecurityContextHolder.getContext().getAuthentication(), null, "read");

        assertThat(hasPermission).isFalse();
    }

    @Test
    @WithAnonymousUser
    public void hasPermission_shouldReturnFalse_givenAnonymousUser() {
        Ticket ticket = new Ticket();

        boolean hasPermission = ticketPermissionEvaluator
                .hasPermission(SecurityContextHolder.getContext().getAuthentication(), ticket, "read");

        assertThat(hasPermission).isFalse();
    }

    @Test
    @WithMockUser
    public void hasPermission_shouldReturnFalse_givenStringInsteadOfTicket() {
        boolean hasPermission = ticketPermissionEvaluator
                .hasPermission(SecurityContextHolder.getContext().getAuthentication(), "string", "read");

        assertThat(hasPermission).isFalse();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void hasPermission_shouldReturnTrue_givenPermissionReadAndRoleAdmin() {
        Ticket ticket = new Ticket();

        boolean hasPermission = ticketPermissionEvaluator
                .hasPermission(SecurityContextHolder.getContext().getAuthentication(), ticket, "read");

        assertThat(hasPermission).isTrue();
    }

    @Test
    @WithMockUser(roles = "STAFF")
    public void hasPermission_shouldReturnTrue_givenPermissionReadAndRoleStaffAndSameDepartment() {
        Department department = new Department();
        department.setName("departmentName");

        Ticket ticket = new Ticket();
        ticket.setDepartment(department);

        when(departmentService.getDepartmentsByUsername(anyString())).thenReturn(List.of(department));

        boolean hasPermission = ticketPermissionEvaluator
                .hasPermission(SecurityContextHolder.getContext().getAuthentication(), ticket, "read");

        assertThat(hasPermission).isTrue();
    }

    @Test
    @WithMockUser(roles = "STAFF")
    public void hasPermission_shouldReturnFalse_givenPermissionReadAndRoleStaffAndDifferentDepartment() {
        Department userDepartment = new Department();
        userDepartment.setName("userDepartment");
        when(departmentService.getDepartmentsByUsername(anyString())).thenReturn(List.of(userDepartment));

        Department ticketDepartment = new Department();
        ticketDepartment.setName("ticketDepartment");
        Ticket ticket = new Ticket();
        ticket.setDepartment(ticketDepartment);

        boolean hasPermission = ticketPermissionEvaluator
                .hasPermission(SecurityContextHolder.getContext().getAuthentication(), ticket, "read");

        assertThat(hasPermission).isFalse();
    }

    @Test
    @WithMockUser(username = "author", roles = "USER")
    public void hasPermission_shouldReturnFalse_givenPermissionReadAndRoleUserAndUserIsAuthor() {
        Ticket ticket = new Ticket();
        ticket.setAuthor("author");

        boolean hasPermission = ticketPermissionEvaluator
                .hasPermission(SecurityContextHolder.getContext().getAuthentication(), ticket, "read");

        assertThat(hasPermission).isTrue();
    }

    @Test
    @WithMockUser(username = "notAuthor", roles = "USER")
    public void hasPermission_shouldReturnFalse_givenPermissionReadAndRoleUserAndUserIsNotAuthor() {
        Ticket ticket = new Ticket();
        ticket.setAuthor("author");

        boolean hasPermission = ticketPermissionEvaluator
                .hasPermission(SecurityContextHolder.getContext().getAuthentication(), ticket, "read");

        assertThat(hasPermission).isFalse();
    }

    @Test
    @WithMockUser
    public void hasPermission_shouldThrowUnsupportedOperationException_givenNonExistentPermission() {
        Ticket ticket = new Ticket();

        assertThrows(UnsupportedOperationException.class,
                () -> ticketPermissionEvaluator.hasPermission(SecurityContextHolder.getContext().getAuthentication(),
                        ticket, "nonExistentPermission"));
    }

}