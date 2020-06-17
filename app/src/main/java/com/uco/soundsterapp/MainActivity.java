package com.uco.soundsterapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.uco.soundsterapp.controller.CatalogoMensajes;
import com.uco.soundsterapp.model.Usuario;

public class MainActivity extends AppCompatActivity {

    private Button btnLogin;
    private TextView txtRegistro;
    private EditText edtEmail, edtPassword;

    private Usuario user = new Usuario();

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        btnLogin = (Button) findViewById(R.id.btn_enterLogin);

        edtEmail = (EditText) findViewById(R.id.loginEdt_email);
        edtPassword = (EditText) findViewById(R.id.loginEdt_password);

        txtRegistro = (TextView) findViewById(R.id.txt_registro);

        txtRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegisterUsersActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.setCorreo(edtEmail.getText().toString());
                user.setContraseña(edtPassword.getText().toString());

                if (!user.getCorreo().isEmpty() && !user.getContraseña().isEmpty()) {
                    iniciarSesion();
                } else {
                    Toast.makeText(MainActivity.this, CatalogoMensajes.COMPLETAR_CAMPOS, Toast.LENGTH_SHORT).show();
                }
            }
        });


        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (!(networkInfo != null && networkInfo.isConnectedOrConnecting())) {
            AlertDialog.Builder alerta = new AlertDialog.Builder(MainActivity.this);
            alerta.setMessage(CatalogoMensajes.NO_HAY_CONEXION_A_INTERNET)
                    .setPositiveButton("De acuerdo", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
            AlertDialog titulo = alerta.create();
            titulo.setTitle(CatalogoMensajes.ERROR_DE_CONEXION);
            titulo.show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(MainActivity.this, ProfileUserActivity.class));
            finish();
        }
    }

    private void iniciarSesion() {
        mAuth.signInWithEmailAndPassword(user.getCorreo(), user.getContraseña()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(MainActivity.this, ProfileUserActivity.class));
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, CatalogoMensajes.ERROR_INICIAR_SESION, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}