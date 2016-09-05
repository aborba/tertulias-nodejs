package pt.isel.s1516v.ps.apiaccess.support.remote;

public class ApiMeCore {

    @com.google.gson.annotations.SerializedName("alias")
    public final String alias;
    @com.google.gson.annotations.SerializedName("firstName")
    public final String firstName;
    @com.google.gson.annotations.SerializedName("lastName")
    public final String lastName;
    @com.google.gson.annotations.SerializedName("email")
    public final String email;
    @com.google.gson.annotations.SerializedName("picture")
    public final String picture;
    @com.google.gson.annotations.SerializedName("myKey")
    public final String myKey;

    public ApiMeCore() {
        alias = firstName = lastName = email = picture = myKey = null;
    }

    @Override
    public String toString() { return alias; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        ApiMeCore other = (ApiMeCore) obj;
        return obj instanceof ApiMeCore && other.alias == this.alias &&
                other.firstName == this.firstName &&
                other.lastName == this.lastName &&
                other.email == this.email &&
                other.picture == this.picture;
    }
}
