package tadeas_musil.ticketing_system.security.permission;

import java.io.Serializable;
import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
@Primary
public class GlobalPermissionEvaluator implements PermissionEvaluator {

    private final List<TargetPermissionEvaluator> evaluators;

    @Override
    public boolean hasPermission(Authentication authentication, Object target, Object permission) {
        if ((authentication == null) || (target == null) || !(permission instanceof String)) {
            return false;
        }

        for (TargetPermissionEvaluator evaluator : evaluators) {
            if (evaluator.getTargetClass().isAssignableFrom(target.getClass())) {
                return evaluator.hasPermission(authentication, target, permission);
            }
        }

        throw new UnsupportedOperationException("Unsupported hasPermission");
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
            Object permission) {
        throw new UnsupportedOperationException("Unsupported hasPermission");
    }

}