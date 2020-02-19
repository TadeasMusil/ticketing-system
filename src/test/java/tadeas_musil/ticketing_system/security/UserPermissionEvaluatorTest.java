package tadeas_musil.ticketing_system.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import tadeas_musil.ticketing_system.entity.Role;
import tadeas_musil.ticketing_system.entity.User;
import tadeas_musil.ticketing_system.security.permission.UserPermissionEvaluator;


@ExtendWith(SpringExtension.class)
public class UserPermissionEvaluatorTest {

    private UserPermissionEvaluator userPermissionEvaluator;

    @BeforeEach
    public void setUp() {
        userPermissionEvaluator = new UserPermissionEvaluator();
    }


    @Test
    @WithMockUser(authorities = "ADMIN")
    public void hasPermission_shouldReturnTrue_givenPermissionReadAndRoleAdmin() {
        User user = new User();

        boolean hasPermission = userPermissionEvaluator
                .hasPermission(SecurityContextHolder.getContext().getAuthentication(), user, "read");

        assertThat(hasPermission).isTrue();
    }

    @Test
    @WithMockUser(authorities = "STAFF")
    public void hasPermission_shouldReturnTrue_whenEditingAccountStatusOfUserWithLowerRole() {
        User user = new User();
        user.getRoles().add(new Role("USER"));

        

        boolean hasPermission = userPermissionEvaluator
                .hasPermission(SecurityContextHolder.getContext().getAuthentication(), user, "edit_account_status");

        assertThat(hasPermission).isTrue();
    }

    @ParameterizedTest
	@ValueSource(strings = {"STAFF", "ADMIN"})
    @WithMockUser(authorities = "STAFF")
    public void hasPermission_shouldReturnFalse_whenEditingAccountStatusOfUserWithEqualOrHigherRole(String role) {
        User user = new User();
        user.getRoles().add(new Role(role));

        boolean hasPermission = userPermissionEvaluator
                .hasPermission(SecurityContextHolder.getContext().getAuthentication(), user, "edit_account_status");

        assertThat(hasPermission).isFalse();
    }

    @ParameterizedTest
	@ValueSource(strings = {"ADMIN", "STAFF", "USER"})
    @WithMockUser(authorities = "USER")
    public void hasPermission_shouldReturnFalse_whenEditingAccountStatus(String role) {
		User user = new User();
        user.getRoles().add(new Role(role));

        boolean hasPermission = userPermissionEvaluator
                .hasPermission(SecurityContextHolder.getContext().getAuthentication(), user, "edit_account_status");

        assertThat(hasPermission).isFalse();
    }

    @Test
    @WithMockUser
    public void hasPermission_shouldThrowUnsupportedOperationException_givenNonExistentPermission() {
        User user = new User();

        assertThrows(UnsupportedOperationException.class,
                () -> userPermissionEvaluator.hasPermission(SecurityContextHolder.getContext().getAuthentication(),
                        user, "nonExistentPermission"));
    }
}