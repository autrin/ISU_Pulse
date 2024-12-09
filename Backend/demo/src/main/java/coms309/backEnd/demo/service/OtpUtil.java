package coms309.backEnd.demo.service;

import java.util.Random;

public class OtpUtil {
    public static String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000)); // Generate a 6-digit OTP
    }
}