package pt.isel.pdm.g04.pf.data.parse.localhelpers;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import pt.isel.pdm.g04.pf.helpers.Constants;
import pt.isel.pdm.g04.pf.helpers.Logger;
import pt.isel.pdm.g04.pf.helpers.Utils;

public class ParseEndPoint  {
    private static final String CLASS_NAME = "ParseServerEndPoint";

    public static ParseUser logIn(String username, String password) throws ParseException {
        lc("logIn");
        Utils.assertNotOnUIThread();
        ;
        return ParseUser.logIn(username, password);
    }

    public static ParseUser signUp(String email, String password, int userType) throws ParseException {
        lc("signUp");
        Utils.assertNotOnUIThread();
        if (existsUser(email)) return logIn(email, password);
        ParseUser parseUser = new ParseUser();
        parseUser.setUsername(email);
        parseUser.setPassword(password);
        parseUser.setEmail(email);
        parseUser.put("isTeacher", userType == Constants.Thoth.UserTypes.TEACHER);
        parseUser.signUp();
        return parseUser;
    }

    public static boolean isMyEmailVerified() throws ParseException {
        Utils.assertNotOnUIThread();
        ;
        ParseUser me = ParseUser.getCurrentUser();
        return !(me == null || me.isDirty() || !me.isAuthenticated())
                && me.fetchIfNeeded().getBoolean("emailVerified");
    }

    public static boolean isEmailVerified(String username) {
        Utils.assertNotOnUIThread();
        ;
        try {
            return ParseUser.getQuery().whereEqualTo("username", username).whereEqualTo("emailVerified", true).count() == 1;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean existsUser(String value) throws ParseException {
        return existsUniqueUserItem("username", value);
    }

    // region Private

    private static boolean isTeacher(String username, String subDomain) {
        return false
                || !subDomain.equals("alunos")
                //* -> Starting with "//" deactivates block comment
                || username.matches("^\\d+-.*teacher$")
                //*/
                ;
    }

    private static boolean existsUniqueUserItem(String key, String value) throws ParseException {
        Utils.assertNotOnUIThread();
        ;
        ParseQuery query = ParseUser.getQuery();
        return query.whereEqualTo(key, value).count() == 1;
    }

    private static void lc(String methodname) {
        Logger.c(CLASS_NAME, methodname);
    }

    // endregion

}
