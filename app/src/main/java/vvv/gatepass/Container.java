package vvv.gatepass;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Container extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GatepassFragment.OnGatepassListFragmentInteractionListener,
        UserProfile.UserProfileInteractionListener, NonReturnableGatepass.NonReturnableInteractionListener,
        LocalGatepass.LocalGatepassInteractionListener, OutStationGatepass.OutStationGatepassInteractionListener,
        WARDENGatepassFragment.OnWARDENGatepassListFragmentInteractionListener{

    MenuItem mPreviousMenuItem;
    String mUserType, mUserName;
    AppData isLoggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        String acc_type, acc_name;

        isLoggedInUser = new AppData(getApplicationContext());
        if(!isLoggedInUser.isLoggedIn()){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        if (savedInstanceState != null)
        {
            AppData.LoggedInUser = PreferenceManager.getDefaultSharedPreferences(this);
            acc_type = AppData.LoggedInUser.getString("rUserType", "");
            acc_name = AppData.LoggedInUser.getString("rFullName", "");
            Log.d("Bundle : ", "Restored.");
            mUserType = acc_type;
            mUserName = acc_name;
        }
        else
        {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                AppData.LoggedInUser = PreferenceManager.getDefaultSharedPreferences(this);
                acc_type = AppData.LoggedInUser.getString("rUserType", "");
                acc_name = AppData.LoggedInUser.getString("rFullName", "");
                Log.d("Bundle : ", "Restored.");
                mUserType = acc_type;
                mUserName = acc_name;
            }
            else {
                acc_type = extras.getString("ACC_TYPE");
                acc_name = extras.getString("ACC_HOLDER");
                mUserType = acc_type;
                mUserName = acc_name;
            }
            UserProfile mStartFragment = new UserProfile();
            getSupportFragmentManager().beginTransaction().add(R.id.contentFragment, mStartFragment).commit();
            Log.d("Bundle : ", "Did not restore anything.");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        expandNavigationView(acc_type);

        View nav_header = navigationView.inflateHeaderView(R.layout.nav_header_user_page);
        TextView name = (TextView) nav_header.findViewById(R.id.textViewName);
        name.setText(mUserName);
        TextView email = (TextView) nav_header.findViewById(R.id.textViewEmail);
        email.setText(mUserType);
    }

    private void expandNavigationView(String acc_type) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        menu.clear();
        String [] menu_list;
        if (acc_type.equals("STUDENT")) {
            menu_list = new String[] {  "User Profile", "Local Gatepass", "Out Station Request", "Non returnable Gatepass",
                                        "Check Gatepass Status", "Visitor's Gatepass", "Visitor's Gatepass Status"};

            for (int i = 0, menu_listLength = menu_list.length; i < menu_listLength; i++) {
                String aMenu_list = menu_list[i];
                menu.add(R.id.group_menu, i, Menu.NONE, aMenu_list).setIcon(R.drawable.ic_menu_camera);
            }
        }
        else if (acc_type.equals("WARDEN")) {
            menu_list = new String[] {  "Respond to request", "View User", "Visitor request", "Defaulter's list", "Blacklist Students",
                    "Generate Report", "Checkout report"};
            for (int i = 0, menu_listLength = menu_list.length; i < menu_listLength; i++) {
                String aMenu_list = menu_list[i];
                menu.add(R.id.group_menu, i, Menu.NONE, aMenu_list).setIcon(R.drawable.ic_menu_camera);
            }
        }

        /*
        for ( String aMenu_list: menu_list) {
            menu.add(R.id.group_menu, Menu.NONE, Menu.NONE, aMenu_list).setIcon(R.drawable.ic_menu_camera);
        }*/
        menu.setGroupCheckable(R.id.group_menu, false, true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Really Exit?")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            //Container.super.onBackPressed();
                            Intent a = new Intent(Intent.ACTION_MAIN);
                            a.addCategory(Intent.CATEGORY_HOME);
                            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(a);
                        }
                    }).create().show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (mPreviousMenuItem != null) {
                mPreviousMenuItem.setChecked(false);
            if (mPreviousMenuItem == item)
                return true;
        }
        mPreviousMenuItem = item;
        Fragment fragment = null;
        Class fragmentClass = null;

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /** Add cases for student block
         *  0 = UserProfile  // Exists
         *  1 = Local Gatepass // DONE
         *  2 = OutStationRequest //DONE
         *  3 = NonReturnableGatepass //TO BE DONE
         *  4 = GatepassFragment  //Exists
         *  5 = Visitor's Gatepass
         *  6 = Visitor's Gatepass Status
         */

        if (mUserType.equals("STUDENT")) {
            switch (id) {
                case 0:
                    fragmentClass = UserProfile.class;
                    break;
                case 1:
                    fragmentClass = LocalGatepass.class;
                    break;
                case 2:
                    fragmentClass = OutStationGatepass.class;
                    break;
                case 3:
                    fragmentClass = NonReturnableGatepass.class;
                    break;
                case 4:
                    fragmentClass = GatepassFragment.class;
                    break;
                case 5:
                    fragmentClass = NonReturnableGatepass.class;
                    break;
                case 6:
                    fragmentClass = NonReturnableGatepass.class;
                    break;
                default:
                    fragmentClass = UserProfile.class;
                    break;
            }
        } else {
            /** Add cases for student block
             *  0 = Respond to request
             *  1 = View User
             *  2 = Visitor request
             *  3 = Defaulter's list
             *  4 = Blacklist Students
             *  5 = Generate Report
             *  6 = Checkout report
             *  Replace default with appropriate option
             */
            switch (id) {
                case 0:
                    fragmentClass = WARDENGatepassFragment.class;
                    break;
                default:
                    fragmentClass = UserProfile.class;
                    break;
            }
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contentFragment, fragment).commit();

        // Highlight the selected item, update the title, and close the drawer
        item.setChecked(true);
        setTitle("GatePass System");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Override with your actions.
     */


    @Override
    public void UserProfileInteraction(Uri uri) {

    }

    @Override
    public void LocalGatepassInteraction(Uri uri) {

    }

    @Override
    public void NonReturnableInteraction(Uri uri) {

    }

    @Override
    public void OutStationGatepassInteraction(Uri uri) {

    }

    @Override
    public void onGatepassListFragmentInteraction(Context mContext, GatepassListViewItem item) {
        CustomDialog customDialog = new CustomDialog(mContext, item);
        customDialog.show();
        Log.d("Custom Dialog", "Opened");
    }

    @Override
    public void onWARDENGatepassListFragmentInteraction(Context mContext, GatepassListViewItem item) {
        CustomDialog customDialog = new CustomDialog(mContext, item);
        customDialog.show();
        Log.d("Custom Dialog","Opened");
    }
}
