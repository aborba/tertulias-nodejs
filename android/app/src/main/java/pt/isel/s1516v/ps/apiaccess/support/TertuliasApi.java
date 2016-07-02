package pt.isel.s1516v.ps.apiaccess.support;

import com.microsoft.windowsazure.mobileservices.http.HttpConstants;

public interface TertuliasApi {

    // HTTP Methods aliases
    String HTTP_GET = HttpConstants.GetMethod;
    String HTTP_POST = HttpConstants.PostMethod;
    String HTTP_PUT = HttpConstants.PutMethod;

    // Endpoints keys
    String ROUTE_END_POINT_LABEL = "RouteEndPoint";
    String ROUTE_METHOD_LABEL = "RouteMethod";
    String GET_TERTULIAS = "getTertulias";
    String POST_TERTULIAS = "postTertulias";
    String POST_REGISTRATION = "postRegistration";

    // Links
    String LINK_SELF = "self";
    String LINK_CREATE = "create";
    String LINK_UPDATE = "update";
    String LINK_DELETE = "delete";


    // Activity Return Codes
    int NEW_TERTULIA_RETURN_CODE = 1;
    int SUBSCRIBE_TERTULIA_RETURN_CODE = 2;

    // Activity Result Codes
    int RESULT_SUCCESS = 1;
    int RESULT_FAIL = 0;

    // To Delete
    String USER_EDIT_API_END_POINT = "profile";
}
