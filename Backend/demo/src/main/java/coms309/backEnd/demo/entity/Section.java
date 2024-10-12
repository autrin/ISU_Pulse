package coms309.backEnd.demo.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class Section {

    @Id
    //Section can be 1,2,3 or A,B,C
    private String section;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;


    private String instructor;

    @Temporal(TemporalType.TIMESTAMP)
    private Date start_time;
    @Temporal(TemporalType.TIMESTAMP)
    private Date end_time;


}
