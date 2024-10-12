package coms309.backEnd.demo.entity;

import java.io.Serializable;
import java.util.Objects;

public class EnrollId implements Serializable {

    private String studentid;
    private int courseid;
    private int section;

    public EnrollId() {
    }

    // Parameterized constructor
    public EnrollId(String sId, int cId, int section) {
        this.studentid = sId;
        this.courseid = cId;
        this.section = section;
    }

    // hashCode and equals methods
    @Override
    public int hashCode() {
        return Objects.hash(studentid, courseid, section);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof EnrollId)) return false;
        EnrollId that = (EnrollId) obj;
        return section == that.section &&
                Objects.equals(studentid, that.studentid) &&
                Objects.equals(courseid, that.courseid);
    }
}