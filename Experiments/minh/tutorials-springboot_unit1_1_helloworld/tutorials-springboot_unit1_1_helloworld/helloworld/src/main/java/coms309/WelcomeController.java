package coms309;

import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;

@RestController // GET, POST, PUT, DELETE
    // Postmapping
class WelcomeController {

    @GetMapping("/") // Handle GET request
    public String welcome() {
        return "Hello and welcome to COMS 309";
    }

    @GetMapping("/demo1/springboot")
    public Map<String, String> demo1() {
        Map<String, String> response = new HashMap<>();
        response.put("date", "2024-09-10");
        response.put("purpose", "demo 01");
        return response;
    }

    @GetMapping("/{name}")
    public String welcome(@PathVariable String name) {
        return "Hello and welcome to COMS 309: " + name;
    }
}
