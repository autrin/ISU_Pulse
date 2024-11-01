package com.mycompany.service;

import com.mycompany.model.Role;
import com.mycompany.vo.LoginVO;
import com.mycompany.vo.Message;
import com.mycompany.model.User;
import com.mycompany.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public Message updateUserName(LoginVO loginVO, String name){
        User user = userRepository.findByEmailAndPassword(loginVO.getEmail(), loginVO.getPassword());
        if (user == null){
            return new Message("User's credentials Don't Exist", 1);
        }
        user.setName(name);
        user.setModifiedOn(new Date());
        userRepository.save(user);
        return new Message("Change Name Successful", 0);
    }

    public Message registerUser(String email, String password, String name, Role role){

        User user = new User(email, password, name, role);
        try {
            userRepository.save(user);
        }catch (Exception e){
            return new Message("ERROR: Email has to be unique", 1);
        }
        return new Message("Register Successful", 0);
    }

    public User validateUser(LoginVO loginVO){
        return userRepository.findByEmailAndPassword(loginVO.getEmail(), loginVO.getPassword());
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
}
