package vvv.gatepass;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import vvv.gatepass.dummy.DummyContent;

public class Container extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GatepassFragment.OnGatepassListFragmentInteractionListener,
                    FragmentUserProfile.OnFragmentInteractionListener, FragmentLocalGatepass.OnFragmentInteractionListener,
                    FragmentOutStationGatepass.OnFragmentInteractionListener, FragmentNonReturnable.OnFragmentInteractionListener{

    private SessionManager session;
    public String acc_type, acc_name;
    String mUserType;

    MenuItem mPreviousMenuItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        //Session Manager Check if user is logged in.
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                acc_type = "Not Authorised";
                acc_name = "Unknown";
            }
            else {
                acc_type = extras.getString("ACC_TYPE");
                acc_name = extras.getString("ACC_NAME");
                mUserType = acc_type;
            }
            FragmentUserProfile firstFragment = new FragmentUserProfile();
            getSupportFragmentManager().beginTransaction().add(R.id.contentFragment, firstFragment).commit();
        }
        else {
            acc_type = getIntent().getExtras().getString("ACC_TYPE");
            acc_name = getIntent().getExtras().getString("ACC_NAME");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add Local Fixed Gatepass here...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
        name.setText("Name");
        TextView email = (TextView) nav_header.findViewById(R.id.textViewEmail);
        email.setText("Email");
    }

    private void logoutUser() {
        session.setLogin(false);
        Intent intent = new Intent(Container.this, LoginActivity.class);
        startActivity(intent);
    }

    private void expandNavigationView(String acc_type) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        menu.clear();

        String [] menu_list;
        if (acc_type.equals("STUDENT")) {
            menu_list = new String[] {  "User Profile",
                                        "Local Gatepass",
                                        "Out Station Request",
                                        "Non returnable Gatepass",
                                        "Check Gatepass Status",
                                        "Visitor's Gatepass",
                                        "Visitor's Gatepass Status"};
            menu.add(R.id.group_menu, Menu.NONE, Menu.NONE, menu_list[0]).setIcon(R.drawable.ic_menu_camera);
            final SubMenu subMenu = menu.addSubMenu("Gatepass");
            for(int i = 1; i<menu_list.length;i++){
                subMenu.add(R.id.group_menu, Menu.NONE, Menu.NONE, menu_list[i]).setIcon(R.drawable.ic_menu_camera);
            }
        }
        else if (acc_type.equals("WARDEN")) {
            menu_list = new String[] {  "Respond to request",
                                        "View User",
                                        "Visitor request",
                                        "Defaulter's list",
                                        "Blacklist Students",
                                        "Generate Report",
                                        "Checkout report"};
            for ( String aMenu_list: menu_list) {
                menu.add(R.id.group_menu, Menu.NONE, Menu.NONE, aMenu_list).setIcon(R.drawable.ic_menu_camera);
            }
        }
        menu.setGroupCheckable(R.id.group_menu, true, true);
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
                            Intent startMain = new Intent(Intent.ACTION_MAIN);
                            startMain.addCategory(Intent.CATEGORY_HOME);
                            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(startMain);

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

    @SuppressWarnings("StatementWithEmptyBody")
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
         *  1 = LocalGatepass // Exists
         *  2 = OutStationRequest //Exists
         *  3 = NonReturnableGatepass //Exists
         *  4 = GatepassFragment  //Modify
         *  5 = Visitor's Gatepass
         *  6 = Visitor's Gatepass Status
         */
        if (mUserType.equals("STUDENT")) {
            switch (id) {
                case R.id.user_profile:
                    fragment = new FragmentUserProfile();
                    //fragmentClass = FragmentUserProfile.class;
                    break;
                case R.id.local_gatepass:
                    fragment = new FragmentLocalGatepass();
                    //fragmentClass = FragmentLocalGatepass.class;
                    break;
                case R.id.out_stat:
                    fragment = new FragmentOutStationGatepass();
                    //fragmentClass = FragmentOutStationGatepass.class;
                    break;
                case R.id.non_ret:
                    fragment = new FragmentNonReturnable();
                    fragmentClass = FragmentNonReturnable.class;
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
                default:
                    //Change when made
                    fragmentClass = FragmentUserProfile.class;
                    break;
            }
        }

        /*try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
        if(fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.contentFragment, fragment).commit();
        }
        else{
            Log.e("NOT FOUND", "Error in fragment loading...");
        }
        // Highlight the selected item, update the title, and close the drawer
        item.setChecked(true);
        setTitle(item.getTitle());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onGatepassListFragmentInteraction(DummyContent.DummyItem item) {
        Toast.makeText(this, item.toString(), Toast.LENGTH_SHORT).show();
    }

    public void onFragmentInteraction(Uri uri) {
        //Whatevs. Replace with your own fragment code.
    }
}
