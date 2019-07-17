package in.edu.ssn.ssnapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;

import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.models.TeamDetails;
import in.edu.ssn.ssnapp.models.TeamDetails;

public class AboutTeamAdapter extends ArrayAdapter<TeamDetails> implements View.OnClickListener {

    public AboutTeamAdapter(Context context, ArrayList<TeamDetails> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TeamDetails drawer = (TeamDetails) getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.about_team_item, parent, false);

        TextView tv_name = convertView.findViewById(R.id.tv_name);
        TextView tv_position = convertView.findViewById(R.id.tv_position);
        ImageView iv_dp = convertView.findViewById(R.id.iv_dp);

        ImageView iv_img1 = convertView.findViewById(R.id.iv_img1);
        ImageView iv_img2 = convertView.findViewById(R.id.iv_img2);
        ImageView iv_img3 = convertView.findViewById(R.id.iv_img3);

        tv_name.setText(drawer.getName());
        tv_position.setText(drawer.getPosition());
        iv_dp.setImageResource(drawer.getDp());

        iv_img1.setBackgroundResource(drawer.getType().get(0));
        iv_img2.setBackgroundResource(drawer.getType().get(1));
        iv_img3.setBackgroundResource(drawer.getType().get(2));

        iv_img1.setTag(drawer.getUrl().get(0));
        iv_img2.setTag(drawer.getUrl().get(1));
        iv_img3.setTag(drawer.getUrl().get(2));


        iv_img1.setOnClickListener(this);
        iv_img2.setOnClickListener(this);
        iv_img3.setOnClickListener(this);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_img1:
                if (v.getTag() != null)
                    getContext().startActivity(new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", v.getTag().toString(), null)));
                break;
            case R.id.iv_img2:
                if (v.getTag() != null)
                    getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(v.getTag().toString())));
                break;
            case R.id.iv_img3:
                if (v.getTag() != null)
                    getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(v.getTag().toString())));
                break;
        }
    }
}
