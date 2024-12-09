package coms309.backEnd.demo;

import coms309.backEnd.demo.entity.FriendRequest;
import coms309.backEnd.demo.entity.FriendShip;
import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.entity.UserType;
import coms309.backEnd.demo.repository.FriendRequestRepository;
import coms309.backEnd.demo.repository.FriendShipRepository;
import coms309.backEnd.demo.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MinhSystemTest
 *
 * This test class contains system-level test cases for Friend Request and Friendship features.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class MinhSystemTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private FriendShipRepository friendShipRepository;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    /**
     * Test Case 1: Get Friend List.
     *
     * Objective: Ensure that the friend list retrieval works correctly.
     */
    @Test
    public void testGetFriendList_Success() {
        // Setup users and friendship
        String user1NetId = "friend_user1";
        String user2NetId = "friend_user2";

        User user1 = createUser(user1NetId, "User1", "Friend", "user1@example.com");
        User user2 = createUser(user2NetId, "User2", "Friend", "user2@example.com");

        createFriendShip(user1, user2);

        try {
            // Fetch friend list
            Response response = RestAssured.given()
                    .header("Content-Type", "application/json")
                    .when()
                    .get("/friendShip/friends/" + user1NetId);

            // Verify response
            assertEquals(200, response.getStatusCode(), "Expected status code 200");
            JSONArray friends = new JSONArray(response.getBody().asString());
            assertEquals(1, friends.length(), "User1 should have one friend");

            JSONObject friend = friends.getJSONObject(0);
            assertEquals(user2NetId, friend.getString("netId"), "Friend NetID should match User2's NetID");

        } catch (JSONException e) {
            fail("JSON parsing failed: " + e.getMessage());
        } finally {
            // Clean up: Remove test-specific friendships and users
            friendShipRepository.findFriendShipBetweenUsers(user1, user2)
                    .ifPresent(friendShipRepository::delete);

            userRepository.delete(user1);
            userRepository.delete(user2);
        }
    }

    /**
     * Test Case 2: Unfriend a User.
     *
     * Objective: Ensure that unfriending a user works correctly.
     */
    @Test
    public void testUnfriendUser_Success() {
        // Setup users and friendship
        String user1NetId = "unfriend_user1";
        String user2NetId = "unfriend_user2";

        User user1 = createUser(user1NetId, "User1", "Unfriend", "user1@example.com");
        User user2 = createUser(user2NetId, "User2", "Unfriend", "user2@example.com");

        createFriendShip(user1, user2);

        try {
            // Unfriend user
            Response response = RestAssured.given()
                    .header("Content-Type", "application/json")
                    .queryParam("userNetId1", user1NetId)
                    .queryParam("userNetId2", user2NetId)
                    .when()
                    .delete("/friendShip/unfriend");

            // Verify response
            assertEquals(200, response.getStatusCode(), "Expected status code 200");
            assertEquals("Unfriended successfully.", response.getBody().asString(), "Expected success message");

            // Verify in database
            assertFalse(friendShipRepository.findFriendShipBetweenUsers(user1, user2).isPresent(), "Friendship should no longer exist");
        } finally {
            // Clean up: Remove test-specific users
            userRepository.delete(user1);
            userRepository.delete(user2);
        }
    }

    /**
     * Test Case 3: Get Friend List - Multiple Friends.
     *
     * Objective: Ensure that retrieving a friend list for a user with multiple friends returns all friends accurately.
     */
    @Test
    public void testGetFriendList_MultipleFriends() {
        // Setup users and friendships
        String userNetId = "main_user";
        String friendNetId1 = "friend_1";
        String friendNetId2 = "friend_2";
        String friendNetId3 = "friend_3";

        // Create the main user and their friends
        User user = createUser(userNetId, "Main", "User", "mainuser@example.com");
        User friend1 = createUser(friendNetId1, "Friend1", "User", "friend1@example.com");
        User friend2 = createUser(friendNetId2, "Friend2", "User", "friend2@example.com");
        User friend3 = createUser(friendNetId3, "Friend3", "User", "friend3@example.com");

        // Create friendships
        createFriendShip(user, friend1);
        createFriendShip(user, friend2);
        createFriendShip(user, friend3);

        try {
            // Fetch friend list
            Response response = RestAssured.given()
                    .header("Content-Type", "application/json")
                    .when()
                    .get("/friendShip/friends/" + userNetId);

            // Verify response
            assertEquals(200, response.getStatusCode(), "Expected status code 200");

            JSONArray friends = new JSONArray(response.getBody().asString());
            assertEquals(3, friends.length(), "User should have three friends");

            // Verify each friend's details
            Set<String> expectedNetIds = new HashSet<>(Arrays.asList(friendNetId1, friendNetId2, friendNetId3));
            for (int i = 0; i < friends.length(); i++) {
                JSONObject friend = friends.getJSONObject(i);
                String friendNetId = friend.getString("netId");
                assertTrue(expectedNetIds.contains(friendNetId), "Friend NetID should be one of the expected values");
                expectedNetIds.remove(friendNetId); // Remove to ensure all are accounted for
            }

            assertTrue(expectedNetIds.isEmpty(), "All expected friends should be included in the response");

        } catch (org.json.JSONException e) {
            fail("JSONException occurred: " + e.getMessage());
        } finally {
            // Clean up: Remove friendships
            friendShipRepository.delete(friendShipRepository.findFriendShipBetweenUsers(user, friend1).get());
            friendShipRepository.delete(friendShipRepository.findFriendShipBetweenUsers(user, friend2).get());
            friendShipRepository.delete(friendShipRepository.findFriendShipBetweenUsers(user, friend3).get());

            // Clean up: Remove users
            userRepository.delete(user);
            userRepository.delete(friend1);
            userRepository.delete(friend2);
            userRepository.delete(friend3);
        }
    }

    /**
     * Test Case 4: Send Friend Request - Null Receiver.
     *
     * Objective: Ensure that attempting to send a friend request to a null receiver
     * is handled gracefully and returns the appropriate error response.
     */
    @Test
    public void testSendFriendRequest_NullReceiver() {
        // Arrange
        String senderNetId = "valid_sender"; // Assuming this is a valid NetID
        String receiverNetId = null; // Null receiver

        User sender = null;

        try {
            // Ensure the sender exists in the database
            sender = userRepository.findUserByNetId(senderNetId).orElseGet(() -> {
                User newSender = new User();
                newSender.setNetId(senderNetId);
                newSender.setFirstName("Sender");
                newSender.setLastName("User");
                newSender.setEmail("sender@example.com");
                newSender.setHashedPassword("hashedpassword123");
                newSender.setUserType(UserType.STUDENT);
                return userRepository.save(newSender);
            });

            // Act
            Response response = RestAssured.given()
                    .header("Content-Type", "application/json")
                    .queryParam("senderNetId", senderNetId)
                    .queryParam("receiverNetId", receiverNetId) // Null value
                    .when()
                    .post("/friendRequest/request");

            // Assert
            assertEquals(404, response.getStatusCode(), "Expected status code 404 for null receiver");
            String responseBody = response.getBody().asString();
            assertEquals("User with ID  not found.", responseBody, "Expected error message for null receiver");

        } finally {
            // Clean up: Remove the sender user if it was created for this test
            if (sender != null) {
                userRepository.delete(sender);
            }
        }
    }

    /**
     * Test Case 5: Unfriend Non-Existent Friendship.
     *
     * Objective: Ensure that attempting to unfriend a user with whom no friendship exists
     * returns the appropriate error response.
     */
    @Test
    public void testUnfriend_NoExistingFriendship() {
        // Arrange
        String userNetId1 = "user1";
        String userNetId2 = "user2";

        User user1 = null;
        User user2 = null;

        try {
            // Ensure both users exist in the database
            user1 = userRepository.findUserByNetId(userNetId1).orElseGet(() -> {
                User newUser1 = new User();
                newUser1.setNetId(userNetId1);
                newUser1.setFirstName("User");
                newUser1.setLastName("One");
                newUser1.setEmail("user1@example.com");
                newUser1.setHashedPassword("hashedpassword1");
                newUser1.setUserType(UserType.STUDENT);
                return userRepository.save(newUser1);
            });

            user2 = userRepository.findUserByNetId(userNetId2).orElseGet(() -> {
                User newUser2 = new User();
                newUser2.setNetId(userNetId2);
                newUser2.setFirstName("User");
                newUser2.setLastName("Two");
                newUser2.setEmail("user2@example.com");
                newUser2.setHashedPassword("hashedpassword2");
                newUser2.setUserType(UserType.STUDENT);
                return userRepository.save(newUser2);
            });

            // Ensure no friendship exists between the two users
            Optional<FriendShip> friendship = friendShipRepository.findFriendShipBetweenUsers(user1, user2);
            friendship.ifPresent(friendShipRepository::delete);

            // Act
            Response response = RestAssured.given()
                    .header("Content-Type", "application/json")
                    .queryParam("userNetId1", userNetId1)
                    .queryParam("userNetId2", userNetId2)
                    .when()
                    .delete("/friendRequest/unfriend");

            // Assert
            assertEquals(404, response.getStatusCode(), "Expected status code 404 for no existing friendship");

        } finally {
            // Clean up: Remove test-specific users
            if (user1 != null) {
                userRepository.delete(user1);
            }
            if (user2 != null) {
                userRepository.delete(user2);
            }
        }
    }
    /**
     * Test Case 6: Reject Non-Existent Friend Request.
     *
     * Objective: Ensure that rejecting a friend request that does not exist
     * returns the appropriate error response.
     */
    @Test
    public void testRejectFriendRequest_NoRequestExists() {
        // Arrange: Define sender and receiver NetIDs
        String senderNetId = "test_sender";
        String receiverNetId = "test_receiver";

        User sender = null;
        User receiver = null;

        try {
            // Ensure sender exists in the database
            sender = userRepository.findUserByNetId(senderNetId).orElseGet(() -> {
                User newUser = new User(senderNetId, "Sender", "Test", "sender@example.com", "hashedpassword", UserType.STUDENT);
                return userRepository.save(newUser);
            });

            // Ensure receiver exists in the database
            receiver = userRepository.findUserByNetId(receiverNetId).orElseGet(() -> {
                User newUser = new User(receiverNetId, "Receiver", "Test", "receiver@example.com", "hashedpassword", UserType.STUDENT);
                return userRepository.save(newUser);
            });

            // Ensure no friend request exists between sender and receiver
            friendRequestRepository.findFriendRequestBySenderAndReceiver(sender, receiver)
                    .ifPresent(friendRequestRepository::delete);

            // Act: Attempt to reject a friend request that doesn't exist
            Response response = RestAssured.given()
                    .header("Content-Type", "application/json")
                    .queryParam("senderNetId", senderNetId)
                    .queryParam("receiverNetId", receiverNetId)
                    .when()
                    .delete("/friendRequest/reject");

            // Assert: Verify the response status and message
            assertEquals(404, response.getStatusCode(), "Expected status code 404 for non-existent friend request");
            assertEquals("Friend request not exist", response.getBody().asString(), "Expected error message for non-existent friend request");
        } finally {
            // Clean up: Remove test-specific users
            if (sender != null) {
                userRepository.delete(sender);
            }
            if (receiver != null) {
                userRepository.delete(receiver);
            }
        }
    }

    /**
     * Test Case 7: Display Common Friends.
     *
     * Objective: Verify that the API correctly identifies and returns the common friends
     * between two users who share mutual friendships.
     */
    @Test
    public void testDisplayCommonFriends() {
        // Arrange: Create users
        User userA = createUser("userA", "Alice", "Anderson", "alice@example.com");
        User userB = createUser("userB", "Bob", "Brown", "bob@example.com");
        User userC = createUser("userC", "Charlie", "Clark", "charlie@example.com");
        User userD = createUser("userD", "David", "Davis", "david@example.com");

        // Arrange: Create friendships
        createFriendShip(userA, userB); // Common Friend
        createFriendShip(userA, userC); // Common Friend
        createFriendShip(userA, userD); // Unique Friend

        try {
            // Act: Call the API to get common friends
            Response response = RestAssured.given()
                    .header("Content-Type", "application/json")
                    .queryParam("netIdUser1", "userB")
                    .queryParam("netIdUser2", "userC")
                    .when()
                    .get("/friendShip/sameFriends");

            // Assert: Verify the response
            assertEquals(200, response.getStatusCode(), "Expected status code 200");
            List<User> friendsInCommon = Arrays.asList(response.getBody().as(User[].class));
            assertNotNull(friendsInCommon, "Friends in common should not be null");
            assertEquals(1, friendsInCommon.size(), "Expected 1 common friends");
            assertTrue(friendsInCommon.stream().anyMatch(friend -> friend.getNetId().equals("userA")), "Expected userA as a common friend");
        } finally {
            // Clean up: Remove test-specific friendships
            friendShipRepository.delete(friendShipRepository.findFriendShipBetweenUsers(userA, userB).get());
            friendShipRepository.delete(friendShipRepository.findFriendShipBetweenUsers(userA, userC).get());
            friendShipRepository.delete(friendShipRepository.findFriendShipBetweenUsers(userA, userD).get());

            // Clean up: Remove test-specific users
            userRepository.delete(userA);
            userRepository.delete(userB);
            userRepository.delete(userC);
            userRepository.delete(userD);
        }
    }


    /**
     * Utility: Create a user.
     */
    private User createUser(String netId, String firstName, String lastName, String email) {
        User user = userRepository.findUserByNetId(netId).orElse(null);
        if (user == null) {
            user = new User(netId, firstName, lastName, email, "hashedpassword", UserType.STUDENT);
            userRepository.save(user);
        }
        return user;
    }

    /**
     * Utility: Create a friendship.
     */
    private void createFriendShip(User user1, User user2) {
        if (!friendShipRepository.findFriendShipBetweenUsers(user1, user2).isPresent()) {
            FriendShip friendship = new FriendShip(user1, user2);
            friendShipRepository.save(friendship);
        }
    }


}
