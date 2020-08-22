package in.edu.ssn.ssnapp.message_utils;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;
import java.util.List;

import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.utils.SharedPref;

import static in.edu.ssn.ssnapp.message_utils.Utils.differenceBetweenDates;
import static in.edu.ssn.ssnapp.message_utils.Utils.getDate;
import static in.edu.ssn.ssnapp.message_utils.Utils.isYesterday;


public class MessageAdapter extends RecyclerView.Adapter {

    boolean darkMode = false;
    FirebaseUser user;
    private Context mContext;
    private List<Message> messageList;
    private TextDrawable.IBuilder builder;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private OnReplyItemClickListener onReplyItemClickListener;
    public MessageAdapter(Context context,
                          List<Message> messageList,
                          OnItemClickListener onItemClickListener,
                          OnItemLongClickListener onItemLongClickListener,
                          OnReplyItemClickListener onReplyItemClickListener) {
        darkMode = SharedPref.getBoolean(context, "dark_mode");
        mContext = context;
        this.messageList = messageList;
        this.onItemClickListener = onItemClickListener;
        this.onItemLongClickListener = onItemLongClickListener;
        this.onReplyItemClickListener = onReplyItemClickListener;
        user = FirebaseAuth.getInstance().getCurrentUser();
        builder = TextDrawable.builder()
                .beginConfig()
                .toUpperCase()
                .endConfig()
                .round();
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        return message.getType();
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == 0) {
            if (darkMode) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received_dark, parent, false);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            }
            return new ReceivedMessageHolder(view, builder, user);
        }

        if (viewType == 1) {
            if (darkMode) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent_dark, parent, false);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            }
            return new SentMessageHolder(view, builder, user);
        }

        if (viewType == 2) {
            if (darkMode) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received_reply_dark, parent, false);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received_reply, parent, false);
            }
            return new ReceivedReplyHolder(view, builder, user);
        }

        if (viewType == 3) {
            if (darkMode) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent_reply_dark, parent, false);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent_reply, parent, false);
            }
            return new SentReplyHolder(view, builder, user);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final Message message = messageList.get(position);

        if (position - 1 >= 0) {
            Message prevMessage = messageList.get(position - 1);
            long diff = differenceBetweenDates(new Date(Long.valueOf(prevMessage.getTimestamp())), new Date(Long.valueOf(message.getTimestamp())));
            if (diff > 0) {
                System.out.println(diff);
                message.setShowDivider(true);
                if (DateUtils.isToday(Long.valueOf(message.getTimestamp()))) {
                    message.setDividerText("Today");
                } else {
                    if (isYesterday(new Date(Long.valueOf(message.getTimestamp())))) {
                        message.setDividerText("Yesterday");
                    } else {
                        message.setDividerText(getDate(new Date(Long.valueOf(message.getTimestamp()))));
                    }
                }
            }
        } else {
            message.setShowDivider(true);
            if (DateUtils.isToday(Long.valueOf(message.getTimestamp()))) {
                message.setDividerText("Today");
            } else {
                if (isYesterday(new Date(Long.valueOf(message.getTimestamp())))) {
                    message.setDividerText("Yesterday");
                } else {
                    message.setDividerText(getDate(new Date(Long.valueOf(message.getTimestamp()))));
                }
            }

        }
        switch (holder.getItemViewType()) {
            case 0: {
                ((ReceivedMessageHolder) holder).bind(message);
                View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        onItemLongClickListener.onMessageLongClicked(((ReceivedMessageHolder) holder).itemView, holder.getAdapterPosition());
                        return true;
                    }
                };
                View.OnClickListener onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onMessageClicked(((ReceivedMessageHolder) holder).itemView, holder.getAdapterPosition());
                    }
                };
                ((ReceivedMessageHolder) holder).messageTV.setOnLongClickListener(onLongClickListener);
                ((ReceivedMessageHolder) holder).itemView.setOnLongClickListener(onLongClickListener);
                ((ReceivedMessageHolder) holder).messageTV.setOnClickListener(onClickListener);
                ((ReceivedMessageHolder) holder).itemView.setOnClickListener(onClickListener);
                break;
            }
            case 1: {
                ((SentMessageHolder) holder).bind(message);
                View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        onItemLongClickListener.onMessageLongClicked(((SentMessageHolder) holder).itemView, holder.getAdapterPosition());
                        return true;
                    }
                };
                View.OnClickListener onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onMessageClicked(((SentMessageHolder) holder).itemView, holder.getAdapterPosition());
                    }
                };
                ((SentMessageHolder) holder).messageTV.setOnLongClickListener(onLongClickListener);
                ((SentMessageHolder) holder).itemView.setOnLongClickListener(onLongClickListener);
                ((SentMessageHolder) holder).messageTV.setOnClickListener(onClickListener);
                ((SentMessageHolder) holder).itemView.setOnClickListener(onClickListener);
                break;
            }
            case 2: {
                ((ReceivedReplyHolder) holder).bind(message);
                View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        onItemLongClickListener.onMessageLongClicked(((ReceivedReplyHolder) holder).itemView, holder.getAdapterPosition());
                        return true;
                    }
                };
                View.OnClickListener onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onMessageClicked(((ReceivedReplyHolder) holder).itemView, holder.getAdapterPosition());
                    }
                };
                View.OnClickListener onReplyClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onReplyItemClickListener.onReplyMessageClicked(((ReceivedReplyHolder) holder).itemView, holder.getAdapterPosition());
                    }
                };
                ((ReceivedReplyHolder) holder).messageTV.setOnLongClickListener(onLongClickListener);
                ((ReceivedReplyHolder) holder).itemView.setOnLongClickListener(onLongClickListener);
                ((ReceivedReplyHolder) holder).messageTV.setOnClickListener(onClickListener);
                ((ReceivedReplyHolder) holder).itemView.setOnClickListener(onClickListener);
                ((ReceivedReplyHolder) holder).replyLL.setOnClickListener(onReplyClickListener);
                break;
            }
            case 3: {
                ((SentReplyHolder) holder).bind(message);
                View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        onItemLongClickListener.onMessageLongClicked(((SentReplyHolder) holder).itemView, holder.getAdapterPosition());
                        return true;
                    }
                };
                View.OnClickListener onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onMessageClicked(((SentReplyHolder) holder).itemView, holder.getAdapterPosition());
                    }
                };
                View.OnClickListener onReplyClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onReplyItemClickListener.onReplyMessageClicked(((SentReplyHolder) holder).itemView, holder.getAdapterPosition());
                    }
                };
                ((SentReplyHolder) holder).messageTV.setOnLongClickListener(onLongClickListener);
                ((SentReplyHolder) holder).itemView.setOnLongClickListener(onLongClickListener);
                ((SentReplyHolder) holder).messageTV.setOnClickListener(onClickListener);
                ((SentReplyHolder) holder).itemView.setOnClickListener(onClickListener);
                ((SentReplyHolder) holder).replyLL.setOnClickListener(onReplyClickListener);
                break;
            }
        }
    }

    public interface OnItemClickListener {
        void onMessageClicked(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onMessageLongClicked(View view, int position);
    }

    public interface OnReplyItemClickListener {
        void onReplyMessageClicked(View view, int position);
    }

}
