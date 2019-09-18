package in.edu.ssn.ssnapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import in.edu.ssn.ssnapp.ClubPostDetailsActivity;
import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.models.ClubPost;
import in.edu.ssn.ssnapp.models.Comments;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.FCMHelper;
import in.edu.ssn.ssnapp.utils.SharedPref;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<Comments> commentArrayList;
    ClubPost clubPost;
    Activity activity;

    Boolean replyingForComment;
    int selectedCommentPosition;


    public void setClubPost(ClubPost clubPost) {
        this.clubPost = clubPost;
    }

    public CustomExpandableListAdapter(Context context, ArrayList<Comments> data, ClubPost clubPost, Activity activity) {
        this.context = context;
        this.clubPost = clubPost;
        for(int i=0;i<data.size();i++)
            Collections.sort(data.get(i).getReply(),new MapComparator("time"));

        this.commentArrayList=data;
        this.activity=activity;
    }

    public void setCommentArrayList(ArrayList<Comments> commentArrayList) {
        this.commentArrayList = commentArrayList;
    }

    public ArrayList<Comments> getCommentArrayList() {
        return commentArrayList;
    }

    public Boolean getReplyingForComment() {
        return replyingForComment;
    }

    public void setReplyingForComment(Boolean replyingForComment) {
        this.replyingForComment = replyingForComment;
    }

    public int getSelectedCommentPosition() {
        return selectedCommentPosition;
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

        final HashMap<String,Object> expandedListText = (HashMap<String, Object>) getChild(listPosition, expandedListPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.custom_reply_item, null);
        }


        TextView replyTime=convertView.findViewById(R.id.tv_time);
        TextView commentListAuthor =  convertView.findViewById(R.id.commentListAuthor);
        TextView commentListDescription =  convertView.findViewById(R.id.commentListDescription);
        ImageView iv_dp=convertView.findViewById(R.id.iv_reply_dp);

        commentListAuthor.setText(expandedListText.get("author").toString());
        commentListDescription.setText(expandedListText.get("message").toString());


        try {
            // timestamp cannot be cast to date sometimes
            Timestamp timestamp = (Timestamp) expandedListText.get("time");
            replyTime.setText(FCMHelper.getTime(timestamp.toDate()));
        }catch (Exception e){
            e.printStackTrace();
        }


        final TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .toUpperCase()
                .endConfig()
                .round();

        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(SharedPref.getString(context,"email"));
        TextDrawable ic=builder.build(String.valueOf(SharedPref.getString(context,"email").charAt(0)), color);
        iv_dp.setImageDrawable(ic);

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

        final Comments comment = (Comments) getGroup(listPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.custom_comment_item, null);
        }

        TextView listTitleTextView = convertView.findViewById(R.id.listTitle);
        TextView listDescription = convertView.findViewById(R.id.listauthor);
        TextView tv_reply=convertView.findViewById(R.id.tv_reply);
        TextView tv_reply_count=convertView.findViewById(R.id.tv_reply_count);
        TextView tv_time=convertView.findViewById(R.id.tv_time);
        ImageView iv_dp=convertView.findViewById(R.id.iv_dp);

        final EditText edt_comment=activity.findViewById(R.id.edt_comment);
        final TextView tv_reply_selected=activity.findViewById(R.id.tv_reply_selected);
        final ImageView iv_cancel_reply=activity.findViewById(R.id.iv_cancel);

        final TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .toUpperCase()
                .endConfig()
                .round();
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(SharedPref.getString(context,"email"));

        TextDrawable ic=builder.build(String.valueOf(SharedPref.getString(context,"email").charAt(0)), color);
        iv_dp.setImageDrawable(ic);

        tv_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                edt_comment.requestFocus();
                InputMethodManager imm = (InputMethodManager) edt_comment.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edt_comment, InputMethodManager.SHOW_IMPLICIT);

                tv_reply_selected.setVisibility(View.VISIBLE);
                iv_cancel_reply.setVisibility(View.VISIBLE);
                tv_reply_selected.setText(comment.getMessage()+" ");


                replyingForComment=true;
                selectedCommentPosition=listPosition;


            }
        });



        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(comment.getAuthor());
        listDescription.setText(comment.getMessage());

        tv_reply_count.setText(commentArrayList.get(listPosition).getReply().size()+" ");
        tv_time.setText(FCMHelper.getTime(commentArrayList.get(listPosition).getTime()));



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
