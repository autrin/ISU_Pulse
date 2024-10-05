package coms309.backEnd.demo.entity;

import java.io.Serializable;
import java.util.Objects;

public class EnrollId implements Serializable {

    private String sId;
    private String cId;
    private int section;

    public EnrollId() {
    }

    // Parameterized constructor
    public EnrollId(String sId, String cId, int section) {
        this.sId = sId;
        this.cId = cId;
        this.section = section;
    }

    // hashCode and equals methods
    @Override
    public int hashCode() {
        return Objects.hash(sId, cId, section);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof EnrollId)) return false;
        EnrollId that = (EnrollId) obj;
        return section == that.section &&
                Objects.equals(sId, that.sId) &&
                Objects.equals(cId, that.cId);
    }
}