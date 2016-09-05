package pt.isel.pdm.g04.pf.data.thoth.models;

import android.database.Cursor;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.HashMap;
import java.util.Map;

import pt.isel.pdm.g04.pf.data.thoth.models.core.Attributes;
import pt.isel.pdm.g04.pf.data.thoth.models.core.IHasId;
import pt.isel.pdm.g04.pf.data.thoth.provider.ThothContract;

public class BaseTeacher implements IHasId {

    @Attributes(primaryKey = true)
    protected int id;
    @Attributes(unique = true)
    protected int number;
    protected String shortName;
    @Attributes(unique = true)
    protected String academicEmail;
    protected AvatarUrl avatarUrl;
    protected BaseLinks _links;
    @Attributes(notMapped = true)
    protected Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public BaseTeacher() {
    }

    public BaseTeacher(Cursor cursor) {
        id = cursor.getInt(cursor.getColumnIndex(ThothContract.Teachers._ID));
        number = cursor.getInt(cursor.getColumnIndex(ThothContract.Teachers.NUMBER));
        shortName = cursor.getString(cursor.getColumnIndex(ThothContract.Teachers.SHORT_NAME));
        academicEmail = cursor.getString(cursor.getColumnIndex(ThothContract.Teachers.ACADEMIC_EMAIL));
        avatarUrl = new AvatarUrl();
        avatarUrl.setSize128(cursor.getString(cursor.getColumnIndex(ThothContract.Teachers.AVATAR_URL_SIZE128)));
        avatarUrl.setSize64(cursor.getString(cursor.getColumnIndex(ThothContract.Teachers.AVATAR_URL_SIZE64)));
        avatarUrl.setSize32(cursor.getString(cursor.getColumnIndex(ThothContract.Teachers.AVATAR_URL_SIZE32)));
        avatarUrl.setSize24(cursor.getString(cursor.getColumnIndex(ThothContract.Teachers.AVATAR_URL_SIZE24)));
        _links = new Links();
        _links.setSelf(cursor.getString(cursor.getColumnIndex(ThothContract.Teachers.LINKS_SELF)));
    }

    /**
     * @return The id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return The number
     */
    public int getNumber() {
        return number;
    }

    /**
     * @param number The number
     */
    public void setNumber(int number) {
        this.number = number;
    }

    /**
     * @return The shortName
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * @param shortName The shortName
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * @return The academicEmail
     */
    public String getAcademicEmail() {
        return academicEmail;
    }

    /**
     * @param academicEmail The academicEmail
     */
    public void setAcademicEmail(String academicEmail) {
        this.academicEmail = academicEmail;
    }

    /**
     * @return The avatarUrl
     */
    public AvatarUrl getAvatarUrl() {
        return avatarUrl;
    }

    /**
     * @param avatarUrl The avatarUrl
     */
    public void setAvatarUrl(AvatarUrl avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    /**
     * @return The Links
     */
    public BaseLinks get_links() {
        return _links;
    }

    /**
     * @param Links The _links
     */
    public void setLinks(BaseLinks Links) {
        this._links = Links;
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
        if ((other instanceof BaseTeacher) == false) {
            return false;
        }
        BaseTeacher rhs = ((BaseTeacher) other);
        return new EqualsBuilder()
                .append(id, rhs.id)
                .append(number, rhs.number)
                .append(shortName, rhs.shortName)
                .append(academicEmail, rhs.academicEmail)
                .append(avatarUrl, rhs.avatarUrl)
                .append(_links, rhs._links)
                .append(additionalProperties, rhs.additionalProperties)
                .isEquals();
    }

}
