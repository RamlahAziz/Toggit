package com.strigiformes.teletalk;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.strigiformes.teletalk.CustomObjects.User;
import java.util.List;

public class CustomListAdapter extends ArrayAdapter<User> {

    private List<User> list;
    private TextView name;

    public CustomListAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
        this.list = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        Log.d("getview", list.toString());
        if (convertView == null) {
            LayoutInflater myCustomInflater = LayoutInflater.from(getContext());
            convertView = myCustomInflater.inflate(R.layout.contact_list_item, parent, false);
        }

        name = convertView.findViewById(R.id.contactName);
        name.setText(getItem(position).getName());

        return convertView;
    }

}
