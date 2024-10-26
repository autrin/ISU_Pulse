package com.cs309.manytomanyself.person;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long>{

	public Person findByName(String name);
}
