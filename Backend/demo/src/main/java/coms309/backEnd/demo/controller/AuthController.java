package coms309.backEnd.demo.controller;

import coms309.backEnd.demo.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Endpoint to send OTP to a user's email
     * @param email The user's email address
     * @return ResponseEntity with the result of the operation
     */
    @PostMapping("/sendOtp")
    public ResponseEntity<String> sendOtp(@RequestParam String email) {
        try {
            String result = authService.sendOtp(email);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send OTP: " + e.getMessage());
        }
    }

    /**
     * Endpoint to verify the OTP for a user's email
     * @param email The user's email address
     * @param otp The OTP to verify
     * @return ResponseEntity with the result of the operation
     */
    @PostMapping("/verifyOtp")
    public ResponseEntity<String> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        try {
            String result = authService.verifyOtp(email, otp);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to verify OTP: " + e.getMessage());
        }
    }
}
