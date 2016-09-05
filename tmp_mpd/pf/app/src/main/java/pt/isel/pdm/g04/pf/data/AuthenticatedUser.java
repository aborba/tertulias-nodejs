package pt.isel.pdm.g04.pf.data;

import android.content.ContentResolver;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.parse.ParseException;
import com.parse.ParseUser;

import pt.isel.pdm.g04.pf.data.thoth.database.Schema;
import pt.isel.pdm.g04.pf.data.thoth.models.Student;
import pt.isel.pdm.g04.pf.data.thoth.models.Teacher;
import pt.isel.pdm.g04.pf.helpers.Constants;
import pt.isel.pdm.g04.pf.helpers.Utils;

public class AuthenticatedUser implements Parcelable {
    private String email;
    public String name;
    public int type;
    public String avatarUrl;
    public String tokenType;
    public String password;
    public String token;
    public Exception lastError;

    public AuthenticatedUser(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public AuthenticatedUser withProfileFrom(ContentResolver contentResolver) {
        Teacher teacher = Schema.Teachers.selectByEmail(contentResolver, email);
        if (teacher == null) {
            Student student = Schema.Students.selectByEmail(contentResolver, email);
            type = Utils.fakeTeacher(email) ?
                    Constants.Thoth.UserTypes.TEACHER : Constants.Thoth.UserTypes.STUDENT;
            if (student == null) {
                name = email;
            } else {
                name = student.getShortName();
                avatarUrl = student.getAvatarUrl().getSize32();
            }
        } else {
            type = Constants.Thoth.UserTypes.TEACHER;
            name = teacher.getShortName();
            avatarUrl = teacher.getAvatarUrl().getSize128();
            if (TextUtils.isEmpty(avatarUrl)) {
                avatarUrl = teacher.getAvatarUrl().getSize32();
            }
        }
        return this;
    }

    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(type);
        out.writeString(email);
        out.writeString(avatarUrl);
        out.writeString(name);
        out.writeString(tokenType);
        out.writeString(password);
        out.writeString(token);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<AuthenticatedUser> CREATOR = new Parcelable.Creator<AuthenticatedUser>() {
        public AuthenticatedUser createFromParcel(Parcel in) {
            return new AuthenticatedUser(in);
        }

        public AuthenticatedUser[] newArray(int size) {
            return new AuthenticatedUser[size];
        }
    };

    public boolean isEmailVerified() {
        ParseUser me = ParseUser.getCurrentUser();
        if (me == null || !me.isAuthenticated()) return false;
        try {
            return me.fetchIfNeeded().getBoolean("emailVerified");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private AuthenticatedUser(Parcel in) {
        type = in.readInt();
        email = in.readString();
        avatarUrl = in.readString();
        name = in.readString();
        tokenType = in.readString();
        password = in.readString();
        token = in.readString();
    }
}