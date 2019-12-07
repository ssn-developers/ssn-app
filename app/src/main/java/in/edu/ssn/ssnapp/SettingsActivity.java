package in.edu.ssn.ssnapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.suke.widget.SwitchButton;

import java.util.Set;

import in.edu.ssn.ssnapp.database.DataBaseHelper;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.FCMHelper;
import in.edu.ssn.ssnapp.utils.SharedPref;
import spencerstudios.com.bungeelib.Bungee;

public class SettingsActivity extends BaseActivity {

    private int clearance = 0;
    SwitchButton darkmodeSB, allpostSB, newsfeedSB, busalertSB, examcellSB, placementsSB, eventsSB;
    TextView eventsTV, almaconnectTV, helplineTV, contributorTV, feedbackTV, inviteTV, ratingTV, updatesTV, privacyTV, logoutTV;
    RelativeLayout allpostsRL, newsfeedRL, busalertRL, examcellRL, placementsRL, eventsRL;
    LinearLayout notificationLL, almaconnectLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (darkModeEnabled) {
            setContentView(R.layout.activity_settings_dark);
        } else
            setContentView(R.layout.activity_settings);

        //login clearance
        clearance = SharedPref.getInt(getApplicationContext(), "clearance");

        initUI(clearance);

        //darkmode switch handling
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

        //all posts switch change handling
        allpostSB.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                newsfeedSB.setChecked(isChecked);
                busalertSB.setChecked(isChecked);
                examcellSB.setChecked(isChecked);
                if (SharedPref.getInt(getApplicationContext(), "year") == Integer.parseInt(Constants.fourth))
                    placementsSB.setChecked(isChecked);
                eventsSB.setChecked(isChecked);
            }
        });


        //almaconnect
        almaconnectTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!CommonUtils.alerter(getApplicationContext())) {
                    CommonUtils.openCustomBrowser(getApplicationContext(), "https://ssn.almaconnect.com");
                } else {
                    Intent intent = new Intent(getApplicationContext(), NoNetworkActivity.class);
                    intent.putExtra("key", "home");
                    startActivity(intent);
                    Bungee.fade(SettingsActivity.this);
                }
            }
        });
        //helpline
        helplineTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), FunctionalHeadsActivity.class));
                Bungee.slideLeft(SettingsActivity.this);
            }
        });
        //contributors
        contributorTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AppInfoActivity.class));
                Bungee.slideLeft(SettingsActivity.this);
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
        //check for new updates
        updatesTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String latestVersion = CommonUtils.getLatestVersionName(getApplicationContext());
                if (latestVersion != null && !BuildConfig.VERSION_NAME.equals(latestVersion)) {
                    //update available
                    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
                    final View dialogView = getLayoutInflater().inflate(R.layout.custom_alert_dialog2, null);
                    dialogBuilder.setView(dialogView);
                    dialogBuilder.setCancelable(false);

                    TextView okTV = dialogView.findViewById(R.id.okTV);
                    TextView titleTV = dialogView.findViewById(R.id.titleTV);
                    TextView messageTV = dialogView.findViewById(R.id.messageTV);

                    titleTV.setText("New Update available!");
                    messageTV.setText("Please update your app to the latest version: " + latestVersion );

                    final AlertDialog alertDialog = dialogBuilder.create();
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    alertDialog.show();

                    okTV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                            alertDialog.dismiss();
                            finish();
                        }
                    });
                } else {
                    Toast.makeText(SettingsActivity.this, "Already the latest version", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void initUI(int clearance) {

        darkmodeSB = (SwitchButton) findViewById(R.id.darkModeSwitch);
        allpostSB = (SwitchButton) findViewById(R.id.switch_all);
        newsfeedSB = (SwitchButton) findViewById(R.id.switch_dept);
        busalertSB = (SwitchButton) findViewById(R.id.switch_bus);
        examcellSB = (SwitchButton) findViewById(R.id.switch_exam);
        placementsSB = (SwitchButton) findViewById(R.id.switch_place);
        eventsSB = (SwitchButton) findViewById(R.id.switch_event);

        eventsTV = (TextView) findViewById(R.id.event_TV);
        almaconnectTV = (TextView) findViewById(R.id.almaconnetTV);
        helplineTV = (TextView) findViewById(R.id.helplineTV);
        feedbackTV = (TextView) findViewById(R.id.suggestionTV);
        inviteTV = (TextView) findViewById(R.id.inviteTV);
        ratingTV = (TextView) findViewById(R.id.rateTV);
        privacyTV = (TextView) findViewById(R.id.privacyTV);
        logoutTV = (TextView) findViewById(R.id.logoutTV);
        updatesTV = (TextView) findViewById(R.id.updatesTV);
        contributorTV = (TextView) findViewById(R.id.contributors_TV);

        allpostsRL = (RelativeLayout) findViewById(R.id.allpostRL);
        newsfeedRL = (RelativeLayout) findViewById(R.id.newsfeedRL);
        busalertRL = (RelativeLayout) findViewById(R.id.busalertRL);
        examcellRL = (RelativeLayout) findViewById(R.id.examcellRL);
        placementsRL = (RelativeLayout) findViewById(R.id.placementRL);
        eventsRL = (RelativeLayout) findViewById(R.id.eventRL);

        notificationLL = (LinearLayout) findViewById(R.id.notificationLL);
        almaconnectLL = (LinearLayout) findViewById(R.id.almaconnetLL);

        //darkmode switch handling
        if (darkModeEnabled)
            darkmodeSB.setChecked(true);
        else
            darkmodeSB.setChecked(false);

        //buttons visibility setting
        //for UnderGraduate...
        if (clearance == 0) {
            allpostSB.setChecked(SharedPref.getBoolean(getApplicationContext(), "switch_all"));
            allpostsRL.setVisibility(View.VISIBLE);

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
        }
        //for PostGraduate...
        if (clearance == 1) {
            allpostSB.setChecked(SharedPref.getBoolean(getApplicationContext(), "switch_all"));
            allpostsRL.setVisibility(View.VISIBLE);

            busalertSB.setChecked(SharedPref.getBoolean(getApplicationContext(), "switch_bus"));
            busalertRL.setVisibility(View.VISIBLE);

            eventsSB.setChecked(SharedPref.getBoolean(getApplicationContext(), "switch_event"));
            eventsRL.setVisibility(View.VISIBLE);

            placementsRL.setVisibility(View.GONE);
            newsfeedRL.setVisibility(View.GONE);
            examcellRL.setVisibility(View.GONE);
        }
        //for Alumni...
        if (clearance == 2) {
            eventsSB.setChecked(SharedPref.getBoolean(getApplicationContext(), "switch_event"));
            eventsTV.setText("Notification");
            eventsRL.setVisibility(View.VISIBLE);

            placementsRL.setVisibility(View.GONE);
            newsfeedRL.setVisibility(View.GONE);
            examcellRL.setVisibility(View.GONE);
            allpostsRL.setVisibility(View.GONE);
            busalertRL.setVisibility(View.GONE);
        }
        //for faculty...
        if (clearance == 2) {
            notificationLL.setVisibility(View.GONE);
            almaconnectLL.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPref.putBoolean(getApplicationContext(), "switch_all", allpostSB.isChecked());
        SharedPref.putBoolean(getApplicationContext(), "switch_dept", newsfeedSB.isChecked());
        SharedPref.putBoolean(getApplicationContext(), "switch_bus", busalertSB.isChecked());
        SharedPref.putBoolean(getApplicationContext(), "switch_exam", examcellSB.isChecked());
        if (SharedPref.getInt(getApplicationContext(), "year") == Integer.parseInt(Constants.fourth))
            SharedPref.putBoolean(getApplicationContext(), "switch_place", placementsSB.isChecked());
        SharedPref.putBoolean(getApplicationContext(), "switch_event", eventsSB.isChecked());
        if (clearance == 0) {
            if (newsfeedSB.isChecked())
                FCMHelper.SubscribeToTopic(this, SharedPref.getString(getApplicationContext(), "dept") + SharedPref.getInt(getApplicationContext(), "year"));
            else
                FCMHelper.UnSubscribeToTopic(this, SharedPref.getString(getApplicationContext(), "dept") + SharedPref.getInt(getApplicationContext(), "year"));
            if (examcellSB.isChecked())
                FCMHelper.SubscribeToTopic(this, SharedPref.getString(getApplicationContext(), "dept") + SharedPref.getInt(getApplicationContext(), "year") + "exam");
            else
                FCMHelper.UnSubscribeToTopic(this, SharedPref.getString(getApplicationContext(), "dept") + SharedPref.getInt(getApplicationContext(), "year") + "exam");

        }

        if (busalertSB.isChecked()) {
            FCMHelper.SubscribeToTopic(this, Constants.BUS_ALERTS);
        } else
            FCMHelper.UnSubscribeToTopic(this, Constants.BUS_ALERTS);


        if (SharedPref.getInt(getApplicationContext(), "year") == Integer.parseInt(Constants.fourth)) {
            if (placementsSB.isChecked())
                FCMHelper.SubscribeToTopic(this, SharedPref.getString(getApplicationContext(), "dept") + SharedPref.getInt(getApplicationContext(), "year") + "place");
            else
                FCMHelper.UnSubscribeToTopic(this, SharedPref.getString(getApplicationContext(), "dept") + SharedPref.getInt(getApplicationContext(), "year") + "place");
        }

        if (eventsSB.isChecked())
            FCMHelper.SubscribeToTopic(this, Constants.Event);
        else
            FCMHelper.UnSubscribeToTopic(this, Constants.Event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        super.darkModeEnabled = SharedPref.getBoolean(getApplicationContext(), "dark_mode");
        Bungee.slideRight(SettingsActivity.this);
        finish();
    }
}
