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

public class loginActivity extends AppCompatActivity {

    Button login,phonelogin;
    EditText email,pass;
    TextView forgotpass,newacc;
    FirebaseAuth firebaseAuth;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        login=(Button) findViewById(R.id.login_button);
        phonelogin=(Button) findViewById(R.id.phone_login_button);
        email=(EditText)findViewById(R.id.email_edittextview);
        pass=(EditText)findViewById(R.id.password_edittextview);
        forgotpass=(TextView)findViewById(R.id.forgot_pass_txtview);
        newacc=(TextView)findViewById(R.id.new_account_txtview);
        firebaseAuth=FirebaseAuth.getInstance();

        dialog=new ProgressDialog(this);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trylogin();
            }
        });

        newacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toregister();
            }
        });
    }

    private void trylogin() {
        String mail=email.getText().toString();
        String password=pass.getText().toString();
        if (TextUtils.isEmpty(mail) || TextUtils.isEmpty(password))
            Toast.makeText(getApplicationContext(),"Plz enter all feilds",Toast.LENGTH_SHORT).show();
        else
        {
            dialog.setTitle("pls wait ,signing in");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            firebaseAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        sendtomainpage();
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),"login successful",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),task.getException().toString(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private void toregister() {
        Intent toregister_activity=new Intent(loginActivity.this,registerActivity.class);
        startActivity(toregister_activity);
    }



    private void sendtomainpage() {
        Intent sentomainpage_intent=new Intent(loginActivity.this,MainActivity.class);
        sentomainpage_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(sentomainpage_intent);
        finish();
    }
}
