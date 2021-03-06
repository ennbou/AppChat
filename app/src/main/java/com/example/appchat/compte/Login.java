package com.example.appchat.compte;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appchat.principalAcitivity.MainActivity;
import com.example.appchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.rengwuxian.materialedittext.MaterialEditText;

public class Login extends AppCompatActivity {

    private Toolbar toolbar;
    private MaterialEditText email, password;
    private Button btn_lg;
    private Button btn_enr;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference reference;
    private TextView newpassword;
    private ProgressBar spinner;

    @Override
    protected void onStart() {
        user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            Intent intent=new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("AppChat");
        getSupportActionBar().getThemedContext();
        toolbar.setTitleTextColor(0xFFFFFFFF);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_lg = (Button) findViewById(R.id.btn_lg);
        btn_enr = (Button) findViewById(R.id.btn_enr);
        newpassword = (TextView) findViewById(R.id.resetPass);
        auth = FirebaseAuth.getInstance();
        spinner=(ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        newpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Login.this, newPassword.class);
                startActivity(intent);
            }
        });

        btn_enr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Login.this, Inscription.class);
                startActivity(intent);
            }
        });

    }

    public void login(View view){
        spinner.setVisibility(View.VISIBLE);
        if(!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){
            auth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Intent intent =new Intent(Login.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        password.setText("");
                        Toast.makeText(Login.this, "Votre email ou mot de passe est incorrect", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            password.setText("");
            Toast.makeText(Login.this, "Votre email ou mot de passe est incorrect", Toast.LENGTH_SHORT).show();
        }

    }

}
