package coms309;

import org.springframework.web.bind.annotation.*;

@RestController
class WelcomeController {

    @GetMapping("/COMS309/demo01")
    public String welcome() {
        return "Hello and welcome to COMS 309";
    }

    @GetMapping("/{name}")
    public String welcome(@PathVariable String name) {
        return "Hello and welcome to COMS 309: " + name;
    }

    @PutMapping("/student/{id}")
    public String changeName(@PathVariable("id") Long studentId,
                                             @RequestParam(required = true) String name) {
        return "Student with ID: " + studentId + " has changed name to " + name;
    }
}
