package tadeas_musil.ticketing_system.repository.bindings;

import java.util.List;

import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import tadeas_musil.ticketing_system.entity.QUser;
@Component
@RequiredArgsConstructor
public class UserListBindings implements QuerydslBinderCustomizer<QUser> {

  private final UserBinder userBinder;

    @Override
    public void customize(QuerydslBindings bindings, QUser user) {
    bindings.including(
      user.isDisabled,
      user.firstName
      );
    bindings.excludeUnlistedProperties(true);

    userBinder.bindUserSearch(bindings, user.firstName, List.of(user.lastName, user.username));
    }
}