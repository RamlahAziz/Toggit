package com.strigiformes.toggit;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.strigiformes.toggit.CustomObjects.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_IMAGE_SENT = 3;
    private static final int VIEW_TYPE_IMAGE_RECEIVED = 4;

    private Context mContext;
    private List<Message> mMessageList;

    //get current user
    private String phone_Number;
    private FirebaseAuth mauth = FirebaseAuth.getInstance();
    private FirebaseUser user = mauth.getCurrentUser();

    public MessageListAdapter(Context context, List<Message> messageList) {
        mContext = context;
        mMessageList = messageList;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {

        Message message =  mMessageList.get(position);
        phone_Number = user.getPhoneNumber();

        if (message.getIdSender().equals(phone_Number)) {
            if(message.getFile()){
                Log.d("MessageListAdapter",String.valueOf(message.getFile()));
                //if message is file and current user is the sender of the message
                return VIEW_TYPE_IMAGE_SENT;
            }
            else{
                // If the message is text current user is the sender of the message
                return VIEW_TYPE_MESSAGE_SENT;
            }

        } else {
            if(message.getFile()){
                //if message is file and some other user sent the message
                return VIEW_TYPE_IMAGE_RECEIVED;
            }
            else{
                // If message is text some other user sent the message
                return VIEW_TYPE_MESSAGE_RECEIVED;
            }

        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        } else if (viewType == VIEW_TYPE_IMAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_image_sent, parent, false);
            return new SentImageHolder(view);
        } else if (viewType == VIEW_TYPE_IMAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_image_received, parent, false);
            return new ReceivedImageHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message =  mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_IMAGE_SENT:
                ((SentImageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_IMAGE_RECEIVED:
                ((ReceivedImageHolder) holder).bind(message);
                break;
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

    private class ReceivedImageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView Image;

        ReceivedImageHolder(View itemView) {
            super(itemView);

            Image = itemView.findViewById(R.id.item_image_received);
            messageText =  itemView.findViewById(R.id.text_message_body);
            timeText =  itemView.findViewById(R.id.text_message_time);
        }

        void bind(Message message) {

            messageText.setText(message.getTextMessage());
            timeText.setText(new SimpleDateFormat("hh:mm aaa").format(new Date(message.getTimestamp())));
            Glide.with(mContext).
                    load(Uri.parse(message.getTextMessage()))
                    .into(Image);
        }
    }

    private class SentImageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView Image;

        SentImageHolder(View itemView) {
            super(itemView);

            Image = itemView.findViewById(R.id.item_image_sent);
            messageText =  itemView.findViewById(R.id.text_message_body);
            timeText =  itemView.findViewById(R.id.text_message_time);
        }

        void bind(Message message) {

            messageText.setText(message.getTextMessage());
            timeText.setText(new SimpleDateFormat("hh:mm aaa").format(new Date(message.getTimestamp())));
            Glide.with(mContext).
                    load(Uri.parse(message.getTextMessage()))
                    .into(Image);
        }
    }

}