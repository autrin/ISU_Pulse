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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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

        // Fetch friend list
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .when()
                .get("/friendShip/friends/" + userNetId);

        try {
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

        // Ensure the sender exists in the database
        if (!userRepository.findUserByNetId(senderNetId).isPresent()) {
            User sender = new User();
            sender.setNetId(senderNetId);
            sender.setFirstName("Sender");
            sender.setLastName("User");
            sender.setEmail("sender@example.com");
            sender.setHashedPassword("hashedpassword123");
            sender.setUserType(UserType.STUDENT);
            userRepository.save(sender);
        }

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

        // Ensure both users exist in the database
        if (!userRepository.findUserByNetId(userNetId1).isPresent()) {
            User user1 = new User();
            user1.setNetId(userNetId1);
            user1.setFirstName("User");
            user1.setLastName("One");
            user1.setEmail("user1@example.com");
            user1.setHashedPassword("hashedpassword1");
            user1.setUserType(UserType.STUDENT);
            userRepository.save(user1);
        }

        if (!userRepository.findUserByNetId(userNetId2).isPresent()) {
            User user2 = new User();
            user2.setNetId(userNetId2);
            user2.setFirstName("User");
            user2.setLastName("Two");
            user2.setEmail("user2@example.com");
            user2.setHashedPassword("hashedpassword2");
            user2.setUserType(UserType.STUDENT);
            userRepository.save(user2);
        }

        // Ensure no friendship exists between the two users
        Optional<FriendShip> friendship = friendShipRepository.findFriendShipBetweenUsers(
                userRepository.findUserByNetId(userNetId1).get(),
                userRepository.findUserByNetId(userNetId2).get()
        );
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
