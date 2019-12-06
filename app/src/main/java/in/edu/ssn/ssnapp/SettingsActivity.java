package in.edu.ssn.ssnapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import spencerstudios.com.bungeelib.Bungee;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_dark);

        /******************************************************************
        //Darkmode handle

        if (darkModeEnabled)
            darkModeSwitch.setChecked(true);
        else
            darkModeSwitch.setChecked(false);

        darkModeSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    darkModeEnabled = true;
                    SharedPref.putBoolean(getApplicationContext(), "dark_mode", darkModeEnabled);
                    finish();
                    startActivity(getIntent());
                    Bungee.fade(StudentHomeActivity.this);
                }
                else {
                    darkModeEnabled = false;
                    SharedPref.putBoolean(getApplicationContext(), "dark_mode", darkModeEnabled);
                    finish();
                    startActivity(getIntent());
                    Bungee.fade(StudentHomeActivity.this);
                }
            }
        });

         */

        /******************************************************************
        //Notification handle for Alumni

         RelativeLayout layout_alum_notif;
         SwitchButton darkModeSwitch, notifSwitch;

        if(SharedPref.getInt(getApplicationContext(),"clearance") == 2){
            layout_alum_notif.setVisibility(View.VISIBLE);
            notifSwitch.setChecked(SharedPref.getBoolean(getApplicationContext(), "switch_event"));
        }
        else
            layout_alum_notif.setVisibility(View.GONE);

        notifSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                SharedPref.putBoolean(getApplicationContext(),"switch_event", isChecked);
                notifSwitch.setChecked(isChecked);

                if(isChecked)
                    FCMHelper.SubscribeToTopic(StudentHomeActivity.this, Constants.Event);
                else
                    FCMHelper.UnSubscribeToTopic(StudentHomeActivity.this, Constants.Event);
            }
        });

         */

         /*************************************************************

        if (SharedPref.getInt(getApplicationContext(), "clearance") == 0) {
            adapter.add(new Drawer("News Feed", R.drawable.ic_feeds));
            adapter.add(new Drawer("Favourites", R.drawable.ic_fav));
        }
        else
            adapter.add(new Drawer("Club Feed", R.drawable.ic_feeds));

        adapter.add(new Drawer("AlmaConnect", R.drawable.ic_alumni));

        if (SharedPref.getInt(getApplicationContext(), "clearance") != 2) {
            adapter.add(new Drawer("Notification Settings", R.drawable.ic_notify_grey));
        }

        adapter.add(new Drawer("Helpline", R.drawable.ic_phone));
        adapter.add(new Drawer("Make a Suggestion", R.drawable.ic_feedback));
        adapter.add(new Drawer("Invite Friends", R.drawable.ic_invite));
        adapter.add(new Drawer("Rate Our App", R.drawable.ic_star));
        adapter.add(new Drawer("Privacy Policy", R.drawable.ic_feedback));
        adapter.add(new Drawer("App Info", R.drawable.ic_info));
        adapter.add(new Drawer("Logout", R.drawable.ic_logout));

        switch (rs.getTitle()) {
            case "News Feed":
            case "Club Feed":
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case "Favourites":
                startActivity(new Intent(getApplicationContext(), FavouritesActivity.class));
                Bungee.slideLeft(StudentHomeActivity.this);
                break;
            case "AlmaConnect":
                if (!CommonUtils.alerter(getApplicationContext())) {
                    CommonUtils.openCustomBrowser(getApplicationContext(),"https://ssn.almaconnect.com");
                } else {
                    Intent intent = new Intent(getApplicationContext(), NoNetworkActivity.class);
                    intent.putExtra("key", "home");
                    startActivity(intent);
                    Bungee.fade(StudentHomeActivity.this);
                }
                break;
            case "Notification Settings":
                startActivity(new Intent(getApplicationContext(), NotificationSettings.class));
                Bungee.slideLeft(StudentHomeActivity.this);
                break;
            case "Invite Friends":
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hi all! Manage your internals, results & exam schedule with ease and Find your bus routes on the go! Click here to stay updated on department, club or placement feeds and events: https://play.google.com/store/apps/details?id=" + StudentHomeActivity.this.getPackageName();
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                break;
            case "Rate Our App":
                if (!CommonUtils.alerter(getApplicationContext())) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                } else {
                    Intent intent = new Intent(getApplicationContext(), NoNetworkActivity.class);
                    intent.putExtra("key", "home");
                    startActivity(intent);
                    Bungee.fade(StudentHomeActivity.this);
                }
                break;
            case "Helpline":
                startActivity(new Intent(getApplicationContext(), FunctionalHeadsActivity.class));
                Bungee.slideLeft(StudentHomeActivity.this);
                break;
            case "Make a Suggestion":
                startActivity(new Intent(getApplicationContext(), FeedbackActivity.class));
                Bungee.slideLeft(StudentHomeActivity.this);
                break;
            case "App Info":
                startActivity(new Intent(getApplicationContext(), AppInfoActivity.class));
                Bungee.slideLeft(StudentHomeActivity.this);
                break;
            case "Privacy Policy":
                if (!CommonUtils.alerter(getApplicationContext())) {
                    CommonUtils.openCustomBrowser(getApplicationContext(),Constants.termsfeed);
                } else {
                    Intent intent = new Intent(getApplicationContext(), NoNetworkActivity.class);
                    intent.putExtra("key", "home");
                    startActivity(intent);
                    Bungee.fade(StudentHomeActivity.this);
                }
                break;
            case "Logout":
                FirebaseAuth.getInstance().signOut();
                CommonUtils.UnSubscribeToAlerts(getApplicationContext());
                DataBaseHelper dbHelper = DataBaseHelper.getInstance(StudentHomeActivity.this);
                dbHelper.dropAllTables();
                SharedPref.removeAll(getApplicationContext());
                SharedPref.putInt(getApplicationContext(), "dont_delete", "is_logged_in", 1);

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                Bungee.slideLeft(StudentHomeActivity.this);

                break;
        }

        */
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bungee.slideRight(SettingsActivity.this);
    }
}
