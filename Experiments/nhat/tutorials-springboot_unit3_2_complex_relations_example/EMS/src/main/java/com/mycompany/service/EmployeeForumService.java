package com.mycompany.service;

import com.mycompany.model.EmployeeForum;
import com.mycompany.repository.EmployeeForumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeForumService {

    @Autowired
    private EmployeeForumRepository employeeForumRepository;

    public List<EmployeeForum> getAllForumsForCompany(long id){
        return employeeForumRepository.findByCompany_Id(id);
    }

    public List<EmployeeForum> getAllForumPerEmployee(long c_id, long e_id){
        List<EmployeeForum> ef =  employeeForumRepository.findByCompany_IdAndEmployees_Id(c_id, e_id);
        return ef;
    }
}
