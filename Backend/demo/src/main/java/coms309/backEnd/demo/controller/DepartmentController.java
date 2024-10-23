package coms309.backEnd.demo.controller;

import coms309.backEnd.demo.entity.Department;
import coms309.backEnd.demo.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/department")
public class DepartmentController {
//    @Autowired
//    private final DepartmentRepository departmentRepository;
//
//    public DepartmentController(DepartmentRepository departmentRepository) {
//        this.departmentRepository = departmentRepository;
//    }
//
//    // Only Admin can add more department(do it later)
//    @PostMapping("/addDepartment")
//    public boolean addDepartment(@RequestBody Department department){
//        departmentRepository.save(department);
//        return true;
//    }
//    @DeleteMapping("/delete")
//    public boolean deleteDepartment
}
