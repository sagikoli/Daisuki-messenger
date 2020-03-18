package com.sagikoli.daisuki;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class groups extends Fragment {

    View grplist;
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> listofgrps=new ArrayList<>();
    DatabaseReference grpref;

    public groups() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        grplist= inflater.inflate(R.layout.fragment_groups, container, false);
        listView=(ListView)grplist.findViewById(R.id.group_list_view);
        arrayAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,listofgrps);
        listView.setAdapter(arrayAdapter);
        grpref=FirebaseDatabase.getInstance().getReference("groups");

        retrivegrps();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currentgrpname=parent.getItemAtPosition(position).toString();
                Intent grpchat_intent=new Intent(getContext(),groupchatActivity.class);
                grpchat_intent.putExtra("groupname",currentgrpname);
                startActivity(grpchat_intent);
            }
        });

        return grplist;
    }

    private void retrivegrps() {
        grpref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator it=dataSnapshot.getChildren().iterator();
                Set<String> set=new HashSet<>();

                while (it.hasNext())
                {
                    set.add(((DataSnapshot)it.next()).getKey());
                }

                listofgrps.clear();
                listofgrps.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
