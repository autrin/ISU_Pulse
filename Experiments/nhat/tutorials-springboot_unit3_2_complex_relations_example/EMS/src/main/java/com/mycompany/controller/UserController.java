package com.mycompany.controller;

import com.mycompany.model.Role;
import com.mycompany.vo.LoginVO;
import com.mycompany.vo.Message;
import com.mycompany.model.User;
import com.mycompany.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping(value = "/registerUser", produces = "application/json")
    public @ResponseBody Message registerUser(@RequestBody User user){
        return userService.registerUser(user.getEmail(), user.getPassword(), user.getName(), user.getRole());
    }


    @PutMapping(value = "/updateUserName/{name}", produces = "application/json")
    public @ResponseBody Message updateUserName(@RequestBody LoginVO loginVO, @PathVariable String name){
        return userService.updateUserName(loginVO, name);
    }


    @GetMapping(value = "/getUser", produces = "application/json")
    public @ResponseBody User getUser(@RequestBody LoginVO loginVO){
        return userService.validateUser(loginVO);
    }



    @GetMapping(value = "/getAllUsers", produces = "application/json")
    public @ResponseBody List<User> getAllUsers(){
        return userService.getAllUsers();
    }



    @GetMapping(value = "/getUserObjectExample", produces = "application/json")
    public @ResponseBody User getUserObjectExample(){
        return new User("email here","password here", "name here", Role.ADMIN);
    }

    @GetMapping(value = "/getLoginVOExample", produces = "application/json")
    public @ResponseBody LoginVO getLoginVOObjectExample(){
        LoginVO example = new LoginVO();
        example.setEmail("email here");
        example.setPassword("password here");
        return example;
    }
}
