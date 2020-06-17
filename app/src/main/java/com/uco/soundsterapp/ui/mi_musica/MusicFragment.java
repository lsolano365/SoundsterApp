package com.uco.soundsterapp.ui.mi_musica;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uco.soundsterapp.R;
import com.uco.soundsterapp.controller.MediaAdapter;
import com.uco.soundsterapp.controller.CatalogoMensajes;
import com.uco.soundsterapp.model.Reproduccion;
import com.uco.soundsterapp.model.Usuario;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class MusicFragment extends Fragment {

    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private DatabaseReference mDatabase, myDatabase;
    private ProgressDialog mProgressDialog;
    private TextView txtToken, txtUri;
    private Usuario users = new Usuario();
    private RecyclerView listaReproduccion;
    private List<Reproduccion> reproduccions, listaMedia;
    private MediaAdapter myMediaAdapter;

    private MediaPlayer mediaPlayer = new MediaPlayer();

    private Button btnUpload, btnReproducir;

    private static int MEDIA_GALLERY = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, container, false);

        btnUpload = (Button) view.findViewById(R.id.btn_uploadMusic);
        btnReproducir = (Button) view.findViewById(R.id.btn_pruebaMusic);
        txtToken = (TextView) view.findViewById(R.id.txt_token);
        txtUri = (TextView) view.findViewById(R.id.txt_uri);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        listaReproduccion = (RecyclerView) view.findViewById(R.id.recycler_view);
        listaReproduccion.setHasFixedSize(true);
        linearLayoutManager.setOrientation(linearLayoutManager.VERTICAL);
        listaReproduccion.setLayoutManager(linearLayoutManager);


        mStorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mProgressDialog = new ProgressDialog(getContext());
        mDatabase = FirebaseDatabase.getInstance().getReference();

        listaMedia = new ArrayList<>();



        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("audio/*");
                startActivityForResult(intent, MEDIA_GALLERY);
            }
        });

        btnReproducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://firebasestorage.googleapis.com/v0/b/soundster-app.appspot.com/o/C7n9gasOtAU4oKDcr35ovOXpYhL2%2FKygo%20-%20Firestone%20(Extended).mp3?alt=media&token=ce1f3c0c-1695-4354-b674-055cf35699f9";
                try {
                    mediaPlayer.setDataSource(url);
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(final MediaPlayer mediaPlayer) {
                            mediaPlayer.start();
                            final AlertDialog.Builder alerta = new AlertDialog.Builder(getContext());
                            alerta.setPositiveButton("pausa", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            boolean validar = false;
                                            mediaPlayer.pause();
                                            if (validar==true){
                                                dialogInterface.dismiss();
                                            }
                                        }
                                    })
                                    .setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            mediaPlayer.stop();
                                            dialogInterface.dismiss();
                                        }
                                    });
                            AlertDialog titulo = alerta.create();
                            titulo.setTitle("Reproducir");
                            titulo.show();
                        }
                    });
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        myDatabase = FirebaseDatabase.getInstance().getReference("Reproduccion");
        myDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Reproduccion reproduccion = postSnapshot.getValue(Reproduccion.class);
                    listaMedia.add(reproduccion);
                }

                myMediaAdapter = new MediaAdapter(getContext(), listaMedia);
                listaReproduccion.setAdapter(myMediaAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MEDIA_GALLERY && resultCode == RESULT_OK) {
            mProgressDialog.setTitle("Subiendo MP3");
            mProgressDialog.setMessage("Subiendo Reproducción a la nube");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            users.setId(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());


            final Uri uri = data.getData();
            StorageReference mediaBase = mStorage.child(users.getId()).child(Objects.requireNonNull(uri.getLastPathSegment()));
            mediaBase.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mProgressDialog.dismiss();

                    reproduccions = new ArrayList<>();
                    Reproduccion reproduccion = new Reproduccion(users.getId(), uri.getLastPathSegment(), taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                    reproduccions.add(reproduccion);

                    String var = mDatabase.push().getKey();
                    mDatabase.child("Reproduccion").child(var).setValue(reproduccion);

                    Toast.makeText(getContext(), CatalogoMensajes.REPRODUCCION_SUBIDA_OK, Toast.LENGTH_SHORT).show();
                }
            });

        }
    }


}