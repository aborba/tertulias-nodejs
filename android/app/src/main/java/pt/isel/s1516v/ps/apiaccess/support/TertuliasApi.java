/*
 * Copyright (c) 2016 António Borba da Silva
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package pt.isel.s1516v.ps.apiaccess.support;

import com.microsoft.windowsazure.mobileservices.http.HttpConstants;

public interface TertuliasApi {

    // HTTP Methods aliases
    String HTTP_GET = HttpConstants.GetMethod;
    String HTTP_POST = HttpConstants.PostMethod;
    String HTTP_PUT = HttpConstants.PutMethod;

    // Endpoints keys
    String INTENT_LINKS = "Links";
    String ROUTE_END_POINT_LABEL = "RouteEndPoint";
    String ROUTE_METHOD_LABEL = "RouteMethod";
    String GET_TERTULIAS = "getTertulias";
    String POST_TERTULIAS = "postTertulias";
    String POST_REGISTRATION = "postRegistration";

    // Links
    String LINK_SELF = "self";
    String LINK_CREATE = "create";
    String LINK_CREATE_WEEKLY = "create_weekly";
    String LINK_CREATE_MONTHLY = "create_monthly";
    String LINK_CREATE_MONTHLYW = "create_monthlyw";
    String LINK_CREATE_YEARLY = "create_yearly";
    String LINK_CREATE_YEARLYW = "create_yearlyw";
    String LINK_UPDATE = "update";
    String LINK_DELETE = "delete";
    String LINK_SEARCHPUBLIC = "searchPublic";
    String LINK_SUBSCRIBE = "subscribe";
    String LINK_UNSUBSCRIBE = "unsubscribe";
    String LINK_POSTVAUCHER = "voucher";
    String LINK_GETVAUCHERS = "get_vouchers";

    // Activity Return Codes
    int NEW_TERTULIA_RETURN_CODE = 1;
    int PICK_PLACE_RETURN_CODE = 2;
    int WEEKLY_RETURN_CODE = 3;
    int MONTHLY_RETURN_CODE = 4;
    int MONTHLYW_RETURN_CODE = 5;
    int YEARLY_RETURN_CODE = 6;
    int YEARLYW_RETURN_CODE = 7;
    int TERTULIA_DETAILS_RETURN_CODE = 8;
    int EDIT_TERTULIA_RETURN_CODE = 9;
    int SEARCH_CONTACTS_RETURN_CODE = 10;
    int SEARCH_PUBLIC_TERTULIA_RETURN_CODE = 11;
    int SUBSCRIBE_PUBLIC_TERTULIA_RETURN_CODE = 12;

    // Schedule Types
    enum SCHEDULES {

        WEEKLY(0, "Weekly"), MONTHLYD(1, "MonthlyD"), MONTHLYW(2, "MonthlyW"), YEARLY(3, "Yearly"), YEARLYW(4, "YearlyW");
//        WEEKLY("Weekly"), MONTHLYD("MonthlyD"), MONTHLYW("MonthlyW"), YEARLY("Yearly"), YEARLYW("YearlyW");

        public final int id;
        public final String type;

        SCHEDULES(int id, String type) {
            this.id = id;
            this.type = type;
        }

//        SCHEDULES(String type) {
//            this.type = type;
//        }

        public String toString() {
            return type;
        }
    }

    int WEEKLY = 0;
    int MONTHLY = 1;
    int MONTHLYW = 2;
    int YEARLY = 3;
    int YEARLYW = 4;

    // Week Days
    int SUNDAY = 0;
    int MONDAY = 1;
    int TUESDAY = 2;
    int WEDNESDAY = 3;
    int THURSDAY = 4;
    int FRIDAY = 5;
    int SATURDAY = 6;

    // Activity Result Codes
//    int RESULT_SUCCESS = -1;
    int RESULT_FAIL = 0;

    // To Delete
    String USER_EDIT_API_END_POINT = "profile";
}
