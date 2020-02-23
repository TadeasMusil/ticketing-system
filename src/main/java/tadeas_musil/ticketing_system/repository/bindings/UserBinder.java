package tadeas_musil.ticketing_system.repository.bindings;

import java.util.List;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.StringPath;

import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.stereotype.Component;

@Component
public class UserBinder {

  // This is a little workaround to create 'search' alias
  // If we want to be able in the future to use the initial path 
  // in a different binding, we can just use a different field to create the alias
  public void bindUserSearch(QuerydslBindings bindings, StringPath initialPath, List<StringPath> paths) {
    bindings.bind(initialPath).as("search").first((initPath, value) -> {
      BooleanBuilder predicate = new BooleanBuilder(initialPath.likeIgnoreCase("%" + value + "%"));

      paths.forEach(path -> predicate.or(path.likeIgnoreCase("%" + value + "%")));
      return predicate;
    });
  }
}