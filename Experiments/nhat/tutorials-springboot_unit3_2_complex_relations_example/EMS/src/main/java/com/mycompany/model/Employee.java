package com.mycompany.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Date createdOn;

    @Column(nullable = false)
    private Date modifiedOn;

    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonIgnore
    Company company;

    @ManyToMany
    @JoinColumn(nullable = true)
    @JsonIgnore
    List<EmployeeForum> employeeForums;

    public Employee(long id, String name, String email, String address, Company company) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
        this.createdOn = new Date();
        this.modifiedOn = new Date();
        this.company = company;
        this.employeeForums = new ArrayList<>();
    }

    public Employee() {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public List<EmployeeForum> getEmployeeForums() {
        return employeeForums;
    }

    public void setEmployeeForums(List<EmployeeForum> employeeForums) {
        this.employeeForums = employeeForums;
    }

    public void addEmployeeForum(EmployeeForum employeeForum){
        this.employeeForums.add(employeeForum);
    }
}
