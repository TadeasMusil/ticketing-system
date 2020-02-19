package tadeas_musil.ticketing_system.security.permission;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.stereotype.Component;


@Component
public interface TargetPermissionEvaluator extends PermissionEvaluator{

    Class getTargetClass();
}