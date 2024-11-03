package coms309.backEnd.demo.controller;

import coms309.backEnd.demo.entity.FriendShip;
import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.repository.FriendShipRepository;
import coms309.backEnd.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    private List<User> getFriendsfromFriendships(List<FriendShip> friendShips, User user){
        List<User> friendlst = new ArrayList<>();
        for(FriendShip friendShip : friendShips){
            if(friendShip.getUser1().getId() == user.getId()){
                friendlst.add(friendShip.getUser2());
            }
            else if (friendShip.getUser1().getId() != user.getId()) {
                friendlst.add(friendShip.getUser1());
            }
        }
        return friendlst;
    }

    @GetMapping("/friends/{netId}")
    public ResponseEntity<List<User>> displayFriendList(@PathVariable String netId){
        Optional<User> curUser = userRepository.findUserByNetId(netId);
        if(curUser.isEmpty()){
            return  ResponseEntity.internalServerError().build();
        }
        User user = curUser.get();
        List<FriendShip> friendShips = user.getFriendShips();
        List<User> friendList = getFriendsfromFriendships(friendShips,user);
        return ResponseEntity.ok(friendList);
    }



    @GetMapping("/sameFriends")
    public ResponseEntity<List<User>> displayingFriendsInCommon(
            @RequestParam String netIdUser1,
            @RequestParam String netIdUser2){

        Optional<User> curUser1 = userRepository.findUserByNetId(netIdUser1);
        if(curUser1.isEmpty()){
            return  ResponseEntity.internalServerError().build();
        }
        User user1 = curUser1.get();

        Optional<User> curUser2 = userRepository.findUserByNetId(netIdUser2);
        if(curUser2.isEmpty()){
            return  ResponseEntity.internalServerError().build();
        }
        User user2 = curUser2.get();

        List<FriendShip> friendShips1 = user1.getFriendShips();
        List<User> friendLst1 = getFriendsfromFriendships(friendShips1,user1);

        List<FriendShip> friendShips2 = user2.getFriendShips();
        List<User> friendLst2 = getFriendsfromFriendships(friendShips2,user2);

        List<User> friendsInCommon = new ArrayList<>();
        for(User userFromFriendLst1 : friendLst1){
            boolean isInCommon = false;
            for(User userFromFriendLst2 : friendLst2){
                if (userFromFriendLst1.getId() == userFromFriendLst2.getId()) {
                    isInCommon = true;
                    break;
                }
            }
            if(isInCommon){
                friendsInCommon.add(userFromFriendLst1);
            }
        }
        return ResponseEntity.ok(friendsInCommon);
    }
}
