package tadeas_musil.ticketing_system.service;

import org.springframework.stereotype.Service;

import tadeas_musil.ticketing_system.entity.User;

@Service
public interface UserService {

    User createUser(User user);

}