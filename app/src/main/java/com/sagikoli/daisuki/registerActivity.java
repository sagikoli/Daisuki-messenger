package com.sagikoli.daisuki;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class registerActivity extends AppCompatActivity {


    FirebaseAuth firebaseAuth;
    Button register;
    TextView haveacc;
    EditText email,pass;
    ProgressDialog loading;
    DatabaseReference root_reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        root_reference=FirebaseDatabase.getInstance().getReference();
        register=(Button)findViewById(R.id.register_button);
        haveacc=(TextView)findViewById(R.id.already_have_acc_txtview);
        email=(EditText)findViewById(R.id.signIp_email_edittext);
        pass=(EditText)findViewById(R.id.signup_password_edittext);
        firebaseAuth=FirebaseAuth.getInstance();
        loading=new ProgressDialog(this);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createnewacc();
            }
        });


        haveacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tologin();
            }
        });
    }

    private void createnewacc() {


        String mail=email.getText().toString();
        String password=pass.getText().toString();
        if(TextUtils.isEmpty(mail) || TextUtils.isEmpty(password))
            Toast.makeText(getApplicationContext(),"Plz enter all feilds",Toast.LENGTH_SHORT).show();
        else {
            loading.setTitle("Creating new account,please wait...");
            loading.setCanceledOnTouchOutside(false);
            loading.show();
            firebaseAuth.createUserWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        String currentuserid=firebaseAuth.getCurrentUser().getUid();
                        root_reference.child("users").child(currentuserid).setValue("");
                        loading.dismiss();
                        tomainpage();
                        Toast.makeText(getApplicationContext(),"account created successfully",Toast.LENGTH_SHORT).show();}
                    else
                    {
                        loading.dismiss();
                    Toast.makeText(getApplicationContext(),task.getException().toString(),Toast.LENGTH_LONG).show();}
                }
            });
        }
    }

    private void tomainpage() {
        Intent tomainpage_activity=new Intent(registerActivity.this,MainActivity.class);
        tomainpage_activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(tomainpage_activity);
    }

    private void tologin() {
        Intent tologin_activity=new Intent(registerActivity.this,loginActivity.class);
        startActivity(tologin_activity);
    }


}
