package vvv.gatepass;

import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static android.Manifest.permission.READ_CONTACTS;

/*
    TODO: Clean activity_login
*/

/**
 * A login screen that offers login via email/mPassword.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    public String rPass, rUserType, rUserName, rEnrollKey, rFullName, rBranch, rJoinDate, rHostel, rRoom, rContact, rEmail, rAddress;
    public String mPassword;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mLoginFormView;
    public TextView desc,textViewName;
    public String fontpath = "fonts/ROCK.TTF";
    private Intent user_page_intent;
    private AppData mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();
        mPasswordView = (EditText) findViewById(R.id.password);

        textViewName = (TextView) findViewById(R.id.textViewName);
        desc = (TextView) findViewById(R.id.desc);

        mSession = new AppData(getApplicationContext());
        if (mSession.isLoggedIn()) {
            Intent intent = new Intent(this, Container.class);
            startActivity(intent);
            finish();
        }

        if (savedInstanceState != null) {
            AppData.LoginDetails = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
            String restoredEmail = AppData.LoginDetails.getString("rUserName", "");
            String restoredPassword = AppData.LoginDetails.getString("rPass", "");
            mEmailView.setText(restoredEmail);
            mPasswordView.setText(restoredPassword);
        }

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        user_page_intent = new Intent(this, Container.class);
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
    }


    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     *
     * TODO: Move the password check to server side.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid mPassword, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            if (isNetworkAvailable()) {
                mAuthTask = new UserLoginTask();
                mAuthTask.execute(email);
                mPassword = password;
            } else {
                ShowDialog(LoginActivity.this, "Unable to Connect", "Please Check your Internet connection.");
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void ShowDialog(Context context, String Title, String Message) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(Title);
        alertDialog.setMessage(Message);
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialog.show();
    }

    /*
        TODO: Remove true after proper database is created.
    */

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.endsWith("@st.niituniversity.in") || email.endsWith("@niituniversity.in") || true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 0;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    @Override
    public void onBackPressed() {
        //Container.super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
        finish();
    }



    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
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
                JSONObject json = jsonParser.makeHttpRequest(AppData.ULRCheckLogin, "GET", params);
                if (json != null) {
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
                    rUserType = json.getString("u_type");
                    rPass = json.getString("u_password");
                    rUserName = json.getString("u_name");
                    rEnrollKey = json.getString("u_enroll");
                    rFullName = json.getString("u_fname");
                    rBranch = json.getString("u_course");
                    rJoinDate = json.getString("u_joining");
                    rHostel = json.getString("u_hostel");
                    rRoom = json.getString("u_room");
                    rContact = json.getString("u_contact");
                    rEmail = json.getString("u_email");
                    rAddress = json.getString("u_address");
                    //r_pic = json.getString("u_pic");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (Objects.equals(rPass, mPassword)) {
                AppData.LoginDetails = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                SharedPreferences.Editor editor = AppData.LoginDetails.edit();
                editor.putString("ACC_NAME", rUserName);
                editor.putString("ACC_PASS", rPass);
                editor.apply();

                AppData.LoggedInUser = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                SharedPreferences.Editor editor2 = AppData.LoginDetails.edit();
                editor2.putString("rUserType", rUserType);
                editor2.putString("rPass", rPass);
                editor2.putString("rUserName", rUserName);
                editor2.putString("rEnrollKey", rEnrollKey);
                editor2.putString("rFullName", rFullName);
                editor2.putString("rBranch", rBranch);
                editor2.putString("rJoinDate", rJoinDate);
                editor2.putString("rHostel", rHostel);
                editor2.putString("rRoom", rRoom);
                editor2.putString("rContact", rContact);
                editor2.putString("rEmail", rEmail);
                editor2.putString("rAddress", rAddress);
                editor2.apply();;

                Toast.makeText(LoginActivity.this, "Authenticated", Toast.LENGTH_LONG).show();
                user_page_intent.putExtra("ACC_TYPE", rUserType);
                user_page_intent.putExtra("ACC_NAME", rUserName);


                mSession.setLogin(true);
                startActivity(user_page_intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Wrong Password", Toast.LENGTH_LONG).show();
            }
        }
    }
}
