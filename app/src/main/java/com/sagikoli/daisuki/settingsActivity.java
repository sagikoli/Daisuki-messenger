package com.sagikoli.daisuki;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class settingsActivity extends AppCompatActivity {

    Button save;
    EditText unm,status;
    FirebaseAuth auth;
    String current_uid;
    DatabaseReference rootref;
    CircleImageView profile_img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        rootref=FirebaseDatabase.getInstance().getReference();
        auth=FirebaseAuth.getInstance();
        current_uid=auth.getUid();
        save=(Button)findViewById(R.id.setting_save_button);
        unm=(EditText)findViewById(R.id.setting_username_edittext);
        status=(EditText)findViewById(R.id.setting_status_edittext);
        profile_img=(CircleImageView) findViewById(R.id.setting_profile_image);

        retriveuserinfo();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatesettings();
            }
        });




    }

    private void retriveuserinfo() {
        rootref.child("users").child(current_uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.i("checking", "onDataChange start ");
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) && (dataSnapshot.hasChild("image")))
                {
                    String ret_username=dataSnapshot.child("name").getValue().toString();
                    String ret_status=dataSnapshot.child("status").getValue().toString();
                    String ret_img=dataSnapshot.child("img").getValue().toString();

                    Log.i("checking", "onDataChange: "+ret_username+"\t"+ret_status);
                    unm.setText(ret_username);
                    status.setText(ret_status);
                }
                else if (dataSnapshot.exists() && (dataSnapshot.hasChild("name")))
                {

                    String ret_username=dataSnapshot.child("name").getValue().toString();
                    String ret_status=dataSnapshot.child("status").getValue().toString();

                    Log.i("checking", "onDataChange: "+ret_username+"\t"+ret_status);
                    unm.setText(ret_username);
                    status.setText(ret_status);
                }
                else
                {
                    Log.i("checking", "onDataChange: else");
                    Toast.makeText(getApplicationContext(),"plz update your profile",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updatesettings() {
        String username=unm.getText().toString();
        String status_inp=status.getText().toString();
        if(TextUtils.isEmpty(username))
        {
            Toast.makeText(getApplicationContext(),"plz enter username",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(status_inp))
        {
            status_inp="Available...";
        }
        else
        {
            HashMap<String,String> profilemap=new HashMap<>();
            profilemap.put("uid",current_uid);
            profilemap.put("name",username);
            profilemap.put("status",status_inp);
            rootref.child("users").child(current_uid).setValue(profilemap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(getApplicationContext(),"profile updated",Toast.LENGTH_SHORT).show();
                        sendtomainpage();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),task.getException().toString(),Toast.LENGTH_SHORT);
                    }
                }
            });

        }
    }

    private void sendtomainpage() {
        Intent sentomainpage_intent=new Intent(settingsActivity.this,MainActivity.class);
        sentomainpage_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(sentomainpage_intent);
        finish();
    }
}
