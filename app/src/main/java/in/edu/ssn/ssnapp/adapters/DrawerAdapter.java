package in.edu.ssn.ssnapp.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.models.Drawer;
import in.edu.ssn.ssnapp.utils.SharedPref;

public class DrawerAdapter extends ArrayAdapter<Drawer> {

    Context context;
    boolean darkMode=false;

    public DrawerAdapter(Context context, ArrayList<Drawer> objects) {
        super(context, 0, objects);
        this.context = context;
        darkMode = SharedPref.getBoolean(context,"dark_mode");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Drawer drawer=(Drawer)getItem(position);

        if(convertView==null){
            if(darkMode)
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.drawer_item_dark, parent, false);
            else
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.drawer_item, parent, false);
        }

        final TextView tv_title=convertView.findViewById(R.id.tv_title);
        ImageView iv_icon=convertView.findViewById(R.id.iv_icon);
        View view_line = convertView.findViewById(R.id.view_line);

        tv_title.setText(drawer.getTitle());
        iv_icon.setImageDrawable(context.getDrawable(drawer.getImage()));

        if(position == 0) {
            if(darkMode){
                tv_title.setTextColor(Color.parseColor("#7ABFFF"));
                iv_icon.setImageTintList(context.getResources().getColorStateList(R.color.colorAccentDark));
            }
            else {
                tv_title.setTextColor(Color.parseColor("#317BC0"));
                iv_icon.setImageTintList(context.getResources().getColorStateList(R.color.colorAccent));
            }
        }
        else{
            if(darkMode) {
                tv_title.setTextColor(Color.parseColor("#ffffff"));
                iv_icon.setImageTintList(context.getResources().getColorStateList(R.color.white));
            }
            else {
                tv_title.setTextColor(Color.parseColor("#9A000000"));
                iv_icon.setImageTintList(context.getResources().getColorStateList(R.color.drawer_grey));
            }
        }

        if(position == getCount() - 1)
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
