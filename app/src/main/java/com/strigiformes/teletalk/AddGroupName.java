package com.strigiformes.teletalk;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.strigiformes.teletalk.CustomObjects.User;

import java.util.ArrayList;
import java.util.List;

public class AddGroupName extends Activity {

    ListView list;
    CustomListAdapter listviewadapter;
    List<User> groupList = new ArrayList<User>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_name);

        groupList = (List<User>) getIntent().getExtras().getSerializable("GROUP_MEMBERS");
        Log.d("groupContacts", groupList.toString());

        list = (ListView) findViewById(R.id.group_list);
        listviewadapter = new CustomListAdapter(this, R.layout.contact_list_item,
                groupList);
        list.setAdapter(listviewadapter);
    }
}
