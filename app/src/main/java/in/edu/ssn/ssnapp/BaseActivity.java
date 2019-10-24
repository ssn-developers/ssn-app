package in.edu.ssn.ssnapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.TypefaceCompat;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import in.edu.ssn.ssnapp.utils.FontChanger;
import in.edu.ssn.ssnapp.utils.SharedPref;

public class BaseActivity extends AppCompatActivity {

    //Fonts
    //public Typeface regular, bold, semi_bold;
    public boolean darkModeEnabled = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        darkModeEnabled = SharedPref.getBoolean(getApplicationContext(),"dark_mode");
    }

    public void clearLightStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = activity.getWindow().getDecorView().getSystemUiVisibility(); // get current flag
            flags = flags ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR; // use XOR here for remove LIGHT_STATUS_BAR from flags
            activity.getWindow().getDecorView().setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(getResources().getColor(R.color.darkColor));
        }
    }

}
