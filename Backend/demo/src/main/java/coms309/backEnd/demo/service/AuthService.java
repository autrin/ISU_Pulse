package coms309.backEnd.demo.service;

import coms309.backEnd.demo.entity.User;
import coms309.backEnd.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

//@Service
//public class AuthService {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private EmailService emailService;
//
//    public String sendOtp(String email) {
//        // Generate OTP
//        String otp = OtpUtil.generateOtp();
//
//        // Save OTP to the user (create user if not exists)
//        Optional<User> userOptional = userRepository.findByEmail(email);
//        User user = userOptional.orElse(new User());
//        user.setEmail(email);
//        user.setOtp(otp);
//        user.setVerified(false); // Mark as unverified
//        userRepository.save(user);
//
//        // Send OTP via email
//        emailService.sendOtp(email, otp);
//
//        return "OTP sent successfully!";
//    }
//
//    public String verifyOtp(String email, String otp) {
//        // Find user by email
//        Optional<User> userOptional = userRepository.findByEmail(email);
//        if (userOptional.isEmpty()) {
//            return "User not found!";
//        }
//
//        // Verify OTP
//        User user = userOptional.get();
//        if (user.getOtp().equals(otp)) {
//            user.setVerified(true); // Mark user as verified
//            user.setOtp(null);      // Clear OTP after verification
//            userRepository.save(user);
//            return "OTP verified successfully!";
//        } else {
//            return "Invalid OTP!";
//        }
//    }
//}
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SendGridEmailService emailService;

    public String sendOtp(String email) {
        // Generate OTP
        String otp = OtpUtil.generateOtp();

        // Find or create user
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            // If the user exists, get the user
            user = userOptional.get();
            user.setVerified(false);
        } else {
            // If the user does not exist, create a new user
            user = new User();
            user.setEmail(email);
            user.setVerified(false); // Initially unverified
            user = userRepository.save(user); // Save the new user
        }

        // Save OTP
        user.setOtp(otp);
        userRepository.save(user);

        // Send OTP via SendGrid
        String subject = "Your OTP Code";
        String body = "Your OTP code is: " + otp + "\nIt will expire in 5 minutes.";
        return emailService.sendEmail(email, subject, body);
    }

    /**
     * Verify the OTP for a given email
     * @param email The user's email
     * @param otp The OTP to verify
     * @return A success or failure message
     */
    public String verifyOtp(String email, String otp) {
        // Find user by email
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return "User not found!";
        }

        User user = userOptional.get();

        // Check if OTP matches
        if (user.getOtp() != null && user.getOtp().equals(otp)) {
            user.setVerified(true); // Mark user as verified
            user.setOtp(null);      // Clear OTP after successful verification
            userRepository.save(user);
            return "OTP verified successfully!";
        } else {
            return "Invalid or expired OTP!";
        }
    }
}
