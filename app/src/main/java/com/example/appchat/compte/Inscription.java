package com.example.appchat.compte;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.appchat.principalAcitivity.MainActivity;
import com.example.appchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class Inscription extends AppCompatActivity {

    private Toolbar toolbar;
    private MaterialEditText email, username, password;
    private Button btn_inscrpt;
    private FirebaseAuth auth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Inscription");
        getSupportActionBar().getThemedContext();
        toolbar.setTitleTextColor(0xFFFFFFFF);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        email = findViewById(R.id.email);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        btn_inscrpt = (Button) findViewById(R.id.btn_inscrpt);
        auth = FirebaseAuth.getInstance();

    }

    public void inscription(View view) {
        if (!email.getText().toString().isEmpty() && !username.getText().toString().isEmpty() && !password.getText().toString().isEmpty() && password.getText().toString().length()>5) {
            auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        assert firebaseUser != null;
                        String usernameId = firebaseUser.getUid();
                        reference = FirebaseDatabase.getInstance().getReference("utilisateurs").child(usernameId);
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("id", usernameId);
                        hashMap.put("username", username.getText().toString());
                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(Inscription.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    Toast.makeText(Inscription.this, "Votre compte a été crée!", Toast.LENGTH_SHORT).show();
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(Inscription.this, "Erreur de l'inscription", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            Toast.makeText(Inscription.this, "Veuillez remplir tout les champs (le mot de passe doit être supérieur à 6 caractères)", Toast.LENGTH_SHORT).show();
        }
    }
}
