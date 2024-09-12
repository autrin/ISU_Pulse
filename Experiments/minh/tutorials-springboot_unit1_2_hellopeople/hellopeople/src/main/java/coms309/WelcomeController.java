package coms309;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple Hello World Controller to display the string returned
 *
 * @author Vivek Bengre
 */

@RestController
class WelcomeController {

    @GetMapping("/")
    public String welcome() {
        return "Hello and welcome to COMS 309";
    }

    @GetMapping("/greet")
    public String greet(){
        return "Have a good day";
    }

    @GetMapping("/vietnam/{name}")
    public String hello(@PathVariable String name){
        return "Bố mày chào mày, bố mày tên là " + name;
    }

    @GetMapping("/users")
    public String getUserById(@RequestParam("name1") String name1, @RequestParam("name2") String name2) {
        return name1 + ", " + name2 + " là 2 thằng đầu buồi giẻ rách";
    }

    @GetMapping("/users/advance")
    public String introduceNameAndAge(@RequestParam("name") String name, @RequestParam("age") int age){
        return "My name is " + name + ". I'm " + age + " years old.";
    }

    @GetMapping("/greet/advance")
    public String greating(@RequestParam("name") String name){
        if(name.equals("bach") || name.equals("Bach")){
             return "Không thích chào thằng nào tên " + name;
        }
        else{
            return "Nice to meet you, " + name;
        }

    }






}
