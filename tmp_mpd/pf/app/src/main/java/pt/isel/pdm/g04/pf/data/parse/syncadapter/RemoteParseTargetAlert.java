package pt.isel.pdm.g04.pf.data.parse.syncadapter;

import android.content.Context;

import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.LinkedList;
import java.util.List;

import pt.isel.pdm.g04.pf.data.parse.classes.Subscription;
import pt.isel.pdm.g04.pf.helpers.Logger;

public class RemoteParseTargetAlert extends RemoteParseTemplate {
    private static final String CLASS_NAME = "RemoteParseTargetAlert";

    private static final String STUDENT_NOT_OK = "User not authentiated, email not verified or is a teacher.";
    private static final String TEACHER_NOT_OK = "User not authentiated, email not verified or is not a teacher.";

    private Context ctx;

    public RemoteParseTargetAlert(Context ctx) {
        this.ctx = ctx;
    }

    public boolean exists(String email) throws ParseException {
        return Subscription.getQuery()
                .whereEqualTo(Subscription.STUDENT, me())
                .whereEqualTo(Subscription.TEACHER, email)
                .count() == 1;
    }

    public List<String> readAll() throws ParseException {
        Logger.c(CLASS_NAME, "readAll");
        checkStudentOk(ctx);
        ParseQuery<Subscription> alertQuery = Subscription.getQuery();
        if (alertQuery.whereEqualTo("student", me()).count() == 0) return new LinkedList<>();
        List<Subscription> subscriptions = alertQuery.whereEqualTo("student", me()).find();
        List<String> providerTargets = new LinkedList<>();
        for (Subscription target : subscriptions) providerTargets.add(target.getTarget());
        return providerTargets;
    }

    public void addInBackground(String email, boolean check) throws ParseException {
        if (check) checkStudentOk(ctx);
        if (exists(email)) return;
        Subscription subscription = new Subscription();
        subscription.addTarget(email);
        subscription.saveInBackground();
    }

    public void deleteInBackground(String email) throws ParseException {
        checkStudentOk(ctx);
        if (!exists(email)) return;
        Subscription subscription = new Subscription();
        subscription.addTarget(email);
        subscription.deleteInBackground();
    }

    private void checkStudentOk(Context ctx) throws ParseException {
        if (isStudentOk(ctx)) return;
        Logger.w("Student not ok...");
        throw new ParseException(ParseException.VALIDATION_ERROR, STUDENT_NOT_OK);
    }
}
