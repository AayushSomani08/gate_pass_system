package vvv.gatepass;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserProfileInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfile extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public String rPass, rUserType, rUserName, rEnrollKey, rFullName, rBranch, rJoinDate, rHostel, rRoom, rContact, rEmail, rAddress;

    TextView fNameII, UserNameII, EnrollmentII, BranchII, NumberII, RoomII;
    Button LogOut, OCGP;
    private AppData mSession;

    private UserProfileInteractionListener mListener;

    public UserProfile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.user_profile_fragment, container, false);

        AppData.LoggedInUser = PreferenceManager.getDefaultSharedPreferences(getActivity());
        rUserName = AppData.LoggedInUser.getString("rUserName", "");
        rEnrollKey = AppData.LoggedInUser.getString("rEnrollKey", "");
        rFullName = AppData.LoggedInUser.getString("rFullName", "");
        rBranch = AppData.LoggedInUser.getString("rBranch", "");
        rRoom = AppData.LoggedInUser.getString("rRoom", "");
        rContact = AppData.LoggedInUser.getString("rContact", "");
        Log.d("Bundle : ", "Restored.");

        mSession = new AppData(getActivity());

        fNameII = (TextView) view.findViewById(R.id.fNameII);
        fNameII.setText(rFullName);
        UserNameII = (TextView) view.findViewById(R.id.UserNameII);
        UserNameII.setText(rUserName);
        EnrollmentII = (TextView) view.findViewById(R.id.EnrollmentII);
        EnrollmentII.setText(rEnrollKey);
        BranchII = (TextView) view.findViewById(R.id.BranchII);
        BranchII.setText(rBranch);
        NumberII = (TextView) view.findViewById(R.id.NumberII);
        NumberII.setText(rContact);
        RoomII = (TextView) view.findViewById(R.id.RoomII);
        RoomII.setText(rRoom);

        LogOut = (Button) view.findViewById(R.id.LogOut);
        LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Press", "Button Pressed LOG OUT");
                mSession.setLogin(false);
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        OCGP = (Button) view.findViewById(R.id.OCGP);
        OCGP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Press", "Button Pressed OCGP");
                AddRequest addRequest = new AddRequest(getActivity());
                addRequest.execute();
            }
        });
        return view;
    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static UserProfile newInstance(String param1, String param2) {
        UserProfile fragment = new UserProfile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.UserProfileInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UserProfileInteractionListener) {
            mListener = (UserProfileInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement LocalGatepassInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface UserProfileInteractionListener {
        // TODO: Update argument type and name
        void UserProfileInteraction(Uri uri);
    }

    class AddRequest extends AsyncTask<String, String, JSONObject> {

        JSONParser jsonParser = new JSONParser();
        private ProgressDialog pDialog;
        Context ctxt;

        AddRequest(Context ctx){
            ctxt = ctx;
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Sending Request...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("student_name", rFullName);
                params.put("user_name", rUserName);
                params.put("purpose", "Fixed Gatepass");
                params.put("request_status", "Pending");
                params.put("request_to", "dummy.warden");
                params.put("enrollment_no", rEnrollKey);

                Calendar mDate = Calendar.getInstance();
                int mtodaysDate = mDate.get(Calendar.DAY_OF_MONTH);
                int mMonth = mDate.get(Calendar.MONTH);
                int mYear = mDate.get(Calendar.YEAR);
                params.put("out_date", String.valueOf(mtodaysDate + "/" + mMonth + "/" + mYear));
                params.put("out_time", "17:30");
                params.put("in_date", String.valueOf(mtodaysDate + "/" + mMonth + "/" + mYear));
                params.put("in_time", "21:30");

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                int seconds = mcurrentTime.get(Calendar.SECOND);
                params.put("request_time", String.valueOf(hour) + ":" + String.valueOf(minute) + ":" + String.valueOf(seconds));
                params.put("approved_time", "Not Required");
                params.put("visit_place", "Local Areas");
                params.put("visit_type", "Others");
                params.put("contact_number", rContact);

                JSONObject json = jsonParser.makeHttpRequest(AppData.ULRAddRequests, "GET", params);
                if (json != null) {
                    return json;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(JSONObject json) {

            pDialog.setMessage("Sending Request...");

            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
            try {
                if (json.getBoolean("result")) {
                    Toast.makeText(ctxt, "Request made.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ctxt, "Request Failed.", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState = null;
        super.onSaveInstanceState(outState);
    }
}
