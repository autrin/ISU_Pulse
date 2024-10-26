package com.mycompany.repository;

import com.mycompany.model.EmployeeForum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeForumRepository extends JpaRepository<EmployeeForum, Long> {
    List<EmployeeForum> findByCompany_Id(long c_id);
    List<EmployeeForum> findByCompany_IdAndEmployees_Id(long c_id, long e_id);
    EmployeeForum findById(long id);
}
