package coms309.backEnd.demo.repository;

import coms309.backEnd.demo.entity.PersonalTask;
import coms309.backEnd.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PersonalTaskRepository extends JpaRepository<PersonalTask, Integer> {
    public List<PersonalTask> findAllByUser(User user);
}
