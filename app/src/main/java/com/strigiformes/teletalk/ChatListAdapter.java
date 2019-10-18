package com.strigiformes.teletalk;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

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
        //mId =  customView.findViewById(R.id.pictureChat);
        mPreview =  customView.findViewById(R.id.lastMessage);

        mName.setText(getItem(position).getName());
        mPreview.setText(getItem(position).getMsgPreview());

        /*if (getItem(position).getPictureUri()!= null) {

            Log.d("testing ChatAdapter", "inside if");
            //Glide.with(getContext()).load(Uri.parse(getItem(position).getPictureUri())).apply(RequestOptions.circleCropTransform().placeholder(R.drawable.ic_avatar)).into(mId);

        }*/

        return customView;
    }

}
