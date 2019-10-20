package com.strigiformes.teletalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.strigiformes.teletalk.CustomObjects.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SelectGroupContacts extends Activity {

    ListView list;
    CustomListAdapter listviewadapter;
    List<User> appContacts = new ArrayList<User>();
    List<User> group = new ArrayList<User>();

    private FirebaseAuth mauth = FirebaseAuth.getInstance();
    private FirebaseUser user = mauth.getCurrentUser();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group_contacts);

        appContacts = (List<User>) getIntent().getExtras().getSerializable("CONTACTS");
        Log.d("groupContacts", appContacts.toString());

        final User me = new User();
        me.setPhoneNumber(user.getPhoneNumber());
        db.collection("users").document(me.getPhoneNumber()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot senderDoc = task.getResult();
                    me.setName(senderDoc.getData().get("name").toString());
                    group.add(me);
                }
            }
        });

        list = (ListView) findViewById(R.id.listview);
        listviewadapter = new CustomListAdapter(this, R.layout.contact_list_item,
                appContacts);
        list.setAdapter(listviewadapter);

        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        list.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode,
                                                  int position, long id, boolean checked) {
                // Capture total checked items
                final int checkedCount = list.getCheckedItemCount();
                // Set the CAB title according to total checked items
                mode.setTitle(checkedCount + " Selected");
                // Calls toggleSelection method from ListViewAdapter Class
                listviewadapter.toggleSelection(position);
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.done:
                        // Calls getSelectedIds method from ListViewAdapter Class
                        SparseBooleanArray selected = listviewadapter
                                .getSelectedIds();
                        // Captures all selected ids with a loop
                        for (int i = (selected.size() - 1); i >= 0; i--) {
                            if (selected.valueAt(i)) {
                                User selecteditem = listviewadapter
                                        .getItem(selected.keyAt(i));
                                group.add(selecteditem);
                            }
                        }

                        Intent groupNameIntent = new Intent(SelectGroupContacts.this, AddGroupName.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("GROUP_MEMBERS", (Serializable) group);
                        groupNameIntent.putExtras(bundle);
                        startActivity(groupNameIntent);

                        // Close CAB
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.activity_group_select_action, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // TODO Auto-generated method stub
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // TODO Auto-generated method stub
                return false;
            }
    });
    }
}
