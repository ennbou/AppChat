package com.example.appchat.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.appchat.R;
import com.example.appchat.adapter.UserAdapter;
import com.example.appchat.modele.Chat;
import com.example.appchat.modele.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MessageFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<User> users;
    private FirebaseUser fbuser;
    private DatabaseReference reference,reference2;
    private List<String> users_name;
    private UserAdapter userAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_message, container, false);
        recyclerView=(RecyclerView) view.findViewById(R.id.recycleview2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fbuser= FirebaseAuth.getInstance().getCurrentUser();
        users_name=new ArrayList<>();
        reference= FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users_name.clear();

                for(DataSnapshot d: dataSnapshot.getChildren()){
                    Chat message=d.getValue(Chat.class);
                    if(message.getEmitteur().equals(fbuser.getUid()))
                        users_name.add(message.getRecepteur());
                    if(message.getRecepteur().equals(fbuser.getUid()))
                        users_name.add(message.getEmitteur());
                }
               LireMessages();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void LireMessages(){
        users=new ArrayList<>();
        reference2=FirebaseDatabase.getInstance().getReference("utilisateurs");
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                    User user = dataSnapshot1.getValue(User.class);
                    users.clear();
                    for (String id : users_name) {
                        if (user.getId().equals(id)) {
                            if (users.size() != 0) {
                                for (User u : users) {
                                    if (!user.getId().equals(u.getId())) {
                                        users.add(user);
                                    }
                                }
                            } else {
                            users.add(user);
                            }
                        }
                    }
                }
                userAdapter=new UserAdapter(getContext(), users,true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
