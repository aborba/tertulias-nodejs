package pt.isel.pdm.g04.pf.data.parse.classes;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("AlertTargets")
public class Subscription extends ParseObject {

    public static final String STUDENT = "student";
    public static final String TEACHER = "teacher";

    public Subscription() {
        super();
    }

    public void addTarget(String email) {
        put(STUDENT, ParseUser.getCurrentUser());
        put(TEACHER, email);
    }

    public String getTarget() {
        return getString(TEACHER);
    }

    public static ParseQuery<Subscription> getQuery() {
        return ParseQuery.getQuery(Subscription.class);
    }

}
