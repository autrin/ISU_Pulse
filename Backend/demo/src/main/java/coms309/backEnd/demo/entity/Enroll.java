package coms309.backEnd.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Enroll")
public class Enroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sId")
    private Long sId;

    @Column(name = "cId")
    private Long cId;

    @Column(name = "section")
    private String section;

    public Enroll() {
    }

    public Long getsId() {
        return sId;
    }

    public void setsId(Long sId) {
        this.sId = sId;
    }

    public Long getcId() {
        return cId;
    }

    public void setcId(Long cId) {
        this.cId = cId;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }
}
