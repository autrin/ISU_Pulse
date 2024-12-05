package coms309.backEnd.demo;

import coms309.backEnd.demo.entity.Profile;
import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.entity.UserType;
import coms309.backEnd.demo.repository.DepartmentRepository;
import coms309.backEnd.demo.repository.FacultyRepository;
import coms309.backEnd.demo.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * NhatSystemTest
 *
 * This test class contains non-trivial test cases for the UserController.
 * It interacts directly with the existing database without clearing it.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class) // JUnit 5 extension for Spring
@ActiveProfiles("test") // Ensure that 'test' profile is configured appropriately
public class NhatSystemTest_UserControllers {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    /**
     * Test Case 1: Register a new student successfully.
     *
     * Objective: Ensure that registering a new student with a unique NetID succeeds.
     */
    @Test
    public void testRegisterNewStudent_Success() {
        // Generate unique identifiers to avoid conflicts
        String uniqueNetId = "s_test_" + System.currentTimeMillis();
        String uniqueEmail = "teststudent" + System.currentTimeMillis() + "@example.com";

        // Create a new User object representing a student
        User newUser = new User();
        newUser.setNetId(uniqueNetId);
        newUser.setFirstName("Test");
        newUser.setLastName("Student");
        newUser.setEmail(uniqueEmail);
        newUser.setHashedPassword("hashedpassword123"); // In real scenarios, ensure passwords are hashed
        newUser.setUserType(UserType.STUDENT);


        // Send POST request to register the new student
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(newUser)
                .when()
                .post("/users");

        // Assert that the response status code is 200 OK
        assertEquals(200, response.getStatusCode(), "Expected status code 200");

        // Assert that the response body contains the success message
        String responseBody = response.getBody().asString();
        assertEquals("User is successfully registered.", responseBody, "Expected success message");

        // Verify that the user is saved in the repository
        User savedUser = userRepository.findUserByNetId(uniqueNetId).orElse(null);
        assertNotNull(savedUser, "Saved user should not be null");
        assertEquals("Test", savedUser.getFirstName(), "First name should match");
        assertEquals("Student", savedUser.getLastName(), "Last name should match");
        assertEquals(uniqueEmail, savedUser.getEmail(), "Email should match");
        assertEquals(UserType.STUDENT, savedUser.getUserType(), "User type should be STUDENT");
        assertNotNull(savedUser.getProfile(), "Profile should not be null");

        userRepository.delete(newUser);
    }

    /**
     * Test Case 2: Attempt to register a student with an existing NetID.
     *
     * Objective: Ensure that registering a student with a duplicate NetID fails gracefully.
     */
    @Test
    public void testRegisterNewStudent_DuplicateNetId() {
        // Existing NetID to test duplicate registration
        String existingNetId = "s_existing_001";
        String existingEmail = "existingstudent@example.com";

        // Ensure the existing user is present in the database
        if (!userRepository.findUserByNetId(existingNetId).isPresent()) {
            User existingUser = new User();
            existingUser.setNetId(existingNetId);
            existingUser.setFirstName("Existing");
            existingUser.setLastName("Student");
            existingUser.setEmail(existingEmail);
            existingUser.setHashedPassword("hashedpassword456");
            existingUser.setUserType(UserType.STUDENT);

            Profile profile = new Profile();
            existingUser.setProfile(profile);
            profile.setUser(existingUser);

            userRepository.save(existingUser);
        }

        // Attempt to register a new user with the same NetID
        User duplicateUser = new User();
        duplicateUser.setNetId(existingNetId); // Duplicate NetID
        duplicateUser.setFirstName("Duplicate");
        duplicateUser.setLastName("Student");
        duplicateUser.setEmail("duplicatestudent@example.com");
        duplicateUser.setHashedPassword("hashedpassword789");
        duplicateUser.setUserType(UserType.STUDENT);

        // Create and associate a Profile
        Profile profile = new Profile();
        duplicateUser.setProfile(profile);
        profile.setUser(duplicateUser);

        // Send POST request to register the duplicate student
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(duplicateUser)
                .when()
                .post("/users");

        // Assert that the response status code is 400 Bad Request
        assertEquals(400, response.getStatusCode(), "Expected status code 400");

        // Assert that the response body contains the error message
        String responseBody = response.getBody().asString();
        assertEquals("NetID already exists.", responseBody, "Expected error message for duplicate NetID");
        userRepository.delete(userRepository.findUserByNetId(existingNetId).get());
    }

    /**
     * Test Case 3: Fetch a user by a valid NetID.
     *
     * Objective: Ensure that fetching an existing user by NetID returns the correct user details.
     */
    @Test
    public void testGetUserByValidNetId_Success() {
        // Existing NetID to fetch
        String netId = "s_fetch_001";
        String firstName = "Fetch";
        String lastName = "Tester";
        String email = "fetchtester@example.com";

        // Ensure the user exists in the database
        User existingUser = userRepository.findUserByNetId(netId).orElse(null);
        if (existingUser == null) {
            existingUser = new User();
            existingUser.setNetId(netId);
            existingUser.setFirstName(firstName);
            existingUser.setLastName(lastName);
            existingUser.setEmail(email);
            existingUser.setHashedPassword("hashedpassword000");
            existingUser.setUserType(UserType.STUDENT);

            Profile profile = new Profile();
            existingUser.setProfile(profile);
            profile.setUser(existingUser);

            userRepository.save(existingUser);
        }

        // Send GET request to fetch the user by NetID
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .when()
                .get("/users/" + netId);

        // Assert that the response status code is 200 OK
        assertEquals(200, response.getStatusCode(), "Expected status code 200");

        try {
            // Parse the response body to a JSONObject
            JSONObject jsonResponse = new JSONObject(response.getBody().asString());

            // Assert that the returned user details match the expected values
            assertEquals(netId, jsonResponse.getString("netId"), "NetID should match");
            assertEquals(firstName, jsonResponse.getString("firstName"), "First name should match");
            assertEquals(lastName, jsonResponse.getString("lastName"), "Last name should match");
            assertEquals(email, jsonResponse.getString("email"), "Email should match");
            assertEquals(UserType.STUDENT.toString(), jsonResponse.getString("userType"), "User type should be STUDENT");
        } catch (org.json.JSONException e) {
            // Print stack trace and fail the test if a JSON exception occurs
            e.printStackTrace();
            fail("JSON parsing failed: " + e.getMessage());
        }
    }

    /**
     * Test Case 4: Attempt to fetch a user with an invalid NetID.
     *
     * Objective: Ensure that fetching a non-existent user by NetID returns an appropriate error.
     */
    @Test
    public void testGetUserByInvalidNetId() {
        // Non-existent NetID to fetch
        String invalidNetId = "s_invalid_999";

        // Send GET request to fetch a user with a non-existent NetID
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .when()
                .get("/users/" + invalidNetId);

        // Assert that the response status code is 500 Internal Server Error due to IllegalStateException
        // Note: It's recommended to update the controller to return 404 Not Found instead
        assertEquals(404, response.getStatusCode(), "User Not Found");

        // Assert that the response body is empty (as per updated controller behavior)
        String responseBody = response.getBody().asString();
        System.out.println("Response body: " + responseBody);
        assertTrue(responseBody.isEmpty(), "Expected empty body for non-existent user");
    }
}