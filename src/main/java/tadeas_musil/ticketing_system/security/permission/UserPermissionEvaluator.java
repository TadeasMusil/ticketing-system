package tadeas_musil.ticketing_system.security.permission;

import java.io.Serializable;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import tadeas_musil.ticketing_system.entity.Role;
import tadeas_musil.ticketing_system.entity.User;

@RequiredArgsConstructor
@Component
public class UserPermissionEvaluator implements TargetPermissionEvaluator {

    private Authentication authentication;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        this.authentication = authentication;
        User targetUser = (User) targetDomainObject;
        if (currentUserHasRole("ADMIN")) {
            return true;
        }
        if (currentUserHasRole("STAFF")) {

            if (permission.equals("edit_account_status")) {
                return userHasRole(targetUser, "USER");
            }
        } else if (currentUserHasRole("USER")) {
            return false;
        }
        throw new UnsupportedOperationException("Unsupported hasPermission");
    }

    private boolean currentUserHasRole(String role) {
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority(role));
    }

    private boolean userHasRole(User user, String role) {
        return user.getRoles().contains(new Role(role));
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
            Object permission) {
        throw new UnsupportedOperationException("Unsupported hasPermission");
    }

    @Override
    public Class getTargetClass() {
        return User.class;
    }

}