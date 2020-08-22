package in.edu.ssn.ssnapp;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import in.edu.ssn.ssnapp.utils.SharedPref;

public class BaseActivity extends AppCompatActivity {

    //Fonts
    //public Typeface regular, bold, semi_bold;
    public boolean darkModeEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        darkModeEnabled = SharedPref.getBoolean(getApplicationContext(), "dark_mode");
    }

    public void clearLightStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            activity.getWindow().setStatusBarColor(getResources().getColor(R.color.darkColor));
    }

}
