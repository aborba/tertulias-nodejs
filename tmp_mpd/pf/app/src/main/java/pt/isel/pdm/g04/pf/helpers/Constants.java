package pt.isel.pdm.g04.pf.helpers;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class Constants {

    /*
    0% = #00
    10% = #16
    20% = #32
    30% = #48
    40% = #64
    50% = #80
    60% = #96
    70% = #112
    80% = #128
    90% = #144
     */
    public static final String PERCENT_TRANSPARENCY = "#96";
    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public interface SyncAdaptor {
        String ACCOUNT_TYPE = "pt.isel.pdm.g04.pf";
        String ACCOUNT_NAME = "Thoth";
        int SYNC_FREQUENCY = 24 * 60 * 60;
    }

    public interface Preferences {
        String TEACHER_SERVER_ADDRESS = "teachersServerAddress";
        String STUDENT_SERVER_ADDRESS = "studentsServerAddress";
        String DISK_CACHE_SIZE = "diskCacheSize";
        String MEMORY_CACHE_SIZE = "memoryCacheSize";
        String PREF_SETUP_COMPLETE = "setupSyncAdapter";
        String USE_COLORED_NOTIFICATIONS = "useColoredNotifications";
        String SHOW_GEOFENCES = "showGeofences";
        String USE_BATTERY_SAVING_MODE = "useBatterySavingMode";
    }

    public static class Activities {
        public static final String NOTIFICATIONS_EXTRA = "notifications";
        public static final String TEACHERS_EXTRA = "teachers";
        public static final String ACCOUNT_TYPE_EXTRA = "account_type";
        public static final String AUTH_TOKEN_TYPE_EXTRA = "auth_token_type";
        public static final String IS_NEW_ACCOUNT_EXTRA = "new_account";
        public static final String USER_EXTRA = "user";

        public interface Main {
            int DRAWER_NOTIFICATIONS = 1;
            int DRAWER_MAPS = 2;
            int DRAWER_TEACHERS = 3;
            int DRAWER_SETTINGS = 4;
            int DRAWER_ABOUT = 5;
            int DRAWER_ADD_ACCOUNT = 6;
            int DRAWER_SHARE = 8;
        }
    }

    public static class Thoth {
        public static final boolean DEBUG_ALWAYS_LOGIN_AS_TEACHER = true;
        public static final String DEBUG_TEACHER_EMAIL = "cguedes@cc.isel.ipl.pt";

        public static final String VALID_EMAIL_SUFFIXES = ".isel.ipl.pt;.isel.pt";

        public interface Urls {
            String TEACHERS = "https://adeetc.thothapp.com/api/v1/teachers/";
            String STUDENTS = "https://adeetc.thothapp.com/api/v1/students/";
        }

        public interface Cursors {
            int TEACHERS_LOADER = 0;
            int STUDENTS_LOADER = 1;
        }

        public interface UserTypes {
            int TEACHER = 0;
            int STUDENT = 1;
        }
    }

    public static class Isel {
        public static final CameraPosition LOCATION =
                new CameraPosition.Builder().target(new LatLng(38.756157, -9.116506))
                        .zoom(17.5f)
                        .bearing(-70)
                        .tilt(15)
                        .build();

        public interface Locations {
            String BUILDING_A = PERCENT_TRANSPARENCY + "A52927";
            String BUILDING_C = PERCENT_TRANSPARENCY + "E67817";
            String BUILDING_E = PERCENT_TRANSPARENCY + "797196";
            String BUILDING_F = PERCENT_TRANSPARENCY + "2F8736";
            String BUILDING_G = PERCENT_TRANSPARENCY + "DB251A";
            String BUILDING_M = PERCENT_TRANSPARENCY + "0093DD";
            String BUILDING_P = PERCENT_TRANSPARENCY + "838280";
            String GYM = PERCENT_TRANSPARENCY + "C4B16F";
            String CC = PERCENT_TRANSPARENCY + "F399A2";
            String STUDENT_PAVILLION = PERCENT_TRANSPARENCY + "2A166F";
            String RESIDENCE = PERCENT_TRANSPARENCY + "3B746B";
            String OTHER = PERCENT_TRANSPARENCY + "8CC6B7";
            String OUTSIDE = PERCENT_TRANSPARENCY;
        }


    }

    public static class Parse {
        public interface Keys {
            String APPLICATION_ID = "iG6wYvJYmAugroxDqKjHze2XDsBEL7n7vjxi1tYq";
            String CLIENT_KEY = "mtqIW7H7ZMxlNk7cpdto7VutCm3zKV0xQLmpZRKx";

            String PARSE_ACCOUNT_TYPE = "pt.isel.pdm.g04.pf.parse";
        }

        public interface Cursors {
            int NOTIFICATIONS_LOADER = 2;
            int SUBSCRIPTIONS_LOADER = 3;
        }
    }
}
