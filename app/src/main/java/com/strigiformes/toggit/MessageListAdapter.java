package com.strigiformes.toggit;

import android.content.Context;
import android.content.Intent;
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
import com.strigiformes.teletalk.RoundedCornersTransformation;
import com.strigiformes.toggit.CustomObjects.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter {

    int sCorner = 15;
    int sMargin = 20;
    int sBorder = 10;
    String sColor = "#F7CF70";

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_FILE_SENT = 3;
    private static final int VIEW_TYPE_FILE_RECEIVED = 4;

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
                //Log.d("MessageListAdapter",String.valueOf(message.getFile()));
                //if message is file and current user is the sender of the message
                return VIEW_TYPE_FILE_SENT;
            }
            else{
                // If the message is text current user is the sender of the message
                return VIEW_TYPE_MESSAGE_SENT;
            }

        } else {
            if(message.getFile()){
                //if message is file and some other user sent the message
                return VIEW_TYPE_FILE_RECEIVED;
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
        } else if (viewType == VIEW_TYPE_FILE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_image_sent, parent, false);
            return new SentImageHolder(view);
        } else if (viewType == VIEW_TYPE_FILE_RECEIVED) {
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
            case VIEW_TYPE_FILE_SENT:
                ((SentImageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_FILE_RECEIVED:
                ((ReceivedImageHolder) holder).bind(message);
                break;
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        ImageView Image;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText =  itemView.findViewById(R.id.text_message_body);
            timeText =  itemView.findViewById(R.id.text_message_time);
            Image = itemView.findViewById(R.id.item_image_received);
        }

        void bind(Message message) {
            messageText.setText(message.getTextMessage());
            // Format the stored timestamp into a readable String using method.
            timeText.setText(new SimpleDateFormat("hh:mm aaa").format(new Date(message.getTimestamp())));
            if (message.getFileLoction() != null) {
                Glide.with(mContext).
                        load(Uri.parse(message.getFileLoction()))
                        .error(R.drawable.attachment)
                        .apply(RequestOptions.bitmapTransform(
                                new com.strigiformes.teletalk.RoundedCornersTransformation(mContext,  sCorner, sColor, sBorder)))
                        .into(Image);
            }
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        ImageView Image;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText =  itemView.findViewById(R.id.text_message_body);
            timeText =  itemView.findViewById(R.id.text_message_time);
            Image = itemView.findViewById(R.id.item_image_received);
        }

        void bind(Message message) {

            messageText.setText(message.getTextMessage());
            timeText.setText(new SimpleDateFormat("hh:mm aaa").format(new Date(message.getTimestamp())));
            if (message.getFileLoction() != null) {
                Glide.with(mContext).
                        load(Uri.parse(message.getFileLoction()))
                        .error(R.drawable.attachment)
                        .apply(RequestOptions.bitmapTransform(
                                new com.strigiformes.teletalk.RoundedCornersTransformation(mContext,  sCorner, sColor, sBorder)))
                        .into(Image);
            }
        }
    }

    private class ReceivedImageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        ImageView Image;
        String fileType;

        ReceivedImageHolder(View itemView) {
            super(itemView);

            Image = itemView.findViewById(R.id.item_image_received);
            messageText =  itemView.findViewById(R.id.text_message_body);
            timeText =  itemView.findViewById(R.id.text_message_time);
        }

        void bind(final Message message) {

            //https://github.com/thedeveloperworldisyours/RoundedConersWithGlide
            messageText.setText(message.getTextMessage());
            timeText.setText(new SimpleDateFormat("hh:mm aaa").format(new Date(message.getTimestamp())));
            fileType =     (message.getTextMessage()).substring((message.getTextMessage()).length() - 3);

            //Log.d("Filetype",fileType);
            if (!fileType.equals("pdf")) { //file is not a pdf, show image

                Glide.with(mContext).
                    load(Uri.parse(message.getFileLoction()))
                    .error(R.drawable.attachment)
                    .apply(RequestOptions.bitmapTransform(
                            new com.strigiformes.teletalk.RoundedCornersTransformation(mContext,  sCorner, sColor, sBorder)))
                    .into(Image);
            }
            else { //file is a pdf add onclick listener
                Image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(message.getFileLoction()));
                        mContext.startActivity(intent);
                    }
                });
            }


        }
    }

    private class SentImageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        ImageView Image;
        String fileType;

        int sCorner = 15;
        //int sMargin = 20;

        int sBorder = 10;
        String sColor = "#73D88A";

        SentImageHolder(View itemView) {
            super(itemView);

            Image = itemView.findViewById(R.id.item_image_sent);
            messageText =  itemView.findViewById(R.id.text_message_body);
            timeText =  itemView.findViewById(R.id.text_message_time);
        }

        void bind(final Message message) {

            messageText.setText(message.getTextMessage());
            timeText.setText(new SimpleDateFormat("hh:mm aaa").format(new Date(message.getTimestamp())));
            Log.d("ImageUri",message.getFileLoction());
            fileType =     (message.getTextMessage()).substring((message.getTextMessage()).length() - 3);

            //Log.d("ImageUri",message.getFileLoction());
            if(! fileType.equals("pdf")){ //file is not a pdf, show image
                Glide.with(mContext).
                        load(Uri.parse(message.getFileLoction()))
                        .error(R.drawable.attachment)
                        .apply(RequestOptions.bitmapTransform(
                                new RoundedCornersTransformation(mContext,  sCorner, sColor, sBorder)))
                        .into(Image);
            }
            else { //file is a pdf add onclick listener
                Image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(message.getFileLoction()));
                        mContext.startActivity(intent);
                    }
                });

            }

        }
    }

}