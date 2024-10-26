<h1> How to start </h1>
Start by doing a GET request to call the enterDummyData function in TestController file. Then play around with the functions in EmployeeForumController to see the relationships between User, Company, Employeer, and EmployeeForum. You are encouraged to add more User objects, Employees Objects,... and modify the logic in enterDummyData. 


<h1> EMS : Employee Management System</h1></br>

This project illustrates the use of Spring Jpa with one-to-many,
many-to-one and many-to-many relations. It also showcases how messages formatted as JSON
are to be sent to the frontend if required.

The database schema is as follows 

<b>User</b> - The application admin offered by mycompany, and is
not related to any other entity.</br>

<b>Company -</b> The company using the Employee management service</br>

<b>Employee -</b> The Employee of a particular company</br>

<b>EmployeeForum - </b> Forums created within the company for employees to join</br>

<h3>Table Relations</h3>

<ul>
  <li>A Company can have many employees as well as employee-forms and thus is a one-to-many with both Employee and EmployeeForum</li>
  <li>Employee can belong to only one company, but many employees can belong to one company. This is a many to one relation</li>
  <li>Employee can sign up to multiple EmployeeForums, and An EmployeeForum can have multiple employees registered to it. This is a many-to-many relation</li>
  <li>A Company can have multiple Forums, whereas a Forum requires exactly one company to belong to. This is again a many-to-one relation</li>
</ul>

<h3>API's</h3>

<ul>
  <li>User Controller</li>
      <ul>
          _____These function is to demo what the body of the GET request should look like when calling a function that does @RequestBody_________
             <li>GET request localhost:8080/api/v1/user/getUserObjectExample : Get an example of an user object. </li>
             <li>GET request localhost:8080/api/v1/user/getLoginVOObjectExample : Get an example of a loginVO object. </li>
          _____These function is to for students to practice calling GET/POST/PUT request_________
             <li>POST request localhost:8080/api/v1/user/registerUser : Accepts an user object but only take the email, password, name, and role of the object to create a new user's data entry in the database </li>
          <li>PUT request localhost:8080/api/v1/user/updateUserName/{name} : Accepts loginVO object and a new name, and update the user's name in the database </li>
          <li>GET request localhost:8080/api/v1/user/getUser : Accepts a loginVO object and returns the corresponding user data </li>
          <li>GET request localhost:8080/api/v1/user/getAllUsers : Gets all users present </li>
      </ul>
  <li>EmployeeForum Controller</li>
       <ul>
          <li> GET request localhost:8080/api/v1/company/{c_id}/forums : Accepts company Id as a path variable and lists all the forums </li>
          <li> GET request localhost:8080/api/v1/company/{c_id}/employee/{e_id}/forums : Accepts company id and employee id and gets all the forums for an employee and company </li>
       </ul>
  <li>Test Controller</li>
        <ul>
          <li> GET request localhost:8080/test/enter : enters dummy data so as to allow more functionality and querying </li>
        </ul>
</ul>

<h3>Service</h3>

The service is generally where the logic for the API is present. For example the PUT api for the User Controller accepts a User
as input, But a user with wrong time stamp can be provided directly by the user so as to mess with the database. Or there maybe users 
who are in different time zones. The Logic for checking such stuff should be present in the service.


### Version Tested

|IntelliJ  | Project SDK | Springboot | Maven |
|----------|-------------|------------|-------|
|2023.2.2  |     17      | 3.1.4      | 3.6.3 |

