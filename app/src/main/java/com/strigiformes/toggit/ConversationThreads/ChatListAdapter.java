package com.strigiformes.toggit.ConversationThreads;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.strigiformes.toggit.CustomObjects.ChatListItem;
import com.strigiformes.toggit.R;

import java.util.List;

public class ChatListAdapter extends ArrayAdapter<ChatListItem> {

    private TextView mName;
    private TextView mPreview;

    public ChatListAdapter(Context context, int resource, List<ChatListItem> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        LayoutInflater myCustomInflater = LayoutInflater.from(getContext());
        View customView = myCustomInflater.inflate(R.layout.chat_home_card, parent, false);

        mName =  customView.findViewById(R.id.name_chat);
        mPreview =  customView.findViewById(R.id.lastMessage);

        mName.setText(getItem(position).getName());
        mPreview.setText(getItem(position).getMsgPreview());

        Long lastSeen = Long.parseLong(getItem(position).getLastSeen());
        Long timeStamp = Long.parseLong(getItem(position).getTimeStamp());
        int compared = lastSeen.compareTo(timeStamp);
        if(compared < 0){
            mPreview.setTypeface(mPreview.getTypeface(), Typeface.BOLD_ITALIC);
        }

        return customView;
    }

}
