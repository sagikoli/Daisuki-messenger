package com.sagikoli.daisuki;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class groupchatActivity extends AppCompatActivity {

    android.support.v7.widget.Toolbar toolbar;
    ImageButton send;
    ScrollView scrollView;
    EditText inputmsg;
    TextView displaymsg;
    String currentgrpname,currentuid,currentunm,currentdate,currenttime;
    FirebaseAuth auth;
    DatabaseReference reference,grpref,grpmsgkeyref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupchat);



        currentgrpname=getIntent().getExtras().get("groupname").toString();
        reference=FirebaseDatabase.getInstance().getReference("users");
        grpref=FirebaseDatabase.getInstance().getReference("groups").child(currentgrpname);
        auth=FirebaseAuth.getInstance();
        currentuid=auth.getCurrentUser().getUid();

        toolbar=(android.support.v7.widget.Toolbar) findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(currentgrpname);
        send=(ImageButton)findViewById(R.id.group_chat_send_imgbutton);
        inputmsg=(EditText)findViewById(R.id.group_chat_input_edittext);
        displaymsg=(TextView)findViewById(R.id.group_chat_text_disaplay);
        scrollView=(ScrollView)findViewById(R.id.group_chat_scrollview);

        set_text_width();
        Toast.makeText(getApplicationContext(),currentgrpname,Toast.LENGTH_SHORT).show();



        getuserinfo();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmsgtodatabase();

                inputmsg.setText("");
            }
        });
    }

    private void set_text_width() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        inputmsg.setLayoutParams(new LinearLayout.LayoutParams(width-125,ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void sendmsgtodatabase() {
        String msg=inputmsg.getText().toString();
        String msgkey=grpref.push().getKey();
        if (TextUtils.isEmpty(msg))
        {
            Toast.makeText(getApplicationContext(),"enter msg to send",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar calfordate=Calendar.getInstance();
            SimpleDateFormat currentdateformat=new SimpleDateFormat("MMM dd, yyyy");
            currentdate=currentdateformat.format(calfordate.getTime());

            Calendar calfortime=Calendar.getInstance();
            SimpleDateFormat currenttimeformat=new SimpleDateFormat("hh:mm a");
            currenttime=currenttimeformat.format(calfortime.getTime());

            HashMap<String,Object> grp_msg_key=new HashMap<>();
            grpref.updateChildren(grp_msg_key);

            grpmsgkeyref=grpref.child(msgkey);

            HashMap<String,Object> msginfomap=new HashMap<>();
            msginfomap.put("name",currentunm);
            msginfomap.put("message",msg);
            msginfomap.put("date",currentdate);
            msginfomap.put("time",currenttime);

            grpmsgkeyref.updateChildren(msginfomap);
        }
    }

    private void getuserinfo() {
        reference.child(currentuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                    {
                        currentunm=dataSnapshot.child("name").getValue().toString();
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        grpref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists())
                {
                    display_messages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists())
                {
                    display_messages(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        super.onStart();
    }

    private void display_messages(DataSnapshot dataSnapshot) {
        Iterator it=dataSnapshot.getChildren().iterator();

        while (it.hasNext())
        {
            String chat_dt=((DataSnapshot)it.next()).getValue().toString();
            String chat_msg=((DataSnapshot)it.next()).getValue().toString();
            String chat_name=((DataSnapshot)it.next()).getValue().toString();
            String chat_time=((DataSnapshot)it.next()).getValue().toString();

            displaymsg.append(chat_name+":\n"+chat_msg+"\n"+chat_time+"\t"+chat_dt+"\n\n\n");


        }

    }
}
