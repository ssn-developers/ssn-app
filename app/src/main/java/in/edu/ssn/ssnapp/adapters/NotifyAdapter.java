package in.edu.ssn.ssnapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;


import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import in.edu.ssn.ssnapp.PdfViewerActivity;
import in.edu.ssn.ssnapp.PostDetailsActivity;
import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.models.Post;
import in.edu.ssn.ssnapp.models.Post;
import in.edu.ssn.ssnapp.utils.Constants;
import spencerstudios.com.bungeelib.Bungee;

import static in.edu.ssn.ssnapp.utils.FCMHelper.getTime;

public class NotifyAdapter extends ArrayAdapter<Post> {

    public ArrayList<String> postType;
    public NotifyAdapter(Context context, ArrayList<Post> objects) {
        super(context, 0, objects);
        postType=new ArrayList<>();
    }

    public void setPostType(ArrayList<String> postType)
    {
        this.postType=postType;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Post drawer=(Post)getItem(position);

        if(convertView==null)
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.notify_item, parent,false);

        TextView tv_title=convertView.findViewById(R.id.tv_title);
        TextView tv_time=convertView.findViewById(R.id.tv_time);
        ImageView iv_dp=convertView.findViewById(R.id.iv_dp);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Post drawer=getItem(position);
                if(postType.get(position).equals("2")) {
                    Intent intent = new Intent(getContext(), PdfViewerActivity.class);
                    intent.putExtra(Constants.PDF_URL,drawer.getFileUrl().get(0));
                    getContext().startActivity(intent);
                    Bungee.fade(getContext());
                }
                else{
                    Intent intent = new Intent(getContext(), PostDetailsActivity.class);
                    intent.putExtra("post",drawer);
                    intent.putExtra("time",getTime(drawer.getTime()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(intent);
                    Bungee.slideUp(getContext());
                }
            }
        });

        tv_title.setText(drawer.getTitle());
        try {
            final TextDrawable.IBuilder builder = TextDrawable.builder()
                    .beginConfig()
                    .toUpperCase()
                    .endConfig()
                    .round();
            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(drawer.getAuthor_image_url());
            TextDrawable ic1 = builder.build(String.valueOf(drawer.getAuthor().charAt(0)), color);
            iv_dp.setImageDrawable(ic1);
        }
        catch (Exception e){
            e.printStackTrace();
            if(drawer.getId().equalsIgnoreCase("2")) {
                iv_dp.setImageResource(R.drawable.ic_bus);
            }
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

        return convertView;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }
}