package com.example.appchat.message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.appchat.R;
import com.example.appchat.adapter.MessageAdapter;
import com.example.appchat.compte.Login;
import com.example.appchat.modele.Chat;
import com.example.appchat.modele.User;
import com.example.appchat.principalAcitivity.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Messages extends AppCompatActivity {

    private CircleImageView profil_img;
    private TextView username;
    private FirebaseUser FBuser;
    private DatabaseReference reference;
    private Toolbar toolbar;
    private Button send;
    private TextView message;
    private MessageAdapter messageAdapter;
    private List<Chat> messages;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Messages.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //finish();
            }
        });

        recyclerView = findViewById(R.id.recycleview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profil_img = (CircleImageView) findViewById(R.id.profile_image1);
        username = (TextView) findViewById(R.id.username1);

        send = (Button) findViewById(R.id.send);
        message = (TextView) findViewById(R.id.message);

        Intent intent = getIntent();
        final String userid = intent.getStringExtra("id");
        //Toast.makeText(Messages.this, userid, Toast.LENGTH_SHORT).show();

        FBuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("utilisateurs").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());

                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                StorageReference dateRef = storageRef.child("profilImages/" + user.getId() + ".jpeg");
                dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUrl) {
                        Glide.with(Messages.this).load(downloadUrl.toString()).into(profil_img);
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        profil_img.setImageResource(R.drawable.defaultprofile);
                    }
                });

                profil_img.setImageResource(R.drawable.defaultprofile);

                LireMessages(FBuser.getUid(), userid, user.getImageUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void sendMessage(View view) {

        Intent intent = getIntent();
        String userid = intent.getStringExtra("id");
        FBuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();

        if (!message.getText().toString().equals("")) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("emitteur", FBuser.getUid());
            hashMap.put("recepteur", userid);
            hashMap.put("message", message.getText().toString());
            reference.child("Chats").push().setValue(hashMap);
            message.setText("");
        }
    }

    private void LireMessages(final String id, final String userid, final String ImgUrl) {
        messages = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messages.clear();
                for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                    Chat message = dataSnap.getValue(Chat.class);
                    if (message.getRecepteur().equals(id) && message.getEmitteur().equals(userid)
                            || message.getRecepteur().equals(userid) && message.getEmitteur().equals(id)
                    ) {
                        messages.add(message);
                    }
                    messageAdapter = new MessageAdapter(Messages.this, messages, ImgUrl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void etatUser(String etat) {
        reference = FirebaseDatabase.getInstance().getReference("utilisateurs").child(FBuser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("etat", etat);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String etat_enLigne = "En Ligne";
        etatUser(etat_enLigne);
    }

    @Override
    protected void onPause() {
        super.onPause();
        String etat_horsLigne = "Hors Ligne";
        etatUser(etat_horsLigne);
    }

}
