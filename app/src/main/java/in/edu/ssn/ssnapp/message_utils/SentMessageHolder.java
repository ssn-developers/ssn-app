package in.edu.ssn.ssnapp.message_utils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.firebase.auth.FirebaseUser;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.hendraanggrian.appcompat.widget.SocialView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.utils.SharedPref;

public class SentMessageHolder extends RecyclerView.ViewHolder {
    private final FirebaseUser user;
    public RelativeLayout dividerLL;
    public TextView dividerTV;
    public SocialTextView messageTV;
    public TextView timeTV;
    public TextDrawable.IBuilder builder;
    public boolean darkMode;
    SentMessageHolder(View itemView, TextDrawable.IBuilder builder, FirebaseUser user) {
        super(itemView);
        darkMode = SharedPref.getBoolean(itemView.getContext(),"dark_mode");
        messageTV = itemView.findViewById(R.id.messageTV);
        timeTV = itemView.findViewById(R.id.timeTV);
        dividerLL = itemView.findViewById(R.id.dividerLL);
        dividerTV = itemView.findViewById(R.id.dateTV);
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
            messageTV.setCompoundDrawablesWithIntrinsicBounds( R.drawable.chat_ic_slash_white, 0, 0, 0);
            messageTV.setAlpha(0.8F);
        }else{
            messageTV.setText(message.getMessage());
            messageTV.setCompoundDrawablesWithIntrinsicBounds( 0, 0, 0, 0);
            messageTV.setAlpha(1);
        }
        timeTV.setText(getTime(new Date(Long.valueOf(message.getTimestamp()))));
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
    }

    public String getTime(Date time){
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
        return dateFormat.format(time);
    }
}