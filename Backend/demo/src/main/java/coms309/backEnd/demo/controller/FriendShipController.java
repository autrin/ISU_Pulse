package coms309.backEnd.demo.controller;

import coms309.backEnd.demo.repository.FriendShipRepository;
import coms309.backEnd.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/friendShip")
public class FriendShipController {
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final FriendShipRepository friendShipRepository;


    public FriendShipController(UserRepository userRepository, FriendShipRepository friendShipRepository) {
        this.userRepository = userRepository;
        this.friendShipRepository = friendShipRepository;
    }
}
