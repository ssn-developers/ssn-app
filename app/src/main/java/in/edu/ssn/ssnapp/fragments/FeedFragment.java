package in.edu.ssn.ssnapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import in.edu.ssn.ssnapp.PostDetailsActivity;
import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.models.Post;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.FontChanger;
import io.grpc.internal.JsonParser;

public class FeedFragment extends Fragment {

    public FeedFragment() { }

    RecyclerView feedsRV;
    FirestoreRecyclerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        initFonts(view);
        initUI(view);

        setupFireStore();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    void setupFireStore(){
        final String json = CommonUtils.getData(getContext());
        Query query = FirebaseFirestore.getInstance().collection("post_cse").whereArrayContains("year",2016);

        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, new SnapshotParser<Post>() {
                    @NonNull
                    @Override
                    public Post parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        final Post post = new Post();
                        post.setTitle(snapshot.getString("title"));
                        post.setDescription(snapshot.getString("description"));
                        post.setTime(snapshot.getTimestamp("time").toDate());

                        String id = snapshot.getString("author");
                        try {
                            JSONArray arr = new JSONArray(json);
                            JSONObject obj = arr.getJSONObject(Integer.parseInt(id.substring(4))-1);
                            JSONObject docId = obj.getJSONObject(id);

                            post.setAuthor(docId.getString("name"));
                            post.setAuthor_image_url(docId.getString("dp_url"));
                            post.setPosition(docId.getString("position"));
                        }
                        catch (Exception e){
                            post.setAuthor("No name");
                            post.setAuthor_image_url("");
                            post.setPosition("Dept Incharge");
                            e.printStackTrace();
                        }

                        //TODO images, files upload
                        return post;
                    }
                })
                .build();

        adapter = new FirestoreRecyclerAdapter<Post, FeedViewHolder>(options) {
            @Override
            public void onBindViewHolder(final FeedViewHolder holder, int position, final Post model) {
                holder.tv_author.setText(model.getAuthor());

                if(!model.getAuthor_image_url().equals(""))
                    Picasso.get().load(model.getAuthor_image_url()).placeholder(R.drawable.ic_user_white).into(holder.userImageIV);
                else
                    holder.userImageIV.setImageResource(R.drawable.ic_user_white);

                holder.tv_position.setText(model.getPosition());
                holder.tv_title.setText(model.getTitle());

                Date time = model.getTime();
                Date now = new Date();
                Long t = now.getTime() - time.getTime();

                if(t < 60000)
                    holder.tv_time.setText(Long.toString(t / 1000) + "s ago");
                else if(t < 3600000)
                    holder.tv_time.setText(Long.toString(t / 60000) + "m ago");
                else if(t < 86400000)
                    holder.tv_time.setText(Long.toString(t / 3600000) + "h ago");
                else if(t < 604800000)
                    holder.tv_time.setText(Long.toString(t/86400000) + "d ago");
                else if(t < 2592000000L)
                    holder.tv_time.setText(Long.toString(t/604800000) + "w ago");
                else if(t < 31536000000L)
                    holder.tv_time.setText(Long.toString(t/2592000000L) + "M ago");
                else
                    holder.tv_time.setText(Long.toString(t/31536000000L) + "y ago");

                if(model.getDescription().length() > 100) {
                    SpannableString ss = new SpannableString(model.getDescription().substring(0, 100) + "... see more");
                    ss.setSpan(new RelativeSizeSpan(0.9f), ss.length() - 12, ss.length(), 0);

                    ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(View textView) {
                            holder.tv_description.setText(model.getDescription());
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setUnderlineText(false);
                        }
                    };
                    ss.setSpan(clickableSpan,ss.length() - 12, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ss.setSpan(new ForegroundColorSpan(Color.parseColor("#404040")), ss.length() - 12, ss.length(), 0);
                    holder.tv_description.setText(ss);
                    holder.tv_description.setMovementMethod(LinkMovementMethod.getInstance());

                }
                else
                    holder.tv_description.setText(model.getDescription());

                holder.feed_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), PostDetailsActivity.class);
                        intent.putExtra("post", model);
                        intent.putExtra("time", holder.tv_time.getText().toString());
                        startActivity(intent);
                    }
                });
            }

            @Override
            public FeedViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.post_item, group, false);
                return new FeedViewHolder(view);
            }
        };

        feedsRV.setAdapter(adapter);
    }

    public class FeedViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_author, tv_position, tv_title, tv_description, tv_time;
        public ImageView userImageIV;
        public RelativeLayout feed_view;

        public FeedViewHolder(View itemView) {
            super(itemView);

            tv_author = itemView.findViewById(R.id.tv_author);
            tv_position = itemView.findViewById(R.id.tv_position);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_description = itemView.findViewById(R.id.tv_description);
            tv_time = itemView.findViewById(R.id.tv_time);
            userImageIV = itemView.findViewById(R.id.userImageIV);
            feed_view = itemView.findViewById(R.id.feed_view);

            /*tv_author.setTypeface(regular);
            tv_position.setTypeface(regular);
            tv_title.setTypeface(bold);
            tv_description.setTypeface(regular);
            tv_time.setTypeface(regular);*/
        }
    }

    void initUI(View view){
        feedsRV = view.findViewById(R.id.feedsRV);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        feedsRV.setLayoutManager(layoutManager);
        feedsRV.setHasFixedSize(true);
    }

    //Fonts
    public Typeface regular, bold, italic, bold_italic;
    private void initFonts(View view){
        regular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/product_san_regular.ttf");
        bold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/product_sans_bold.ttf");
        italic = Typeface.createFromAsset(getActivity().getAssets(), "fonts/product_sans_italic.ttf");
        bold_italic = Typeface.createFromAsset(getActivity().getAssets(), "fonts/product_sans_bold_italic.ttf");
        FontChanger fontChanger = new FontChanger(bold);
        //fontChanger.replaceFonts((ViewGroup) view);
    }
}