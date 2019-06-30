package in.edu.ssn.ssnapp;

import android.graphics.Typeface;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

import in.edu.ssn.ssnapp.utils.FontChanger;

public class BaseActivity extends AppCompatActivity {

    //Fonts
    public Typeface regular, bold, italic, bold_italic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        initFonts();

    }

    private void initFonts(){
        regular = Typeface.createFromAsset(getAssets(), "fonts/product_san_regular.ttf");
        bold = Typeface.createFromAsset(getAssets(), "fonts/product_sans_bold.ttf");
        italic = Typeface.createFromAsset(getAssets(), "fonts/product_sans_italic.ttf");
        bold_italic = Typeface.createFromAsset(getAssets(), "fonts/product_sans_bold_italic.ttf");
    }

    //This changes font for all the text views in a view group
    //fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
    public void changeFont(Typeface typeface, ViewGroup viewGroup){
        FontChanger fontChanger = new FontChanger(typeface);
        fontChanger.replaceFonts(viewGroup);
    }
}
