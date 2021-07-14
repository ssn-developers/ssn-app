package in.edu.ssn.ssnapp;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import spencerstudios.com.bungeelib.Bungee;

// Extends Base activity for darkmode variable and status bar.
public class GradeCalculatorActivity extends BaseActivity {

    TextView OTV, APTV, ATV, BPTV, BTV, internalsTV;
    ImageView right, left, back;
    ArrayList<Integer> values = new ArrayList<>();
    int internals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //check if dark mode is enabled and open the appropriate layout.
        if (darkModeEnabled) {
            setContentView(R.layout.activity_grade_calculator_dark);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                getWindow().setStatusBarColor(getResources().getColor(R.color.darkColorLight));
        } else
            setContentView(R.layout.activity_grade_calculator);

        initUI();

        //Increase the internal marks.
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                internals = Integer.parseInt(internalsTV.getText().toString());

                //increase the internal by 1 upto 40.
                if (internals < 40) {
                    internalsTV.setText(Integer.toString(++internals));
                    getValues(internals);
                }
            }
        });

        //Decrease the internal marks.
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                internals = Integer.parseInt(internalsTV.getText().toString());

                //Decrease the internal by 1 upto 0.
                if (internals > 0) {
                    internalsTV.setText(Integer.toString(--internals));
                    getValues(internals);
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    /**********************************************************************/
    // Initiate variables and UI elements.
    private void initUI() {
        OTV = findViewById(R.id.OTV);
        APTV = findViewById(R.id.APTV);
        ATV = findViewById(R.id.ATV);
        BPTV = findViewById(R.id.BPTV);
        BTV = findViewById(R.id.BTV);
        internalsTV = findViewById(R.id.internals_TV);
        right = findViewById(R.id.pm_right_button);
        left = findViewById(R.id.pm_left_button);
        back = findViewById(R.id.backIV);
        getValues(35);
    }
    /**********************************************************************/

    /**********************************************************************/
    // Get min marks for various grades using the internals.
    private void getValues(int mark) {
        values.clear();
        values.add(0, 0);
        values.add(1, 0);
        values.add(2, 0);
        values.add(3, 0);
        values.add(4, 0);

        //Calculate the min marks for grades using internals.
        //Formula:
        //O grade: (0.6 * your mark) + internal should be greater than 91
        //A+ grade: (0.6 * your mark) + internal should be greater than 81
        //...etc
        for (int i = 100; i > 49; i--) {
            int n = (int) (0.6 * i) + (mark);
            if (n >= 91) {
                values.remove(0);
                values.add(0, i);
            } else if (n >= 81) {
                values.remove(1);
                values.add(1, i);
            } else if (n >= 71) {
                values.remove(2);
                values.add(2, i);
            } else if (n >= 61) {
                values.remove(3);
                values.add(3, i);
            } else if (n >= 50) {
                values.remove(4);
                values.add(4, i);
            }
        }

        /*************************************************/
        //If the values is '0', set the Textview as 'N/A'
        //or
        //If the values is not '0', set the Textview as value.

        //O Grade
        if (values.get(0) == 0) {
            OTV.setText("N/A");
        } else {
            OTV.setText(values.get(0).toString());
        }
        //A+ Grade
        if (values.get(1) == 0) {
            APTV.setText("N/A");
        } else {
            APTV.setText(values.get(1).toString());
        }
        //A Grade
        if (values.get(2) == 0) {
            ATV.setText("N/A");
        } else {
            ATV.setText(values.get(2).toString());
        }
        //B+ Grade
        if (values.get(3) == 0) {
            BPTV.setText("N/A");
        } else {
            BPTV.setText(values.get(3).toString());
        }
        //B Grade
        if (values.get(4) == 0) {
            BTV.setText("N/A");
        } else {
            BTV.setText(values.get(4).toString());
        }
        /*************************************************/

    }

    /********************************************************/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bungee.slideRight(GradeCalculatorActivity.this);
        finish();
    }
}