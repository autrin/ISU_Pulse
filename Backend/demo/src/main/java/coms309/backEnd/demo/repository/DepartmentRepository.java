package coms309.backEnd.demo.repository;

import coms309.backEnd.demo.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    public Department findDepartmentByName(String name);
}
