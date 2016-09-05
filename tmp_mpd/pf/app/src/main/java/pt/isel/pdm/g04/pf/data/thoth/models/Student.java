package pt.isel.pdm.g04.pf.data.thoth.models;

import android.database.Cursor;

import com.google.gson.annotations.Expose;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import pt.isel.pdm.g04.pf.data.thoth.models.core.Attributes;
import pt.isel.pdm.g04.pf.data.thoth.models.core.IHasId;
import pt.isel.pdm.g04.pf.data.thoth.provider.ThothContract;

public class Student implements IHasId {

    @Expose
    @Attributes(primaryKey = true)
    protected int id;
    @Expose
    protected int number;
    @Expose
    protected String shortName;
    @Expose
    @Attributes(unique = true)
    protected String academicEmail;
    @Expose
    protected String GitHubUsername;
    @Expose
    protected AvatarUrl avatarUrl;
    protected BaseLinks _links;

    public Student(Cursor cursor) {
        id = cursor.getInt(cursor.getColumnIndex(ThothContract.Students._ID));
        number = cursor.getInt(cursor.getColumnIndex(ThothContract.Students.NUMBER));
        GitHubUsername = cursor.getString(cursor.getColumnIndex(ThothContract.Students.GITHUB_USERNAME));
        shortName = cursor.getString(cursor.getColumnIndex(ThothContract.Students.SHORT_NAME));
        academicEmail = cursor.getString(cursor.getColumnIndex(ThothContract.Students.ACADEMIC_EMAIL));
        avatarUrl = new AvatarUrl();
        avatarUrl.setSize128(cursor.getString(cursor.getColumnIndex(ThothContract.Students.AVATAR_URL_SIZE128)));
        avatarUrl.setSize64(cursor.getString(cursor.getColumnIndex(ThothContract.Students.AVATAR_URL_SIZE64)));
        avatarUrl.setSize32(cursor.getString(cursor.getColumnIndex(ThothContract.Students.AVATAR_URL_SIZE32)));
        avatarUrl.setSize24(cursor.getString(cursor.getColumnIndex(ThothContract.Students.AVATAR_URL_SIZE24)));
        _links = new Links();
        _links.setSelf(cursor.getString(cursor.getColumnIndex(ThothContract.Students.LINKS_SELF)));
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

    public Student withId(int id) {
        this.id = id;
        return this;
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

    public Student withNumber(int number) {
        this.number = number;
        return this;
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

    public Student withShortName(String shortName) {
        this.shortName = shortName;
        return this;
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

    public Student withAcademicEmail(String academicEmail) {
        this.academicEmail = academicEmail;
        return this;
    }

    /**
     * @return The GitHubUsername
     */
    public Object getGitHubUsername() {
        return GitHubUsername;
    }

    /**
     * @param GitHubUsername The GitHubUsername
     */
    public void setGitHubUsername(String GitHubUsername) {
        this.GitHubUsername = GitHubUsername;
    }

    public Student withGitHubUsername(String GitHubUsername) {
        this.GitHubUsername = GitHubUsername;
        return this;
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

    public Student withAvatarUrl(AvatarUrl avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
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
    public int hashCode() {
        return new HashCodeBuilder().append(id).append(number).append(shortName).append(academicEmail).append(GitHubUsername).append(avatarUrl).append(_links.getSelf()).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Student) == false) {
            return false;
        }
        Student rhs = ((Student) other);
        return new EqualsBuilder().append(id, rhs.id).append(number, rhs.number).append(shortName, rhs.shortName).append(academicEmail, rhs.academicEmail).append(GitHubUsername, rhs.GitHubUsername).append(avatarUrl, rhs.avatarUrl).append(_links, rhs.get_links()).isEquals();
    }

}
