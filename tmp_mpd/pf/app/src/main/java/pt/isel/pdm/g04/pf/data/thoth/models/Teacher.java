package pt.isel.pdm.g04.pf.data.thoth.models;

import android.database.Cursor;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import pt.isel.pdm.g04.pf.data.thoth.provider.ThothContract;

public class Teacher extends BaseTeacher {

    private String fullName;

    public Teacher(Cursor cursor) {
        super(cursor);
        fullName = cursor.getString(cursor.getColumnIndex(ThothContract.Teachers.FULL_NAME));
    }

    public Teacher(BaseTeacher bt) {
        setAcademicEmail(bt.academicEmail);
        setAvatarUrl(bt.avatarUrl);
        setId(bt.id);
        setLinks(bt._links);
        setNumber(bt.number);
        setShortName(bt.shortName);
    }

    /**
     * @return The fullName
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * @param fullName The fullName
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(number)
                .append(shortName)
                .append(academicEmail)
                .append(get_links().getSelf())
                .append(avatarUrl.getSize32())
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Teacher) == false) {
            return false;
        }
        Teacher rhs = ((Teacher) other);
        return new EqualsBuilder()
                .append(id, rhs.id)
                .append(number, rhs.number)
                .append(shortName, rhs.shortName)
                .append(fullName, rhs.fullName)
                .append(academicEmail, rhs.academicEmail)
                .append(avatarUrl, rhs.avatarUrl)
                .append(_links, rhs._links)
                .append(additionalProperties, rhs.additionalProperties)
                .isEquals();
    }

}
