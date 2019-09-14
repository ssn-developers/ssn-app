package in.edu.ssn.ssnapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

import in.edu.ssn.ssnapp.OpenImageActivity;
import in.edu.ssn.ssnapp.PostDetailsActivity;
import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.database.DataBaseHelper;
import in.edu.ssn.ssnapp.models.Club;
import spencerstudios.com.bungeelib.Bungee;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ClubImageAdapter extends PagerAdapter {
    Context context;
    List<String> images;
    LayoutInflater layoutInflater;
    Club model;
    String timer;
    Boolean flag;

    public ClubImageAdapter(Context context, List<String> images, Boolean flag, Club model, String timer) {
        this.context = context;
        this.images = images;
        this.model= model;
        this.timer = timer;
        this.flag = flag;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ClubImageAdapter(Context context, List<String> images, Boolean flag) {
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


        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

    /**********************************************************/


}