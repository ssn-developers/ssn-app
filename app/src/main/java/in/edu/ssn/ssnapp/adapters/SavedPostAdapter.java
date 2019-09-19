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

        TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .toUpperCase()
                .endConfig()
                .round();
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(model.getAuthor_image_url());
        TextDrawable ic1 = builder.build(String.valueOf(model.getAuthor().charAt(0)), color);
        userImageIV.setImageDrawable(ic1);

        tv_position.setText(model.getPosition());
        tv_title.setText(model.getTitle());

        tv_time.setText(CommonUtils.getTime(model.getTime()));

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

            int type=Integer.parseInt(DataBaseHelper.getInstance(getContext()).getPostType(model.getId()));

            final ImageAdapter imageAdapter = new ImageAdapter(getContext(), model.getImageUrl(),type, model);
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
                int type=Integer.parseInt(DataBaseHelper.getInstance(getContext()).getPostType(model.getId()));
                intent.putExtra("type",type);
                getContext().startActivity(intent);
            }
        });

        return convertView;
    }
}
