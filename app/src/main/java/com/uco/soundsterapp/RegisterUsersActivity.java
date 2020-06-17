package com.uco.soundsterapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uco.soundsterapp.controller.CatalogoMensajes;
import com.uco.soundsterapp.model.Usuario;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterUsersActivity extends AppCompatActivity {

    private EditText edtName, edtEmail,edtPassword, edtConfirmPass;
    private Button btnRegistrar;

    //Variables a registrar

    private Usuario user = new Usuario();
    private String confirmPassword = "";

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    //Eventos al iniciar el modal de registro, seteo de campos por id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_users);


        if(getSupportActionBar()!= null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        edtName = (EditText) findViewById(R.id.edt_name);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        edtConfirmPass = (EditText) findViewById(R.id.edt_confirmPassword);

        btnRegistrar = (Button) findViewById(R.id.btn_registrar);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Seteo de variables al usuario
                user.setNombreCompleto(edtName.getText().toString());
                user.setCorreo(edtEmail.getText().toString().trim());
                user.setContraseña(edtPassword.getText().toString());
                confirmPassword = edtConfirmPass.getText().toString();

                if (!user.getNombreCompleto().isEmpty() && !user.getCorreo().isEmpty() && !user.getContraseña().isEmpty() && !confirmPassword.isEmpty()) {

                    if (user.getContraseña().length() >= 6){
                        if (confirmPassword.equals(user.getContraseña())) {
                            RegistrarUsuario();
                        } else {
                            edtConfirmPass.setError(CatalogoMensajes.CONTRASEÑAS_NO_COINCIDEN);
                            Toast.makeText(RegisterUsersActivity.this, CatalogoMensajes.CONTRASEÑAS_NO_COINCIDEN, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegisterUsersActivity.this, CatalogoMensajes.CONTRASEÑA_NO_TIENE_LONGITUD_MINIMA, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(RegisterUsersActivity.this, CatalogoMensajes.RELLERNAR_TODOS_LOS_CAMPOS, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    //Función que se encarga de ir a FireBase y registrar el nuevo usuario.
    private void RegistrarUsuario() {

        mAuth.createUserWithEmailAndPassword(user.getCorreo(), user.getContraseña()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    user.setDescripcion("Añade la descripción");

                    Map<String, Object> map = new HashMap<>();
                    map.put("nombre", user.getNombreCompleto());
                    map.put("email", user.getCorreo());
                    map.put("password", user.getContraseña());
                    map.put("descripcion", user.getDescripcion());

                    user.setId(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                    mDatabase.child("Users").child(user.getId()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if (task2.isSuccessful()) {
                                Toast.makeText(RegisterUsersActivity.this, CatalogoMensajes.REGISTRO_EXITOSO, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterUsersActivity.this, ProfileUserActivity.class));
                                finish();
                            } else {
                                task2.getException();
                                Toast.makeText(RegisterUsersActivity.this, CatalogoMensajes.NO_SE_PUDO_REGISTRAR, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(RegisterUsersActivity.this, CatalogoMensajes.USUARIO_YA_SE_ENCUENTRA_REGISTRADO, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}