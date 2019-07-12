package in.edu.ssn.ssnapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.models.Post;
import in.edu.ssn.ssnapp.models.Post;

public class NotifyAdapter extends ArrayAdapter<Post> {

    public NotifyAdapter(Context context, ArrayList<Post> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Post drawer=(Post)getItem(position);

        if(convertView==null)
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.notify_item, parent,false);

        TextView tv_title=convertView.findViewById(R.id.tv_title);
        TextView tv_time=convertView.findViewById(R.id.tv_time);
        CircleImageView iv_dp=convertView.findViewById(R.id.iv_dp);
        ImageView iv_bg=convertView.findViewById(R.id.iv_bg);

        tv_title.setText(drawer.getTitle());
        try {
            Picasso.get().load(drawer.getAuthor_image_url()).placeholder(R.drawable.ic_user_white).into(iv_dp);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        Date time = drawer.getTime();
        Date now = new Date();
        Long t = now.getTime() - time.getTime();
        //Long t = 86400001L;

        if(t < 60000)
            tv_time.setText(Long.toString(t / 1000) + "s ago");
        else if(t < 3600000)
            tv_time.setText(Long.toString(t / 60000) + "m ago");
        else if(t < 86400000)
            tv_time.setText(Long.toString(t / 3600000) + "h ago");
        else if(t < 604800000)
            tv_time.setText(Long.toString(t/86400000) + "d ago");
        else if(t < 2592000000L)
            tv_time.setText(Long.toString(t/604800000) + "w ago");
        else if(t < 31536000000L)
            tv_time.setText(Long.toString(t/2592000000L) + "M ago");
        else
            tv_time.setText(Long.toString(t/31536000000L) + "y ago");

        if(position == getCount()-1)
            iv_bg.setBackgroundResource(R.drawable.notify_item_bg);
        else
            iv_bg.setBackgroundResource(R.drawable.notify_item_bg_bottom);

        if(position > -1)
            tv_title.setTypeface(ResourcesCompat.getFont(getContext(), R.font.open_sans));
        else
            tv_title.setTypeface(ResourcesCompat.getFont(getContext(), R.font.open_sans_bold));

        return convertView;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }
}