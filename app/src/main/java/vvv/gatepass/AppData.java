package vvv.gatepass;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Pradumn K Mahanta on 10-03-2016.
 */
public class AppData {
    public static final String ULRCheckLogin = "http://gatepass.esy.es/checklogin.php";

    public static final String ULRAddRequests = "http://gatepass.esy.es/addrequest.php";

    public static final String ULRGetRequests = "http://gatepass.esy.es/getrequests.php";

    public static final String ULRUpdateRequests = "http://gatepass.esy.es/updaterequests.php";

    public static final String ULRGetRequestsWARDEN = "http://gatepass.esy.es/wardengetrequests.php";

    public static SharedPreferences LoginDetails;

    public static SharedPreferences LoggedInUser;

    private static final String PREF_NAME = "GatepassSystem";
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static String TAG = AppData.class.getSimpleName();

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    public AppData(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.commit();
        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn() {

        return pref.getBoolean(KEY_IS_LOGGEDIN, false);

    }

}
