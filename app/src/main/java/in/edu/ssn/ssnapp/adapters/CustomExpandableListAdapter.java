package in.edu.ssn.ssnapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.models.ClubPost;
import in.edu.ssn.ssnapp.models.Comments;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<Comments> commentArrayList;
    ClubPost clubPost;


    public void setClubPost(ClubPost clubPost) {
        this.clubPost = clubPost;
    }

    public CustomExpandableListAdapter(Context context, ArrayList<Comments> data) {
        this.context = context;

        for(int i=0;i<data.size();i++)
            Collections.sort(data.get(i).getReply(),new MapComparator("time"));

        this.commentArrayList=data;
    }

    public ArrayList<Comments> getCommentArrayList() {
        return commentArrayList;
    }

    public void setCommentArrayList(ArrayList<Comments> commentArrayList) {
        this.commentArrayList = commentArrayList;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        //return this.expandableListDetail.get(this.commentArrayList.get(listPosition)).get(expandedListPosition);
        return this.commentArrayList.get(listPosition).getReply().get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final HashMap<String,String> expandedListText = (HashMap<String, String>) getChild(listPosition, expandedListPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.custom_reply_item, null);
        }

        TextView commentListAuthor =  convertView.findViewById(R.id.commentListAuthor);
        TextView commentListDescription =  convertView.findViewById(R.id.commentListDescription);
        commentListAuthor.setText(expandedListText.get("author"));
        commentListDescription.setText(expandedListText.get("message"));

        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.commentArrayList.get(listPosition).getReply().size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.commentArrayList.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.commentArrayList.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(final int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        Comments comment = (Comments) getGroup(listPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.custom_comment_item, null);
        }

        TextView listTitleTextView = convertView.findViewById(R.id.listTitle);
        TextView listDescription = convertView.findViewById(R.id.listauthor);
        TextView tv_reply=convertView.findViewById(R.id.tv_reply);

        final EditText edt_reply=convertView.findViewById(R.id.edt_reply);
        final Button btn_reply=convertView.findViewById(R.id.btn_post_reply);
        final RelativeLayout rl_reply=convertView.findViewById(R.id.rl_reply);

        rl_reply.setVisibility(View.GONE);

        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(comment.getAuthor());
        listDescription.setText(comment.getMessage());


        btn_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final HashMap<String,Object> temp=new HashMap<>();
                temp.put("author","logesh");
                temp.put("message",edt_reply.getText().toString());
                temp.put("time", Calendar.getInstance().getTime());
                commentArrayList.get(listPosition).getReply().add(temp);

                FirebaseFirestore.getInstance().collection("post_club").document("L3xGyQRaz8yRsjqP5Jto").update("comment",commentArrayList).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("Test","success");
                    }
                });

            }
        });

        tv_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rl_reply.setVisibility(View.VISIBLE);
            }
        });

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

    class MapComparator implements Comparator<Map<String, Object>>
    {
        private final String key;

        public MapComparator(String key)
        {
            this.key = key;
        }

        public int compare(Map<String, Object> first, Map<String, Object> second)
        {
            Date firstValue = ((Timestamp)first.get(key)).toDate();
            Date secondValue = ((Timestamp)first.get(key)).toDate();
            if(firstValue.compareTo(secondValue)>0)
                return 1;
            else
                return -1;
        }
    }

}
