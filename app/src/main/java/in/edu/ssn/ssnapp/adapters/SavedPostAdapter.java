package in.edu.ssn.ssnapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import in.edu.ssn.ssnapp.PostDetailsActivity;
import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.database.DataBaseHelper;
import in.edu.ssn.ssnapp.models.Post;

public class SavedPostAdapter extends ArrayAdapter<Post> {

    public SavedPostAdapter(@NonNull Context context, ArrayList<Post> resource) {
        super(context,0,resource);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final Post model=getItem(position);
        final TextView tv_author, tv_position, tv_title, tv_time, tv_current_image;
        SocialTextView tv_description;
        ImageView userImageIV;
        RelativeLayout feed_view;
        ViewPager viewPager;

        if(convertView==null)
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.student_post_item, parent,false);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Testing one", Toast.LENGTH_SHORT).show();
            }
        });

        tv_author = convertView.findViewById(R.id.tv_author);
        tv_position = convertView.findViewById(R.id.tv_position);
        tv_title = convertView.findViewById(R.id.tv_title);
        tv_description = convertView.findViewById(R.id.tv_description);
        tv_time = convertView.findViewById(R.id.tv_time);
        tv_current_image = convertView.findViewById(R.id.currentImageTV);
        userImageIV = convertView.findViewById(R.id.userImageIV);
        feed_view = convertView.findViewById(R.id.feed_view);
        viewPager = convertView.findViewById(R.id.viewPager);

        tv_author.setText(model.getAuthor());

        try {
            if (model.getAuthor_image_url() != null)
                Picasso.get().load(model.getAuthor_image_url()).placeholder(R.drawable.ic_user_white).into(userImageIV);
            else
                userImageIV.setImageResource(R.drawable.ic_user_white);
        }
        catch (Exception e){
            e.printStackTrace();
            userImageIV.setImageResource(R.drawable.ic_user_white);
            Crashlytics.log("stackTrace: "+e.getStackTrace()+" \n Error: "+e.getMessage());
        }

        tv_position.setText(model.getPosition());
        tv_title.setText(model.getTitle());

        Date time = model.getTime();
        //Date time = new Date();
        Date now = new Date();
        Long t = now.getTime() - time.getTime();
        String timer;

        if(t < 60000)
            timer = Long.toString(t / 1000) + "s ago";
        else if(t < 3600000)
            timer = Long.toString(t / 60000) + "m ago";
        else if(t < 86400000)
            timer = Long.toString(t / 3600000) + "h ago";
        else if(t < 604800000)
            timer = Long.toString(t/86400000) + "d ago";
        else if(t < 2592000000L)
            timer = Long.toString(t/604800000) + "w ago";
        else if(t < 31536000000L)
            timer = Long.toString(t/2592000000L) + "M ago";
        else
            timer = Long.toString(t/31536000000L) + "y ago";

        tv_time.setText(timer);

        if(model.getDescription().length() > 100) {
            SpannableString ss = new SpannableString(model.getDescription().substring(0, 100) + "... see more");
            ss.setSpan(new RelativeSizeSpan(0.9f), ss.length() - 12, ss.length(), 0);
            ss.setSpan(new ForegroundColorSpan(Color.parseColor("#404040")), ss.length() - 12, ss.length(), 0);
            tv_description.setText(ss);
        }
        else
            tv_description.setText(model.getDescription().trim());

        if(model.getImageUrl() != null && model.getImageUrl().size() != 0) {
            viewPager.setVisibility(View.VISIBLE);

            final ImageAdapter imageAdapter = new ImageAdapter(getContext(), model.getImageUrl(),true, model, timer);
            viewPager.setAdapter(imageAdapter);

            if(model.getImageUrl().size()==1){
                tv_current_image.setVisibility(View.GONE);
            }
            else {
                tv_current_image.setVisibility(View.VISIBLE);
                tv_current_image.setText(String.valueOf(1)+" / "+String.valueOf(model.getImageUrl().size()));
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int pos) {
                        tv_current_image.setText(String.valueOf(pos+1)+" / "+String.valueOf(model.getImageUrl().size()));
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
        }
        else {
            viewPager.setVisibility(View.GONE);
            tv_current_image.setVisibility(View.GONE);
        }

        feed_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PostDetailsActivity.class);
                intent.putExtra("post", model);
                intent.putExtra("time", tv_time.getText());
                getContext().startActivity(intent);
            }
        });

        return convertView;
    }
}
