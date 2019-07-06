package in.edu.ssn.ssnapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

        return convertView;
    }
}
