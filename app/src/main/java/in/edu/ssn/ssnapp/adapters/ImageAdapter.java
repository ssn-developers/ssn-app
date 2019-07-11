package in.edu.ssn.ssnapp.adapters;

import android.content.Context;
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
import android.widget.Toast;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;
import java.util.List;
import in.edu.ssn.ssnapp.R;

public class ImageAdapter extends PagerAdapter {
    Context context;
    List<String> images;
    LayoutInflater layoutInflater;

    public ImageAdapter(Context context, List<String> images) {
        this.context = context;
        this.images = images;
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
            Picasso.get().load(images.get(position)).into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO navigate to post detail activity
            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}