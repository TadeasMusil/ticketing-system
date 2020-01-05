package tadeas_musil.ticketing_system.security;

import java.io.Serializable;
import java.util.List;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import tadeas_musil.ticketing_system.entity.Department;
import tadeas_musil.ticketing_system.entity.Ticket;
import tadeas_musil.ticketing_system.service.DepartmentService;

@RequiredArgsConstructor
@Component
public class TicketPermissionEvaluator implements PermissionEvaluator {

    private final DepartmentService departmentService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if ((authentication == null) || !(targetDomainObject instanceof Ticket) || !(permission instanceof String)){
            return false;
        }
        Ticket ticket = (Ticket) targetDomainObject;
        if (permission.equals("read")) {
            if (userHasRole("ADMIN", authentication)) {
                return true;
            } 
            else if (userHasRole("STAFF", authentication)) {
                List<Department> departments = departmentService.getDepartmentsByUsername(authentication.getName());
                return departments.contains(ticket.getDepartment());
            } 
            else if (userHasRole("USER", authentication)) {
                return ticket.getAuthor().equals(authentication.getName());
            }
        }
        throw new UnsupportedOperationException("hasPermission not supported");
    }

    private boolean userHasRole(String role, Authentication authentication) {
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
            Object permission) {
        throw new UnsupportedOperationException("hasPermission not supported");
    }

}