package ecommerce.junior.service;

import ecommerce.junior.model.Grupo;
import ecommerce.junior.model.User;
import ecommerce.junior.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void createUser(User user){

        userRepository.save(user);
    }



}
