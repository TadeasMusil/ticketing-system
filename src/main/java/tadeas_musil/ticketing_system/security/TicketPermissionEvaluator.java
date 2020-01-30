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

    private Authentication authentication;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if ((authentication == null) || !(targetDomainObject instanceof Ticket) || !(permission instanceof String)) {
            return false;
        }
        this.authentication = authentication;
        Ticket ticket = (Ticket) targetDomainObject;
        if (userHasRole("ADMIN")) {
            return true;
        }
        if (userHasRole("STAFF")) {
            
            if (permission.equals("edit")) {
                return isUserInSameDepartmentAs(ticket);
            }
            else if(permission.equals("read")){
                return isUserAuthorOf(ticket) || isUserInSameDepartmentAs(ticket);
            }
            else if(permission.equals("respond")){
                return isUserAuthorOf(ticket) || isUserInSameDepartmentAs(ticket);
            }
        } 
        else if (userHasRole("USER")) {
            if (permission.equals("read")) {
                return isUserAuthorOf(ticket);
            }
            else if(permission.equals("respond")){
                return isUserAuthorOf(ticket);
            } 
            else if (permission.equals("edit")) {
                return false;
            } 
        }
        throw new UnsupportedOperationException("Unsupported hasPermission");
    }

    private boolean userHasRole(String role) {
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority(role));
    }

    private boolean isUserAuthorOf(Ticket ticket){
        return ticket.getAuthor().equals(authentication.getName());
    }
    private boolean isUserInSameDepartmentAs(Ticket ticket){
        List<Department> departments = departmentService.getDepartmentsByUsername(authentication.getName());
        return departments.contains(ticket.getDepartment());
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
            Object permission) {
        throw new UnsupportedOperationException("Unsupported hasPermission");
    }

}