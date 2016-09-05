package pt.isel.pdm.g04.pf.data.thoth.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

public class Students {

    @Expose
    private List<Student> students = new ArrayList<Student>();
    @SerializedName("_links")
    @Expose
    private Links Links;

    /**
     * @return The students
     */
    public List<Student> getStudents() {
        return students;
    }

    /**
     * @param students The students
     */
    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public Students withStudents(List<Student> students) {
        this.students = students;
        return this;
    }

    /**
     * @return The Links
     */
    public Links getLinks() {
        return Links;
    }

    /**
     * @param Links The _links
     */
    public void setLinks(Links Links) {
        this.Links = Links;
    }

    public Students withLinks(Links Links) {
        this.Links = Links;
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(students).append(Links).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Students) == false) {
            return false;
        }
        Students rhs = ((Students) other);
        return new EqualsBuilder().append(students, rhs.students).append(Links, rhs.Links).isEquals();
    }

}
