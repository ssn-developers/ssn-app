package in.edu.ssn.ssnapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.suke.widget.SwitchButton;

import in.edu.ssn.ssnapp.database.DataBaseHelper;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.FCMHelper;
import in.edu.ssn.ssnapp.utils.SharedPref;
import spencerstudios.com.bungeelib.Bungee;

// Extends Base activity for darkmode variable and status bar.
public class SettingsActivity extends BaseActivity {

    SwitchButton darkmodeSB, newsfeedSB, busalertSB, examcellSB, placementsSB, eventsSB, chatSB, notifSwitch;
    TextView eventsTV, almaconnectTV, helplineTV, contributorTV, feedbackTV, inviteTV, ratingTV, privacyTV, logoutTV, calendar_TV;
    RelativeLayout newsfeedRL, busalertRL, examcellRL, placementsRL, eventsRL, chatRL;
    LinearLayout notificationLL;
    RelativeLayout almaconnectRL, facultyRL;
    ImageView backIV;
    private int clearance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //check if darkmode is enabled and open the appropriate layout.
        if (darkModeEnabled) {
            setContentView(R.layout.activity_settings_dark);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                getWindow().setStatusBarColor(getResources().getColor(R.color.darkColorLight));
        } else
            setContentView(R.layout.activity_settings);

        //Get the User type such as Ug student[0], PG student[1], Alumni[2] or Faculty[3]
        clearance = SharedPref.getInt(getApplicationContext(), "clearance");

        initUI(clearance);

        //on/off switch handling for DarkMode.
        darkmodeSB.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    darkModeEnabled = true;
                    SharedPref.putBoolean(getApplicationContext(), "dark_mode", darkModeEnabled);
                    finish();
                    startActivity(getIntent());
                    Bungee.fade(SettingsActivity.this);
                } else {
                    darkModeEnabled = false;
                    SharedPref.putBoolean(getApplicationContext(), "dark_mode", darkModeEnabled);
                    finish();
                    startActivity(getIntent());
                    Bungee.fade(SettingsActivity.this);
                }
            }
        });

        //almaconnect.
        almaconnectTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Active internet connection
                if (!CommonUtils.alerter(getApplicationContext())) {
                    CommonUtils.openCustomBrowser(getApplicationContext(), "https://ssn.almaconnect.com");
                }
                //No Active internet connection
                else {
                    Intent intent = new Intent(getApplicationContext(), NoNetworkActivity.class);
                    intent.putExtra("key", "home");
                    startActivity(intent);
                    Bungee.fade(SettingsActivity.this);
                }
            }
        });

        //helpline.
        helplineTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), FunctionalHeadsActivity.class));
                Bungee.slideLeft(SettingsActivity.this);
            }
        });

        //contributors.
        contributorTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AppInfoActivity.class));
                Bungee.slideLeft(SettingsActivity.this);
            }
        });

        //SSN Academic Calender.
        calendar_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), PdfViewerActivity.class);
                i.putExtra(Constants.PDF_URL, Constants.calendar);
                startActivity(i);
                Bungee.fade(SettingsActivity.this);
            }
        });

        //make a suggestion
        feedbackTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), FeedbackActivity.class));
                Bungee.slideLeft(SettingsActivity.this);
            }
        });

        //invite friends
        inviteTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hi all! Manage your internals, results & exam schedule with ease and Find your bus routes on the go! Click here to stay updated on department, club or placement feeds and events: https://play.google.com/store/apps/details?id=" + SettingsActivity.this.getPackageName();
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        //rate our app
        ratingTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!CommonUtils.alerter(getApplicationContext())) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                } else {
                    Intent intent = new Intent(getApplicationContext(), NoNetworkActivity.class);
                    intent.putExtra("key", "home");
                    startActivity(intent);
                    Bungee.fade(SettingsActivity.this);
                }
            }
        });

        //privacy documentation
        privacyTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!CommonUtils.alerter(getApplicationContext())) {
                    CommonUtils.openCustomBrowser(getApplicationContext(), Constants.termsfeed);
                } else {
                    Intent intent = new Intent(getApplicationContext(), NoNetworkActivity.class);
                    intent.putExtra("key", "home");
                    startActivity(intent);
                    Bungee.fade(SettingsActivity.this);
                }
            }
        });

        //logout
        logoutTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                CommonUtils.UnSubscribeToAlerts(getApplicationContext());
                DataBaseHelper dbHelper = DataBaseHelper.getInstance(SettingsActivity.this);
                dbHelper.dropAllTables();
                SharedPref.removeAll(getApplicationContext());

                SharedPref.putInt(getApplicationContext(), "dont_delete", "is_logged_in", 1);
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                Bungee.slideLeft(SettingsActivity.this);
            }
        });

        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    /**********************************************************************/
    // Initiate variables and UI elements.
    public void initUI(int clearance) {
        darkmodeSB = findViewById(R.id.darkModeSwitch);
        notifSwitch = findViewById(R.id.notifSwitch);
        newsfeedSB = findViewById(R.id.switch_dept);
        busalertSB = findViewById(R.id.switch_bus);
        examcellSB = findViewById(R.id.switch_exam);
        placementsSB = findViewById(R.id.switch_place);
        eventsSB = findViewById(R.id.switch_event);
        chatSB = findViewById(R.id.switch_chat);

        eventsTV = findViewById(R.id.event_TV);
        almaconnectTV = findViewById(R.id.almaconnetTV);
        helplineTV = findViewById(R.id.helplineTV);
        feedbackTV = findViewById(R.id.suggestionTV);
        inviteTV = findViewById(R.id.inviteTV);
        ratingTV = findViewById(R.id.rateTV);
        privacyTV = findViewById(R.id.privacyTV);
        logoutTV = findViewById(R.id.logoutTV);
        contributorTV = findViewById(R.id.contributors_TV);
        calendar_TV = findViewById(R.id.calendar_TV);

        newsfeedRL = findViewById(R.id.newsfeedRL);
        busalertRL = findViewById(R.id.busalertRL);
        examcellRL = findViewById(R.id.examcellRL);
        placementsRL = findViewById(R.id.placementRL);
        eventsRL = findViewById(R.id.eventRL);
        chatRL = findViewById(R.id.chatRL);

        notificationLL = findViewById(R.id.notificationLL);
        almaconnectRL = findViewById(R.id.almaconnetLL);
        facultyRL = findViewById(R.id.facultyRL);

        backIV = findViewById(R.id.backIV);

        //darkmode switch handling
        if (darkModeEnabled)
            darkmodeSB.setChecked(true);
        else
            darkmodeSB.setChecked(false);

        //buttons visibility setting
        //for UnderGraduate...
        if (clearance == 0) {
            newsfeedSB.setChecked(SharedPref.getBoolean(getApplicationContext(), "switch_dept"));
            newsfeedRL.setVisibility(View.VISIBLE);

            busalertSB.setChecked(SharedPref.getBoolean(getApplicationContext(), "switch_bus"));
            busalertRL.setVisibility(View.VISIBLE);

            examcellSB.setChecked(SharedPref.getBoolean(getApplicationContext(), "switch_exam"));
            examcellRL.setVisibility(View.VISIBLE);

            eventsSB.setChecked(SharedPref.getBoolean(getApplicationContext(), "switch_event"));
            eventsRL.setVisibility(View.VISIBLE);

            if (SharedPref.getInt(getApplicationContext(), "year") == Integer.parseInt(Constants.fourth)) {
                placementsSB.setChecked(SharedPref.getBoolean(getApplicationContext(), "switch_place"));
                placementsRL.setVisibility(View.VISIBLE);
            } else
                placementsRL.setVisibility(View.GONE);

            chatSB.setChecked(SharedPref.getBoolean(getApplicationContext(), "switch_global_chat"));
            chatRL.setVisibility(View.VISIBLE);
        }

        //for PostGraduate...
        if (clearance == 1) {
            busalertSB.setChecked(SharedPref.getBoolean(getApplicationContext(), "switch_bus"));
            busalertRL.setVisibility(View.VISIBLE);

            eventsSB.setChecked(SharedPref.getBoolean(getApplicationContext(), "switch_event"));
            eventsRL.setVisibility(View.VISIBLE);

            chatSB.setChecked(SharedPref.getBoolean(getApplicationContext(), "switch_global_chat"));
            chatRL.setVisibility(View.VISIBLE);

            placementsRL.setVisibility(View.GONE);
            newsfeedRL.setVisibility(View.GONE);
            examcellRL.setVisibility(View.GONE);
        }

        //for Alumni and Faculty...
        if (clearance == 2 || clearance == 3) {
            notifSwitch.setChecked(SharedPref.getBoolean(getApplicationContext(), "notif_switch"));
            facultyRL.setVisibility(View.VISIBLE);
            notificationLL.setVisibility(View.GONE);
        }
    }
    /**********************************************************************/

    /**********************************************************************/
    //Setting up Firebase cloud Messaging for notification subcriptions.
    @Override
    protected void onPause() {
        super.onPause();

        /**********************************************************************/
        //For Undergraduate
        //Newsfeed, Examcell, BusAlert, Events, Chats
        if (clearance == 0) {
            SharedPref.putBoolean(getApplicationContext(), "switch_dept", newsfeedSB.isChecked());
            SharedPref.putBoolean(getApplicationContext(), "switch_exam", examcellSB.isChecked());
            SharedPref.putBoolean(getApplicationContext(), "switch_bus", busalertSB.isChecked());
            SharedPref.putBoolean(getApplicationContext(), "switch_event", eventsSB.isChecked());
            SharedPref.putBoolean(getApplicationContext(), "switch_global_chat", chatSB.isChecked());

            if (newsfeedSB.isChecked())
                FCMHelper.SubscribeToTopic(this, SharedPref.getString(getApplicationContext(), "dept") + SharedPref.getInt(getApplicationContext(), "year"));
            else
                FCMHelper.UnSubscribeToTopic(this, SharedPref.getString(getApplicationContext(), "dept") + SharedPref.getInt(getApplicationContext(), "year"));

            if (examcellSB.isChecked())
                FCMHelper.SubscribeToTopic(this, SharedPref.getString(getApplicationContext(), "dept") + SharedPref.getInt(getApplicationContext(), "year") + "exam");
            else
                FCMHelper.UnSubscribeToTopic(this, SharedPref.getString(getApplicationContext(), "dept") + SharedPref.getInt(getApplicationContext(), "year") + "exam");

            if (busalertSB.isChecked()) {
                FCMHelper.SubscribeToTopic(this, Constants.BUS_ALERTS);
            } else
                FCMHelper.UnSubscribeToTopic(this, Constants.BUS_ALERTS);

            if (eventsSB.isChecked())
                FCMHelper.SubscribeToTopic(this, Constants.Event);
            else
                FCMHelper.UnSubscribeToTopic(this, Constants.Event);

            if (chatSB.isChecked())
                FCMHelper.SubscribeToTopic(getApplicationContext(), Constants.GLOBAL_CHAT);
            else
                FCMHelper.UnSubscribeToTopic(getApplicationContext(), Constants.GLOBAL_CHAT);
        }

        //For Undergraduate Final year
        //(Newsfeed, Examcell, BusAlert, Events, Chats) + PLACEMENTS
        if (SharedPref.getInt(getApplicationContext(), "year") == Integer.parseInt(Constants.fourth)) {
            SharedPref.putBoolean(getApplicationContext(), "switch_place", placementsSB.isChecked());

            if (placementsSB.isChecked())
                FCMHelper.SubscribeToTopic(this, SharedPref.getString(getApplicationContext(), "dept") + SharedPref.getInt(getApplicationContext(), "year") + "place");
            else
                FCMHelper.UnSubscribeToTopic(this, SharedPref.getString(getApplicationContext(), "dept") + SharedPref.getInt(getApplicationContext(), "year") + "place");
        }

        //For Postgraduate
        //BusAlert, Events, Chats
        if (clearance == 1) {
            SharedPref.putBoolean(getApplicationContext(), "switch_bus", busalertSB.isChecked());
            SharedPref.putBoolean(getApplicationContext(), "switch_event", eventsSB.isChecked());
            SharedPref.putBoolean(getApplicationContext(), "switch_global_chat", chatSB.isChecked());

            if (busalertSB.isChecked()) {
                FCMHelper.SubscribeToTopic(this, Constants.BUS_ALERTS);
            } else
                FCMHelper.UnSubscribeToTopic(this, Constants.BUS_ALERTS);

            if (eventsSB.isChecked())
                FCMHelper.SubscribeToTopic(this, Constants.Event);
            else
                FCMHelper.UnSubscribeToTopic(this, Constants.Event);

            if (chatSB.isChecked())
                FCMHelper.SubscribeToTopic(getApplicationContext(), Constants.GLOBAL_CHAT);
            else
                FCMHelper.UnSubscribeToTopic(getApplicationContext(), Constants.GLOBAL_CHAT);
        }

        //For Alumni
        //Events, Chats
        if (clearance == 2) {
            SharedPref.putBoolean(getApplicationContext(), "notif_switch", notifSwitch.isChecked());

            if (notifSwitch.isChecked()) {
                FCMHelper.SubscribeToTopic(this, Constants.Event);
                FCMHelper.SubscribeToTopic(this, Constants.GLOBAL_CHAT);
            } else {
                FCMHelper.UnSubscribeToTopic(this, Constants.Event);
                FCMHelper.UnSubscribeToTopic(this, Constants.GLOBAL_CHAT);
            }
        }

        //For Faculty
        //BusAlert
        if (clearance == 3) {
            SharedPref.putBoolean(getApplicationContext(), "notif_switch", notifSwitch.isChecked());

            if (notifSwitch.isChecked())
                FCMHelper.SubscribeToTopic(this, Constants.BUS_ALERTS);
            else
                FCMHelper.UnSubscribeToTopic(this, Constants.BUS_ALERTS);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        super.darkModeEnabled = SharedPref.getBoolean(getApplicationContext(), "dark_mode");
        Bungee.slideRight(SettingsActivity.this);
    }
}
