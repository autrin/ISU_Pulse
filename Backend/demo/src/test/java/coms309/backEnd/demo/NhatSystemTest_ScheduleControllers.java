package coms309.backEnd.demo;

import coms309.backEnd.demo.entity.Course;
import coms309.backEnd.demo.entity.Enroll;
import coms309.backEnd.demo.entity.Schedule;
import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.entity.UserType;
import coms309.backEnd.demo.repository.CourseRepository;
import coms309.backEnd.demo.repository.EnrollRepository;
import coms309.backEnd.demo.repository.ScheduleRepository;
import coms309.backEnd.demo.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ScheduleControllerTest
 *
 * This test class contains non-trivial test cases for the ScheduleController.
 * It interacts directly with the existing database without clearing it.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class) // JUnit 5 extension for Spring
@ActiveProfiles("test") // Ensure that 'test' profile is configured appropriately
public class NhatSystemTest_ScheduleControllers {

    @LocalServerPort
    private int port;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollRepository enrollRepository;

    // Lists to keep track of created test data for cleanup
    private List<User> testUsers = new ArrayList<>();
    private List<Course> testCourses = new ArrayList<>();
    private List<Schedule> testSchedules = new ArrayList<>();
    private List<Enroll> testEnrolls = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @AfterEach
    public void tearDown() {
        // Delete Enrolls first due to foreign key constraints
        for (Enroll enroll : testEnrolls) {
            enrollRepository.delete(enroll);
        }
        testEnrolls.clear();

        // Delete Schedules
        for (Schedule schedule : testSchedules) {
            scheduleRepository.delete(schedule);
        }
        testSchedules.clear();

        // Delete Courses
        for (Course course : testCourses) {
            courseRepository.delete(course);
        }
        testCourses.clear();

        // Delete Users
        for (User user : testUsers) {
            userRepository.delete(user);
        }
        testUsers.clear();
    }

    /**
     * Helper method to create a user with enrollments and schedules.
     *
     * @param netId      Unique NetID for the user.
     * @param firstName  First name of the user.
     * @param lastName   Last name of the user.
     * @param email      Email of the user.
     * @param userType   Type of the user (e.g., STUDENT).
     * @param courseCodes  List of course codes the user is enrolling in.
     * @return The created User object.
     */
    private User createUserWithEnrollments(String netId, String firstName, String lastName, String email, UserType userType, List<String> courseCodes) {
        User user = new User();
        user.setNetId(netId);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setHashedPassword("hashedPassword123"); // In real scenarios, ensure passwords are hashed
        user.setUserType(userType);

        userRepository.save(user);
        testUsers.add(user);

        for (String courseCode : courseCodes) {
            // Fetch the course by code
            Optional<Course> courseOpt = courseRepository.findByCode(courseCode);
            Course course;
            if (courseOpt.isEmpty()) {
                // Create a new course if it doesn't exist
                course = new Course();
                course.setCode(courseCode);
                course.setTitle("Course " + courseCode);
                course.setDescription("Description for " + courseCode);
                course.setCredits(3);
                // You can set Department here if necessary
                courseRepository.save(course);
                testCourses.add(course);
            } else {
                course = courseOpt.get();
            }

            // Create a Schedule for the course
            Schedule schedule = new Schedule();
            schedule.setCourse(course);
            schedule.setSection("Section " + System.currentTimeMillis());
            schedule.setRecurringPattern("MWF");
            schedule.setStartTime(LocalTime.of(10, 0));
            schedule.setEndTime(LocalTime.of(11, 0));
            scheduleRepository.save(schedule);
            testSchedules.add(schedule);

            // Create an Enroll for the user
            Enroll enroll = new Enroll(user, schedule);
            enrollRepository.save(enroll);
            testEnrolls.add(enroll);
        }

        return user;
    }

    /**
     * Test Case 1: Find mutual courses between two users successfully.
     *
     * Objective: Ensure that the controller correctly identifies and returns mutual courses between two existing users.
     */
    @Test
    public void testFindMutualCourses_Success() {
        // Create unique identifiers to avoid conflicts
        String user1NetId = "s_test_" + System.currentTimeMillis() + "_1";
        String user2NetId = "s_test_" + System.currentTimeMillis() + "_2";

        // Define unique course codes
        String courseCodeA = "COMS 2270" + System.currentTimeMillis();
        String courseCodeB = "COMS 2280" + System.currentTimeMillis();
        String courseCodeC = "STAT 301" + System.currentTimeMillis();

        // Create Courses by ensuring unique codes
        List<String> user1Courses = List.of(courseCodeA, courseCodeB); // User1 enrolled in CSE101 and MAT201
        List<String> user2Courses = List.of(courseCodeA, courseCodeC); // User2 enrolled in CSE101 and PHY301

        // Create Users with Enrollments
        User user1 = createUserWithEnrollments(user1NetId, "Alice", "Anderson", "alice.anderson@example.com", UserType.STUDENT, user1Courses);
        User user2 = createUserWithEnrollments(user2NetId, "Bob", "Brown", "bob.brown@example.com", UserType.STUDENT, user2Courses);

        // Send GET request to find mutual courses
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .queryParam("user1NetId", user1NetId)
                .queryParam("user2NetId", user2NetId)
                .when()
                .get("/schedule/coursesInMutual");

        // Assert that the response status code is 200 OK
        assertEquals(200, response.getStatusCode(), "Expected status code 200");

        // Parse the response body to a Course array
        Course[] mutualCourses = response.getBody().as(Course[].class);

        // Assert that only courseCodeA is mutual
        assertNotNull(mutualCourses, "Mutual courses should not be null");
        assertEquals(1, mutualCourses.length, "There should be exactly one mutual course");
        assertEquals(courseCodeA, mutualCourses[0].getCode(), "Mutual course should be " + courseCodeA);
    }

    /**
     * Test Case 2: Attempt to find mutual courses when one user does not exist.
     *
     * Objective: Ensure that the controller returns an appropriate error when one of the users does not exist.
     */
    @Test
    public void testFindMutualCourses_UserNotFound() {
        // Create unique identifier for the existing user
        String existingUserNetId = "s_test_" + System.currentTimeMillis() + "_3";

        // Define a unique course code
        String courseCodeD = "STAT 347" + System.currentTimeMillis();

        // Create a Course and enroll the existing user
        List<String> existingUserCourses = List.of(courseCodeD);
        User existingUser = createUserWithEnrollments(existingUserNetId, "Charlie", "Clark", "charlie.clark@example.com", UserType.STUDENT, existingUserCourses);

        // Define a non-existent user NetID
        String nonExistentUserNetId = "s_test_" + System.currentTimeMillis() + "_999";

        // Send GET request to find mutual courses
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .queryParam("user1NetId", existingUserNetId)
                .queryParam("user2NetId", nonExistentUserNetId)
                .when()
                .get("/schedule/coursesInMutual");

        // Assert that the response status code is 404 Not Found
        assertEquals(404, response.getStatusCode(), "Expected status code 404 when one user does not exist");

        // Optionally, assert that the response body contains an error message
        String responseBody = response.getBody().asString();
        assertTrue(responseBody.isEmpty(), "Expected empty body for non-existent user");
    }

    /**
     * Test Case 3: Attempt to find mutual courses when both users do not exist.
     *
     * Objective: Ensure that the controller returns an appropriate error when both users do not exist.
     */
    @Test
    public void testFindMutualCourses_BothUsersNotFound() {
        // Define non-existent user NetIDs
        String nonExistentUser1NetId = "s_test_" + System.currentTimeMillis() + "_888";
        String nonExistentUser2NetId = "s_test_" + System.currentTimeMillis() + "_999";

        // Send GET request to find mutual courses
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .queryParam("user1NetId", nonExistentUser1NetId)
                .queryParam("user2NetId", nonExistentUser2NetId)
                .when()
                .get("/schedule/coursesInMutual");

        // Assert that the response status code is 404 Not Found
        assertEquals(404, response.getStatusCode(), "Expected status code 404 when both users do not exist");

        // Optionally, assert that the response body contains an error message
        String responseBody = response.getBody().asString();
        assertTrue(responseBody.isEmpty(), "Expected empty body for non-existent users");
    }

    /**
     * Test Case 4: Find mutual courses when users have no overlapping courses.
     *
     * Objective: Ensure that the controller correctly returns an empty list when there are no mutual courses.
     */
    @Test
    public void testFindMutualCourses_NoMutualCourses() {
        // Create unique identifiers to avoid conflicts
        String user1NetId = "s_test_" + System.currentTimeMillis() + "_4";
        String user2NetId = "s_test_" + System.currentTimeMillis() + "_5";

        // Define unique course codes
        String courseCodeE = "COMS 2270" + System.currentTimeMillis();
        String courseCodeF = "COMS 2280" + System.currentTimeMillis();
        String courseCodeG = "STAT 301" + System.currentTimeMillis();

        // Create Users with Enrollments
        List<String> user1Courses = List.of(courseCodeE); // User1 enrolled in BIO101
        List<String> user2Courses = List.of(courseCodeF, courseCodeG); // User2 enrolled in CHEM201 and HIST301

        User user1 = createUserWithEnrollments(user1NetId, "Diana", "Dawson", "diana.dawson@example.com", UserType.STUDENT, user1Courses);
        User user2 = createUserWithEnrollments(user2NetId, "Ethan", "Evans", "ethan.evans@example.com", UserType.STUDENT, user2Courses);

        // Send GET request to find mutual courses
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .queryParam("user1NetId", user1NetId)
                .queryParam("user2NetId", user2NetId)
                .when()
                .get("/schedule/coursesInMutual");

        // Assert that the response status code is 200 OK
        assertEquals(200, response.getStatusCode(), "Expected status code 200");

        // Parse the response body to a Course array
        Course[] mutualCourses = response.getBody().as(Course[].class);

        // Assert that the mutual courses list is empty
        assertNotNull(mutualCourses, "Mutual courses should not be null");
        assertEquals(0, mutualCourses.length, "There should be no mutual courses");
    }
}