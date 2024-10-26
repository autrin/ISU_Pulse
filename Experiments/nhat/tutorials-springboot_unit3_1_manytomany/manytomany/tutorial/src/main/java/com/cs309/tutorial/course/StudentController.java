package com.cs309.tutorial.course;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/student")
public class StudentController {
	
	@Autowired
	StudentRepository studentRepo;
	
	@Autowired
	CourseRepository CourseRepository;
	
	@PostMapping("/create")
	public void createStudent() {
		Student student = new Student();
		studentRepo.save(student);
	}
	
	@GetMapping("/get")
	public Student getStudent(@RequestParam Long id) {
		return studentRepo.getOne(id);
	}
	
	@PostMapping("/registerCourse")
	public void registerCourse() {
		
	}
}
