//package coms309.backEnd.demo.entity;
//
//import lombok.Data;
//
//import java.io.Serializable;
//import java.util.Objects;
//
//public class TeachId implements Serializable {
//
//    private String fId;
//    private int cId;
//    private int section;
//
//
//    public TeachId() {
//    }
//
//    // Parameterized constructor
//    public TeachId(String fId, int cId, int section) {
//        this.fId = fId;
//        this.cId = cId;
//        this.section = section;
//    }
//
//    // hashCode and equals methods
//    @Override
//    public int hashCode() {
//        return Objects.hash(fId, cId, section);
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) return true;
//        if (!(obj instanceof TeachId)) return false;
//        TeachId that = (TeachId) obj;
//        return section == that.section &&
//                Objects.equals(fId, that.fId) &&
//                Objects.equals(cId, that.cId);
//    }
//}