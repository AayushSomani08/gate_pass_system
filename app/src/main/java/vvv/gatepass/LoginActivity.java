package vvv.gatepass;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    private View mProgressView;
    private View mLoginFormView;

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private TextView textViewName;
    private TextView desc;
    private Intent user_page_intent;
    public String r_pass, r_type, r_user, r_enroll, r_fname, r_course, r_joining, r_hostel, r_room, r_contact, r_email, r_address;
    public String password;
    String fontPath="fonts/ROCK.TTF";
    private SessionManager session;

    private static final String LOGIN_URL = "http://gatepass.esy.es/checklogin.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);
        textViewName = (TextView) findViewById(R.id.textViewName);
        desc =(TextView) findViewById(R.id.desc);
        Typeface type=Typeface.createFromAsset(getAssets(), fontPath);
        textViewName.setTypeface(type);
        desc.setTypeface(type);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, Container.class);
            startActivity(intent);
            finish();
        }

        if (savedInstanceState == null) {
            SharedPreferences prefs = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
            String restoredEmail = prefs.getString("Email", "");
            String restoredPassword = prefs.getString("Password", "");
            mEmailView.setText(restoredEmail);
            mPasswordView.setText(restoredPassword);
        }

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    //attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //attemptLogin();
                String email = mEmailView.getText().toString();
                password = mPasswordView.getText().toString();
                new UserLoginTask().execute(email);
            }
        });
        user_page_intent = new Intent(LoginActivity.this, Container.class);
    }


    /*private void attemptLogin() {

        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            new UserLoginTask().execute(email);
        }
    }*/

    //LOGIN TASK
    class UserLoginTask extends AsyncTask<String, String, JSONObject> {

        JSONParser jsonParser = new JSONParser();
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Attempting login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            try {

                HashMap<String, String> params = new HashMap<>();
                params.put("user_name", args[0]);

                Log.d("request", "starting");

                JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "GET", params);

                if (json != null) {
                    Log.d("JSON result", json.toString());

                    return json;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(JSONObject json) {

            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if (json != null) {

                try {
                    r_type = json.getString("u_type");
                    r_pass = json.getString("u_password");
                    r_user = json.getString("u_name");
                    r_enroll = json.getString("u_enroll");
                    r_fname = json.getString("u_fname");
                    r_course = json.getString("u_course");
                    r_joining = json.getString("u_joining");
                    r_hostel = json.getString("u_hostel");
                    r_room = json.getString("u_room");
                    r_contact = json.getString("u_contact");
                    r_email = json.getString("u_email");
                    r_address = json.getString("u_address");
                    //r_pic = json.getString("u_pic");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (Objects.equals(r_pass, password)){

                Toast.makeText(LoginActivity.this, "Authenticated", Toast.LENGTH_LONG).show();
                user_page_intent.putExtra("ACC_TYPE", r_type);
                user_page_intent.putExtra("ACC_NAME", r_user);
                session.setLogin(true);
                startActivity(user_page_intent);
                finish();
            }else{

                Toast.makeText(LoginActivity.this, "Wrong Password", Toast.LENGTH_LONG).show();
            }
        }
    }
}