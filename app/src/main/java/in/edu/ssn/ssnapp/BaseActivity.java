package in.edu.ssn.ssnapp;

import android.content.Context;
import android.graphics.Typeface;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.crashlytics.android.Crashlytics;

import in.edu.ssn.ssnapp.utils.FontChanger;

public class BaseActivity extends AppCompatActivity {

    //Fonts
    public Typeface regular, bold, semi_bold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        initFonts();
    }

    private void initFonts(){
        regular = ResourcesCompat.getFont(this, R.font.open_sans);
        bold = ResourcesCompat.getFont(this, R.font.open_sans_bold);
        semi_bold = ResourcesCompat.getFont(this, R.font.open_sans_semi_bold);
    }

    //This changes font for all the text views in a view group
    //fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
    public void changeFont(Typeface typeface, ViewGroup viewGroup){
        FontChanger fontChanger = new FontChanger(typeface);
        fontChanger.replaceFonts(viewGroup);
    }

    public void closeKeyboard(EditText view) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        catch (Exception e){
            e.printStackTrace();
            Crashlytics.log(" "+e.getStackTrace());
        }

    }
}
