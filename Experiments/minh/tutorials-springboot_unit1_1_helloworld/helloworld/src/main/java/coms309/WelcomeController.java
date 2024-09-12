package coms309;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
class WelcomeController {

    @GetMapping("/")
    public String welcome() {
            return "Hello and welcome to COMS 309";
    }

    @GetMapping("/introduce/{name}")
    public String introduce(@PathVariable String name){

        return "My name is " + name + ". Nice to meet you guys!!!";
    }

    @GetMapping("/{name}")
    public String welcome(@PathVariable String name) {

        return "Hello and welcome to COMS 309: " + name;
    }
    @GetMapping("/laugh")
    public String welcome2(){
        return "Hahahahaha";
    }

    @GetMapping("/game/{game1}")
    public String game(@PathVariable String game1){

        return "My favorite game is " + game1;
    }

    @GetMapping("/music/{type}")
    public String music(@PathVariable String type){
        return "I love " + type;
    }


}
