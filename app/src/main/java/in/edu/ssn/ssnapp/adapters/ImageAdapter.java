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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import in.edu.ssn.ssnapp.OpenImageActivity;
import in.edu.ssn.ssnapp.PostDetailsActivity;
import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.database.DataBaseHelper;
import in.edu.ssn.ssnapp.fragments.StudentFeedFragment;
import in.edu.ssn.ssnapp.models.Post;
import spencerstudios.com.bungeelib.Bungee;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ImageAdapter extends PagerAdapter {
    Context context;
    List<String> images;
    LayoutInflater layoutInflater;
    Post model;
    String timer;
    Boolean flag;

    public ImageAdapter(Context context, List<String> images, Boolean flag, Post model, String timer) {
        this.context = context;
        this.images = images;
        this.model= model;
        this.timer = timer;
        this.flag = flag;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ImageAdapter(Context context, List<String> images, Boolean flag) {
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
                if(flag) {
                    Intent intent = new Intent(context, PostDetailsActivity.class);
                    intent.putExtra("post", model);
                    intent.putExtra("time", timer);
                    context.startActivity(intent);
                    Bungee.slideUp(context);
                }
                else{
                    Intent intent = new Intent(context, OpenImageActivity.class);
                    intent.putExtra("url", images.get(position));
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    Bungee.slideLeft(context);
                }
            }
        });

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(flag)
                    handleBottomSheet(v,model);
                return true;
            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

    /**********************************************************/

    public void handleBottomSheet(View v,final Post post) {
        RelativeLayout ll_save,ll_share;
        final TextView tv_save;

        final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(context);
        View sheetView=LayoutInflater.from(context).inflate(R.layout.bottom_menu, null);
        bottomSheetDialog.setContentView(sheetView);

        ll_save=sheetView.findViewById(R.id.saveLL);
        ll_share=sheetView.findViewById(R.id.shareLL);
        tv_save=sheetView.findViewById(R.id.tv_save);

        final DataBaseHelper dataBaseHelper=DataBaseHelper.getInstance(context);
        if(dataBaseHelper.checkPost(post.getId()))
            tv_save.setText("Remove from Favourites");
        else
            tv_save.setText("Add to Favourites");

        bottomSheetDialog.show();

        ll_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dataBaseHelper.checkPost(post.getId())){
                    dataBaseHelper.deletePost(post.getId());
                    tv_save.setText("Add to Favourites");
                }
                else{
                    tv_save.setText("Remove from Favourites");
                    dataBaseHelper.addPost(post);
                }
                bottomSheetDialog.hide();
            }
        });

        ll_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hello! New posts from " + post.getAuthor().trim() + ". Check it out: http://ssnportal.cf/share.html?id=" + post.getId();
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });
    }
}