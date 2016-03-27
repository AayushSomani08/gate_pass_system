package vvv.gatepass;

import android.content.SharedPreferences;

/**
 * Created by Pradumn K Mahanta on 10-03-2016.
 */
public class AppData {
    public static final String ULRCheckLogin = "http://gatepass.esy.es/checklogin.php";

    public static final String ULRAddRequests = "http://gatepass.esy.es/addrequest.php";

    public static final String ULRGetRequests = "http://gatepass.esy.es/getrequests.php";

    public static SharedPreferences LoginDetails;

    public static SharedPreferences LoggedInUser;

}
