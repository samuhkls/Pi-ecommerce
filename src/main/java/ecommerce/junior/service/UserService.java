package ecommerce.junior.service;

import ecommerce.junior.model.Grupo;
import ecommerce.junior.model.User;
import ecommerce.junior.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public void createUser(User user){
        userRepository.save(user);
    }

}
