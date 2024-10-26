package com.cs309.tutorial.course;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "student")
public class Student {

    @Id
    @Column(name = "id")
    private Long id;

    @ManyToMany
    @JoinTable(name = "course_like", joinColumns = @JoinColumn(name = "student_id"), inverseJoinColumns = @JoinColumn(name = "course_id"))
    private Set<Course> likedCourses;

    @OneToMany(mappedBy = "student")
    private Set<CourseRating> ratings;

    @OneToMany(mappedBy = "student")
    private Set<CourseRegistration> registrations;

    // additional properties

    public Student() {
    }

    public Long getId() {
        return id;
    }

    public Set<Course> getLikedCourses() {
        return likedCourses;
    }

    public Set<CourseRating> getRatings() {
        return ratings;
    }

    public Set<CourseRegistration> getRegistrations() {
        return registrations;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Student other = (Student) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}