package coms309.backEnd.demo.repository;

import coms309.backEnd.demo.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository  extends JpaRepository<Group, Long> {
}
