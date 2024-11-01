package com.mycompany.controller;

import com.mycompany.model.Employee;
import com.mycompany.model.EmployeeForum;
import com.mycompany.service.EmployeeForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/company")
public class EmployeeForumController {

    @Autowired
    private EmployeeForumService employeeForumService;

    @GetMapping(value = "/{c_id}/forums", produces = "application/json")
    List<EmployeeForum> getAllEmployeeForumsForCompany(@PathVariable long c_id){
        return employeeForumService.getAllForumsForCompany(c_id);
    }

    @GetMapping(value = "/{c_id}/employee/{e_id}/forums", produces = "application/json")
    List<EmployeeForum> getAllForumForEmployee(@PathVariable long c_id, @PathVariable long e_id){
        return employeeForumService.getAllForumPerEmployee(c_id, e_id);
    }

}
