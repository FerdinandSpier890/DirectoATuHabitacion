package com.equipo09.directoatuhabitacion.opciones;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.equipo09.directoatuhabitacion.Principal;
import com.equipo09.directoatuhabitacion.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ActualizarContrasenia extends AppCompatActivity {

    TextView misCredenciales, txtCorreoActual, txtContraseñaActual;
    EditText eTContraseñaActual, eTNuevaContraseña;
    Button botonCambiarContraseña;

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_contrasenia);

        ActionBar actionBar = getSupportActionBar();
        assert  actionBar != null; //El titulo no es nulo
        actionBar.setTitle("Actualizar Contraseña");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        misCredenciales = (TextView) findViewById(R.id.misCredenciales);
        txtCorreoActual = (TextView) findViewById(R.id.txtCorreoActual);
        txtContraseñaActual = (TextView) findViewById(R.id.txtContraseñaActual);

        eTContraseñaActual = (EditText) findViewById(R.id.txtActualizarContraseña);
        eTNuevaContraseña = (EditText) findViewById(R.id.txtNuevaContraseña);

        botonCambiarContraseña = (Button) findViewById(R.id.buttonCambiarContraseña);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");

        progressDialog = new ProgressDialog(ActualizarContrasenia.this);

        //Consultamos el correo y la contraseña del usuario
        Query query = databaseReference.orderByChild("Correo").equalTo(firebaseUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                    //Obtenemos los valores
                    String correo = "" + dataSnapshot.child("Correo").getValue();
                    String contraseña = "" + dataSnapshot.child("Contraseña").getValue();

                    //Hacemos uso de los set para los TextView
                    txtCorreoActual.setText(correo);
                    txtContraseñaActual.setText(contraseña);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Creamos el evento para cambiar de contraseña
        botonCambiarContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contraseñaActual = eTContraseñaActual.getText().toString();
                String contraseñaNueva = eTNuevaContraseña.getText().toString();

                if(TextUtils.isEmpty(contraseñaActual)){
                    Toast.makeText(ActualizarContrasenia.this, "El Campo Contraseña " +
                            "Actual Esta Vacio", Toast.LENGTH_SHORT).show();
                }

                if(TextUtils.isEmpty(contraseñaNueva)){
                    Toast.makeText(ActualizarContrasenia.this, "El Campo Contraseña " +
                            "Nueva Esta Vacio", Toast.LENGTH_SHORT).show();
                }

                if(!contraseñaNueva.equals("") && contraseñaNueva.length() > 8){

                    //Se ejecuta el metodo para cambiar la contraseña el cual recibe 2 parametros
                    cambiarContraseña(contraseñaActual, contraseñaNueva);
                }else{
                    eTNuevaContraseña.setError("La contraseña debe ser mayor a 8 caracteres");
                    eTNuevaContraseña.setFocusable(true);
                }
            }
        });

    }

    //Metodo para el cambio de contraseña
    private void cambiarContraseña(String contraseñaActual, String contraseñaNueva) {
        progressDialog.show();
        progressDialog.setTitle("Actualizando");
        progressDialog.setMessage("Espere un momento por favor");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), contraseñaActual);
        firebaseUser.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                firebaseUser.updatePassword(contraseñaNueva).
                        addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        String valor = eTNuevaContraseña.getText().toString().trim();
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("Contraseña", valor);

                        //Actualizamos la contraseña en la base de datos
                        databaseReference.child(firebaseUser.getUid()).updateChildren(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(ActualizarContrasenia.this, "Contraseña" +
                                        " Actualizada Correctamente", Toast.LENGTH_SHORT);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                            }
                        });

                        //Se cerrara sesión y se dirigira a la actividad del login
                        firebaseAuth.signOut();
                        startActivity(new Intent(ActualizarContrasenia.this, Principal.class));
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(ActualizarContrasenia.this, "La contraseña actual " +
                        "no es correcta", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Metodo para retroceder a la actividad anterior
    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return super.onNavigateUp();
    }
}