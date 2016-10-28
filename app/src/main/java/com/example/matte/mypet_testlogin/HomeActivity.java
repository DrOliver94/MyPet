package com.example.matte.mypet_testlogin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    public static MyPetDB dbManager;
    public static SharedPreferences shPref;

    private DrawerLayout drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Drawer
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);   //preseleziona il primo bottone del drawer

        //DB
        dbManager = new MyPetDB(this);

        //SharedPref
        shPref = getSharedPreferences("MyPetPrefs", MODE_PRIVATE);

        //Impostazione del fragment al caricamento
        setTitle("Feed");
        if (findViewById(R.id.main_fragment) != null) {
            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            FeedFragment firstFragment = new FeedFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment, firstFragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        //Se il drawer e' aperto, lo chiude
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //Va indietro solo se sono presenti fragment nel BackStack
            if(getFragmentManager().getBackStackEntryCount()!=0)
                super.onBackPressed();
            else
                drawer.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_settings:
                return true;    //gestito qui
            case R.id.menuEditProfile:
                return false;   //gestito nel fragment
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment, ProfileFragment.newInstance(shPref.getString("IdUser", "")))
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.nav_feed) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment, new FeedFragment())
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.nav_reminder) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment, new ReminderFragment())
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.nav_animals) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment, AnimalsListFragment.newInstance(shPref.getString("IdUser", "")))
                    .addToBackStack(null)
                    .commit();
        } else if(id == R.id.nav_friends) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment, FriendsFragment.newInstance(shPref.getString("IdUser", "")))
                        .addToBackStack(null)
                        .commit();
        } else if (id == R.id.nav_logout) {
            //Cancella token in shPref => e' come fare logout
            SharedPreferences.Editor editor = shPref.edit();
            editor.remove("Token").apply();

            //Si torna alla schermata di login
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

//    @Override
//    public void onFragmentInteraction(String name) {
//        setTitle(name);
//    }
}
