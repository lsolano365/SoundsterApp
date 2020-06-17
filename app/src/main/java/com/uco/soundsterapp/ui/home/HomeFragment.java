package com.uco.soundsterapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.uco.soundsterapp.R;
import com.uco.soundsterapp.model.Usuario;

import java.util.Objects;

public class HomeFragment extends Fragment {

    private TextView txtName;
    private Usuario user = new Usuario();

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    private static final int MEDIA_GALLERY = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        txtName = (TextView) view.findViewById(R.id.txt_nameProfile);


        obtenerInfo();
        return view;
    }

    private void obtenerInfo() {
        user.setId(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        mDatabase.child("Users").child(user.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("nombre").getValue().toString();

                    txtName.setText(name);
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}