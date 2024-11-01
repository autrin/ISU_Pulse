package com.mycompany.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String registrationNumber;

    @Column(nullable = false)
    private Date createdOn;

    @Column(nullable = false)
    private Date modifiedOn;

    @OneToMany(mappedBy = "company")
    @JsonIgnore
    private List<Employee> employees;

    @OneToMany(mappedBy = "company")
    @JsonIgnore
    private List<EmployeeForum> employeeForums;

    public Company(long id, String name, String registrationNumber) {
        this.id = id;
        this.name = name;
        this.registrationNumber = registrationNumber;
        this.createdOn = new Date();
        this.modifiedOn = new Date();
        this.employeeForums = new ArrayList<>();
        this.employees = new ArrayList<>();
    }

    public Company(){
        createdOn = new Date();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public List<EmployeeForum> getEmployeeForums() {
        return employeeForums;
    }

    public void setEmployeeForums(List<EmployeeForum> employeeForums) {
        this.employeeForums = employeeForums;
    }
}
