package com.cs309.manytomanyself.person;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/person")
public class PersonController {
	
	@Autowired
	PersonRepository personRepo;
	
	@PostMapping("/add")
	public void addPerson(@RequestBody Person p) {
		personRepo.save(p);
	}
	
	@PostMapping("/addFriend")
	public void addFriend(@RequestParam String name, @RequestParam String fName) {
		Person person = personRepo.findByName(name);
		Person friend = personRepo.findByName(fName);
		if(person!= null && friend != null) {
			person.getFriends().add(friend);
			personRepo.save(person);
		}
	}
	
	@GetMapping("/allPeople")
	public List<Person> getAll(){
		return personRepo.findAll();
	}
}
