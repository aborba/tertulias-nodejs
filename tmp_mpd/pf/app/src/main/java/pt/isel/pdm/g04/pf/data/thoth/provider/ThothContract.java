package pt.isel.pdm.g04.pf.data.thoth.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public interface ThothContract {

    String AUTHORITY = "pt.isel.pdm.g04.pf.data.thoth.provider";
    Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Users extends BaseColumns
    {
        String ACADEMIC_EMAIL = "academicEmail";
        String SHORT_NAME = "shortName";
        String NUMBER = "number";
        String DEFAULT_SORT_ORDER = SHORT_NAME + " ASC";
        String EMAIL_SORT_ORDER = ACADEMIC_EMAIL + " ASC";
        String AVATAR_URL_SIZE32 = "avatarUrlsize32";
        String AVATAR_URL_SIZE128 = "avatarUrlsize128";
        String AVATAR_URL_SIZE64 = "avatarUrlsize64";
        String AVATAR_URL_SIZE24 = "avatarUrlsize24";
        String LINKS_SELF = "_linksself";
    }
    interface Teachers extends Users {
        String RESOURCE = "Teacher";

        Uri CONTENT_URI =
                Uri.withAppendedPath(
                        ThothContract.CONTENT_URI,
                        RESOURCE);

        String FULL_NAME = "fullName";
    }

    interface Students extends Users {
        String RESOURCE = "Student";

        Uri CONTENT_URI =
                Uri.withAppendedPath(
                        ThothContract.CONTENT_URI,
                        RESOURCE);

        String GITHUB_USERNAME = "GitHubUsername";
    }
}
