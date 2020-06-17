package com.uco.soundsterapp.ui.perfil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uco.soundsterapp.R;
import com.uco.soundsterapp.controller.CatalogoMensajes;
import com.uco.soundsterapp.model.Usuario;

import java.util.Objects;

public class PerfilFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Usuario user = new Usuario();

    private Usuario usuario = new Usuario();

    private TextView txtName, txtEmail, txtDescription;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        txtName = (TextView) view.findViewById(R.id.txt_nameUser);
        txtEmail = (TextView) view.findViewById(R.id.txt_emailUser);
        txtDescription = (TextView) view.findViewById(R.id.txt_descripcion);

        txtDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alerta = new AlertDialog.Builder(getContext());
                alerta.setMessage("Descripción");
                final EditText input = new EditText(getContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alerta.setView(input)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {
                                String description = input.getText().toString();
                                user.setDescripcion(description);
                                user.setId(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                                mDatabase.child("Users").child(user.getId()).child("descripcion").setValue(user.getDescripcion()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), CatalogoMensajes.DESCRIPCION_ACTUALIZADA, Toast.LENGTH_SHORT).show();
                                            dialogInterface.cancel();
                                        }
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                AlertDialog titulo = alerta.create();
                titulo.setTitle("Editar");
                titulo.show();
            }
        });


        obtenerDatos();

        return view;

    }

    private void obtenerDatos() {

        usuario.setId(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        mDatabase.child("Users").child(usuario.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("nombre").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();
                    String description = dataSnapshot.child("descripcion").getValue().toString();

                    txtName.setText(name);
                    txtEmail.setText(email);

                    if (!description.isEmpty() ) {
                        txtDescription.setText(description);
                    } else {
                        txtDescription.setText("");
                    }


                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}