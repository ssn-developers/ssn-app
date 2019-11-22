package in.edu.ssn.ssnapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
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

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.internal.service.Common;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.hendraanggrian.appcompat.widget.SocialTextView;


import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import in.edu.ssn.ssnapp.PostDetailsActivity;
import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.database.DataBaseHelper;
import in.edu.ssn.ssnapp.models.Comments;
import in.edu.ssn.ssnapp.models.Post;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.SharedPref;

public class SavedPostAdapter extends ArrayAdapter<Post> {
    boolean darkMode=false;
    public SavedPostAdapter(@NonNull Context context, ArrayList<Post> resource) {
        super(context,0,resource);
        darkMode = SharedPref.getBoolean(context,"dark_mode");
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Post model=getItem(position);

        final TextView authorTV, positionTV, titleTV, timeTV, current_imageTV;
        SocialTextView descriptionTV;
        ImageView userImageIV;
        RelativeLayout feed_view;
        ViewPager viewPager;

        if(convertView==null){
            if(darkMode){
                convertView= LayoutInflater.from(getContext()).inflate(R.layout.student_post_item_dark, parent,false);
            }else {
                convertView= LayoutInflater.from(getContext()).inflate(R.layout.student_post_item, parent,false);
            }

        }


        authorTV = convertView.findViewById(R.id.authorTV);
        positionTV = convertView.findViewById(R.id.positionTV);
        titleTV = convertView.findViewById(R.id.titleTV);
        descriptionTV = convertView.findViewById(R.id.descriptionTV);
        timeTV = convertView.findViewById(R.id.timeTV);
        current_imageTV = convertView.findViewById(R.id.currentImageTV);
        userImageIV = convertView.findViewById(R.id.userImageIV);
        feed_view = convertView.findViewById(R.id.feed_view);
        viewPager = convertView.findViewById(R.id.viewPager);

        authorTV.setText(model.getAuthor());

        TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .toUpperCase()
                .endConfig()
                .round();
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(model.getAuthor_image_url());
        TextDrawable ic1 = builder.build(String.valueOf(model.getAuthor().charAt(0)), color);
        userImageIV.setImageDrawable(ic1);

        positionTV.setText(model.getPosition());
        titleTV.setText(model.getTitle());

        timeTV.setText(CommonUtils.getTime(model.getTime()));

        if(model.getDescription().length() > 100) {
            SpannableString ss = new SpannableString(model.getDescription().substring(0, 100) + "... see more");
            ss.setSpan(new RelativeSizeSpan(0.9f), ss.length() - 12, ss.length(), 0);
            ss.setSpan(new ForegroundColorSpan(Color.parseColor("#404040")), ss.length() - 12, ss.length(), 0);
            descriptionTV.setText(ss);
        }
        else
            descriptionTV.setText(model.getDescription().trim());

        if(model.getImageUrl() != null && model.getImageUrl().size() != 0) {
            viewPager.setVisibility(View.VISIBLE);

            int type=Integer.parseInt(DataBaseHelper.getInstance(getContext()).getPostType(model.getId()));

            final ImageAdapter imageAdapter = new ImageAdapter(getContext(), model.getImageUrl(),type, model);
            viewPager.setAdapter(imageAdapter);

            if(model.getImageUrl().size()==1){
                current_imageTV.setVisibility(View.GONE);
            }
            else {
                current_imageTV.setVisibility(View.VISIBLE);
                current_imageTV.setText(String.valueOf(1)+" / "+String.valueOf(model.getImageUrl().size()));
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int pos) {
                        current_imageTV.setText(String.valueOf(pos+1)+" / "+String.valueOf(model.getImageUrl().size()));
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
        }
        else {
            viewPager.setVisibility(View.GONE);
            current_imageTV.setVisibility(View.GONE);
        }

        feed_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PostDetailsActivity.class);
                intent.putExtra("post", model);
                int type=Integer.parseInt(DataBaseHelper.getInstance(getContext()).getPostType(model.getId()));
                intent.putExtra("type",type);
                getContext().startActivity(intent);
            }
        });

        return convertView;
    }
}
