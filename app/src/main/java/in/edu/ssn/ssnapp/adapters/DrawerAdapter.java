package in.edu.ssn.ssnapp.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.models.Drawer;

public class DrawerAdapter extends ArrayAdapter<Drawer> {

    public DrawerAdapter(Context context, ArrayList<Drawer> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Drawer drawer=(Drawer)getItem(position);

        if(convertView==null)
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.drawer_item,parent,false);

        TextView tv_title=convertView.findViewById(R.id.tv_title);
        ImageView iv_icon=convertView.findViewById(R.id.iv_icon);
        View view_line = convertView.findViewById(R.id.view_line);

        tv_title.setText(drawer.getTitle());
        iv_icon.setBackgroundResource(drawer.getImage());

        if(position == 0){
            tv_title.setTextColor(Color.parseColor("#317BC0"));
        }
        else{
            tv_title.setTextColor(Color.parseColor("#9A000000"));
        }

        if(position == getCount()-1)
            view_line.setVisibility(View.INVISIBLE);
        else
            view_line.setVisibility(View.VISIBLE);

        return convertView;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }
}
