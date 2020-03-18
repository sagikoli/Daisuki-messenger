package com.sagikoli.daisuki;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    TabAccessorAdapter adapter;
    FirebaseUser user;
    FirebaseAuth auth;
    DatabaseReference rootref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        rootref=FirebaseDatabase.getInstance().getReference();
        toolbar=(Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("DAISUKI");
        viewPager=(ViewPager) findViewById(R.id.main_page_pager);
        adapter=new TabAccessorAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout=(TabLayout)findViewById(R.id.main_page_tablayout);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(user == null)
            sendtologin();
        else
            verify_user();
    }

    private void verify_user() {
        String currentuid=auth.getCurrentUser().getUid();
        rootref.child("users").child(currentuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("name").exists())
                {
                    Toast.makeText(MainActivity.this,"Welcome",Toast.LENGTH_LONG).show();
                }
                else
                {
                    sendtosettings();
                    Toast.makeText(getApplicationContext(),"set your profile plz",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendtologin() {
        Intent sendtologin_intent=new Intent(MainActivity.this,loginActivity.class);
        sendtologin_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(sendtologin_intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_page_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout_item)
        {
            auth.signOut();
            sendtologin();
        }
        if (item.getItemId() == R.id.setting_item)
        {
            sendtosettings();
        }
        if (item.getItemId() == R.id.find_friends_item)
        {

        }
        if (item.getItemId() == R.id.create_group_item)
        {
            createnewgroup();
        }
        return super.onOptionsItemSelected(item);
    }

    private void createnewgroup() {
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle("enter group name");
        final EditText grpname=new EditText(MainActivity.this);
        grpname.setHint("e.g. family");
        builder.setView(grpname);
        builder.setPositiveButton("create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String grpnm=grpname.getText().toString();
                if (TextUtils.isEmpty(grpnm))
                    Toast.makeText(MainActivity.this,"pls enter grp name",Toast.LENGTH_LONG).show();
                else
                {
                    creategrp(grpnm);
                }
            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
            builder.show();
    }

    private void creategrp(final String grpnm) {
        rootref.child("groups").child(grpnm).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Log.i("checking", "onComplete: ");
                    Toast.makeText(getApplicationContext(),grpnm + " group created successfully",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getBaseContext(),"unable",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendtosettings() {
        Intent sendtosettings_intent=new Intent(MainActivity.this,settingsActivity.class);
        sendtosettings_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(sendtosettings_intent);
        finish();
    }
}
