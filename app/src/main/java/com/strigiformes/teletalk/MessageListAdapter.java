package com.strigiformes.teletalk;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.strigiformes.teletalk.CustomObjects.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    private List<Message> mMessageList;

    //get current user
    private String phone_Number;
    private FirebaseAuth mauth = FirebaseAuth.getInstance();
    private FirebaseUser user = mauth.getCurrentUser();

    public MessageListAdapter(Context context, List<Message> messageList) {

        Log.d("testing3 messageAdapter", "inside constructor");

        mContext = context;
        mMessageList = messageList;

    }

    @Override
    public int getItemCount() {

        Log.d("testing4 messageAdapter", String.valueOf(mMessageList.size()));
        Log.d("testing messageAdapter", mMessageList.toString());

        return mMessageList.size();
    }

    @Override
    public long getItemId(int position){
        Log.d("testing messageAdapter", "inside getItemPosition");
        return position;
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {

        Log.d("testing0 messageAdaper", "inside messageListAdapter");

        Message message =  mMessageList.get(position);
        phone_Number = user.getPhoneNumber();

        if (message.getIdSender().equals(phone_Number)) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        Log.d("testing1 messageAdapter", "inside onCreateViewHolder");

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message =  mMessageList.get(position);

        Log.d("testing2 messageAdaper", "inside onBindViewHolder");

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText =  itemView.findViewById(R.id.text_message_body);
            timeText =  itemView.findViewById(R.id.text_message_time);
        }

        void bind(Message message) {
            messageText.setText(message.getTextMessage());

            // Format the stored timestamp into a readable String using method.
            timeText.setText(new SimpleDateFormat("hh:mm aaa").format(new Date(message.getTimestamp())));

        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText =  itemView.findViewById(R.id.text_message_body);
            timeText =  itemView.findViewById(R.id.text_message_time);
        }

        void bind(Message message) {

            messageText.setText(message.getTextMessage());
            timeText.setText(new SimpleDateFormat("hh:mm aaa").format(new Date(message.getTimestamp())));
        }
    }
}