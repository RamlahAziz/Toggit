package com.strigiformes.teletalk.ConversationThreads;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.strigiformes.teletalk.CustomObjects.ChatListItem;
import com.strigiformes.teletalk.R;

import java.util.List;

public class ChatListAdapter extends ArrayAdapter<ChatListItem> {

    private TextView mFrom;
    private TextView mTo;
    private TextView mName;
    private ImageView mId;
    private TextView mPreview;

    public ChatListAdapter(Context context, int resource, List<ChatListItem> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        Log.d("testing0 ChatAdapter", "inside getview");

        LayoutInflater myCustomInflater = LayoutInflater.from(getContext());
        View customView = myCustomInflater.inflate(R.layout.chat_home_card, parent, false);

        mName =  customView.findViewById(R.id.name_chat);
        mPreview =  customView.findViewById(R.id.lastMessage);

        mName.setText(getItem(position).getName());
        mPreview.setText(getItem(position).getMsgPreview());



        return customView;
    }

}
