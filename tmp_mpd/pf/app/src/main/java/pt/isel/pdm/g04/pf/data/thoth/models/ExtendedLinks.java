package pt.isel.pdm.g04.pf.data.thoth.models;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class ExtendedLinks extends Links {

    private String teachers;
    private String root;


    /**
     * @return The teachers
     */
    public String getTeachers() {
        return teachers;
    }

    /**
     * @param teachers The teachers
     */
    public void setTeachers(String teachers) {
        this.teachers = teachers;
    }

    /**
     * @return The root
     */
    public String getRoot() {
        return root;
    }

    /**
     * @param root The root
     */
    public void setRoot(String root) {
        this.root = root;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(self).append(teachers).append(root).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ExtendedLinks) == false) {
            return false;
        }
        ExtendedLinks rhs = ((ExtendedLinks) other);
        return new EqualsBuilder().append(self, rhs.self).append(teachers, rhs.teachers).append(root, rhs.root).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
