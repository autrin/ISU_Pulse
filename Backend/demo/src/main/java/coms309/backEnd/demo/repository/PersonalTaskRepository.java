package coms309.backEnd.demo.repository;

import coms309.backEnd.demo.entity.PersonalTask;
import coms309.backEnd.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonalTaskRepository extends JpaRepository<PersonalTask, Integer> {
    public List<PersonalTask> findAllByUser(User user);
}
