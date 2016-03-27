package vvv.gatepass.dummy;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import vvv.gatepass.AppData;
import vvv.gatepass.JSONParser;
import vvv.gatepass.JSONParserArray;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    /*
    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }
    */

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static DummyItem createDummyItem(int position) {
        return new DummyItem(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }
    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public final String id;
        public final String content;
        public final String details;

        public DummyItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }

    class GetRequests extends AsyncTask<String, String, JSONArray> {

        JSONParserArray jsonParser = new JSONParserArray();

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected JSONArray doInBackground(String... args) {
            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("user_name", args[0]);
                JSONArray json = jsonParser.makeHttpRequest(AppData.ULRGetRequests, "GET", params);
                if (json != null) {
                    return json;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(JSONArray jsonAr) {
            String rStudentName, rUserName, rRequestStatus, rRequestTo,
                    rEnrollmentNo, rOutDate, rOutTime, rInDate, rInTime,
                    rRequestTime, rApprovedTime, rVisitPlace, rVisitType,
                    rContactNo, rGatepassNumber;

            try {

                for (int i=0; i<jsonAr.length(); i++) {
                    JSONObject json = jsonAr.getJSONObject(i);
                    rGatepassNumber = json.getString("gatepass_number");
                    rStudentName = json.getString("student_name");
                    rUserName = json.getString("user_name");
                    rRequestStatus = json.getString("request_status");
                    rRequestTo = json.getString("request_to");
                    rEnrollmentNo = json.getString("enrollment_no");
                    rOutDate = json.getString("out_date");
                    rOutTime = json.getString("out_time");
                    rInDate = json.getString("in_date");
                    rInTime = json.getString("in_time");
                    rRequestTime = json.getString("request_time");
                    rApprovedTime = json.getString("approved_time");
                    rVisitPlace = json.getString("visit_place");
                    rVisitType = json.getString("visit_type");
                    rContactNo = json.getString("contact_number");
                    addItem(createDummyItem(Integer.getInteger(rGatepassNumber)));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    }
}
