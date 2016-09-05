package pt.isel.pdm.g04.pf.data.thoth.models;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Teachers {

    private List<BaseTeacher> teachers = new ArrayList<BaseTeacher>();
    private pt.isel.pdm.g04.pf.data.thoth.models.Links Links;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The teachers
     */
    public List<BaseTeacher> getTeachers() {
        return teachers;
    }

    /**
     * @param teachers The teachers
     */
    public void setTeachers(List<BaseTeacher> teachers) {
        this.teachers = teachers;
    }

    /**
     * @return The Links
     */
    public pt.isel.pdm.g04.pf.data.thoth.models.Links getLinks() {
        return Links;
    }

    /**
     * @param Links The _links
     */
    public void setLinks(pt.isel.pdm.g04.pf.data.thoth.models.Links Links) {
        this.Links = Links;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(teachers).append(Links).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Teachers) == false) {
            return false;
        }
        Teachers rhs = ((Teachers) other);
        return new EqualsBuilder().append(teachers, rhs.teachers).append(Links, rhs.Links).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
