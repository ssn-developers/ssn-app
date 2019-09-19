package in.edu.ssn.ssnapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

import in.edu.ssn.ssnapp.ClubPageActivity;
import in.edu.ssn.ssnapp.ClubPostDetailsActivity;
import in.edu.ssn.ssnapp.OpenImageActivity;
import in.edu.ssn.ssnapp.PostDetailsActivity;
import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.database.DataBaseHelper;
import in.edu.ssn.ssnapp.fragments.StudentFeedFragment;
import in.edu.ssn.ssnapp.models.Club;
import in.edu.ssn.ssnapp.models.ClubPost;
import in.edu.ssn.ssnapp.models.Post;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import spencerstudios.com.bungeelib.Bungee;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ImageAdapter extends PagerAdapter {
    Context context;
    List<String> images;
    LayoutInflater layoutInflater;
    Post model;
    Club c_model;
    String id;
    int flag;

    public ImageAdapter(Context context, List<String> images, int flag, Post model) {
        this.context = context;
        this.images = images;
        this.model= model;
        this.flag = flag;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ImageAdapter(Context context, List<String> images, int flag, Club c_model, String id) {
        this.context = context;
        this.images = images;
        this.c_model= c_model;
        this.id = id;
        this.flag = flag;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ImageAdapter(Context context, List<String> images, int flag) {
        this.context = context;
        this.images = images;
        this.flag = flag;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.viewpager_image_item, container, false);

        ImageView imageView = itemView.findViewById(R.id.imageView);
        container.addView(itemView);

        if(getCount() == 0)
            imageView.setVisibility(View.GONE);
        else
            Glide.with(context).load(images.get(position)).into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag == 0){
                    Intent intent = new Intent(context, OpenImageActivity.class);
                    intent.putExtra("url", images.get(position));
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    try {
                        Bungee.slideLeft(context);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else if(flag == 4){
                    Intent intent = new Intent(context, ClubPostDetailsActivity.class);
                    intent.putExtra("data", id);
                    intent.putExtra("club", c_model);
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    try {
                        Bungee.slideLeft(context);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else {
                    Intent intent = new Intent(context, PostDetailsActivity.class);
                    intent.putExtra("post", model);
                    intent.putExtra("type", flag);
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    try {
                        Bungee.slideLeft(context);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(flag != 0 && flag != 4)
                    CommonUtils.handleBottomSheet(v,model,flag,context);
                return true;
            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}