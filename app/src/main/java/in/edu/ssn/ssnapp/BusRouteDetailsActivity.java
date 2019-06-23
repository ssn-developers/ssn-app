package in.edu.ssn.ssnapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

public class BusRouteDetailsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_route_details);

        initUI();

    }

    void initUI(){
        changeFont(bold,(ViewGroup)this.findViewById(android.R.id.content));
    }

}
