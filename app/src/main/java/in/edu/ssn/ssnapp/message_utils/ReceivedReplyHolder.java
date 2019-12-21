package in.edu.ssn.ssnapp.message_utils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.firebase.auth.FirebaseUser;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.hendraanggrian.appcompat.widget.SocialView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.utils.SharedPref;

import static in.edu.ssn.ssnapp.message_utils.Utils.getDate;
import static in.edu.ssn.ssnapp.message_utils.Utils.isYesterday;

public class ReceivedReplyHolder extends RecyclerView.ViewHolder {
    private final FirebaseUser user;
    public RelativeLayout dividerLL;
    public TextView dividerTV;
    public SocialTextView messageTV;
    public TextView timeTV, nameTV;
    public TextView replyNameTV, replyMessageTV;
    public ImageView avatarIV;
    public CardView avatarCV;
    public LinearLayout replyLL;
    public LinearLayout bgLL;
    public TextView replyTimeTV;
    private final TextDrawable.IBuilder builder;
    public boolean darkMode;

    ReceivedReplyHolder(View itemView, TextDrawable.IBuilder builder, FirebaseUser user) {
        super(itemView);
        darkMode = SharedPref.getBoolean(itemView.getContext(),"dark_mode");
        messageTV = itemView.findViewById(R.id.messageTV);
        timeTV = itemView.findViewById(R.id.timeTV);
        nameTV = itemView.findViewById(R.id.nameTV);
        avatarIV = itemView.findViewById(R.id.avatarIV);
        replyNameTV = itemView.findViewById(R.id.replyNameTV);
        replyMessageTV = itemView.findViewById(R.id.replyMessageTV);
        dividerLL = itemView.findViewById(R.id.dividerLL);
        dividerTV = itemView.findViewById(R.id.dateTV);
        avatarCV = itemView.findViewById(R.id.avatarCV);
        replyLL = itemView.findViewById(R.id.replyLL);
        replyTimeTV = itemView.findViewById(R.id.replyTimeTV);
        bgLL = itemView.findViewById(R.id.bgLL);
        this.builder = builder;
        this.user = user;
    }

    void bind(final Message message) {
        if(message.isMessageDeleted()){
            if(user.getUid().equals(message.getSenderId())){
                messageTV.setText("You deleted this message");
            }else{
                messageTV.setText("This message was deleted");
            }
            if(darkMode){
                messageTV.setCompoundDrawablesWithIntrinsicBounds( R.drawable.chat_ic_slash_white, 0, 0, 0);
            }else{
                messageTV.setCompoundDrawablesWithIntrinsicBounds( R.drawable.chat_ic_slash, 0, 0, 0);
            }
            bgLL.setAlpha(0.7F);
        }else{
            messageTV.setText(message.getMessage());
            messageTV.setCompoundDrawablesWithIntrinsicBounds( 0, 0, 0, 0);
            bgLL.setAlpha(1);
        }
        timeTV.setText(getTime(new Date(Long.valueOf(message.getTimestamp()))));
        nameTV.setText(message.getSenderName());
        if(message.getReplyMessage().getSenderId().equals(user.getUid())){
            replyNameTV.setText("You");
        }else{
            replyNameTV.setText(message.getReplyMessage().getSenderName());
        }
        if(message.getReplyMessage().isMessageDeleted()) {
            if (user.getUid().equals(message.getReplyMessage().getSenderId())) {
                replyMessageTV.setText("You deleted this message");
            } else {
                replyMessageTV.setText("This message was deleted");
            }
        }else {
            replyMessageTV.setText(message.getReplyMessage().getMessage().trim());
        }
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(message.getSenderName());
        String textDrawable=String.valueOf(message.getSenderName().charAt(0));
        TextDrawable ic1 = builder.build(textDrawable, color);
        avatarIV.setImageDrawable(ic1);
        if(message.isShowDivider()){
            dividerLL.setVisibility(View.VISIBLE);
            dividerTV.setText(message.getDividerText());
        }else{
            dividerLL.setVisibility(View.GONE);
        }
        messageTV.setOnHyperlinkClickListener(new SocialView.OnClickListener() {
            @Override
            public void onClick(@NonNull SocialView view, @NonNull CharSequence text) {
                String url = text.toString();
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                messageTV.getContext().startActivity(intent);
            }
        });
        if(message.newMessageBlink){
            message.newMessageBlink=false;
            dividerLL.setVisibility(View.VISIBLE);
            if(message.newMessageCount==1){
                dividerTV.setText("New message");
            }else{
                dividerTV.setText(message.newMessageCount+" New messages");

            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (dividerLL != null) {
                            dividerLL.setVisibility(View.GONE);
                            message.newMessageCount=0;
                        }
                    }catch (Exception e){

                    }
                }
            },4000);
        }
        if(message.isBlinkReply()){
            message.setBlinkReply(false);
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(),R.color.colorAccentAlpha3Chat));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), android.R.color.transparent));
                    }catch (Exception e){}
                }
            },1000);
        }

        if(DateUtils.isToday(Long.valueOf(message.getReplyMessage().getTimestamp()))){
            replyTimeTV.setText("Today, "+getTime(new Date(Long.valueOf(message.getReplyMessage().getTimestamp()))));
        }else{
            if(isYesterday(new Date(Long.valueOf(message.getTimestamp())))){
                replyTimeTV.setText("Yesterday, "+getTime(new Date(Long.valueOf(message.getReplyMessage().getTimestamp()))));
            }else{
                replyTimeTV.setText(getDate(new Date(Long.valueOf(message.getReplyMessage().getTimestamp())))+", "+getTime(new Date(Long.valueOf(message.getReplyMessage().getTimestamp()))));
            }
        }
    }

    public String getTime(Date time){
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
        return dateFormat.format(time);
    }
}
