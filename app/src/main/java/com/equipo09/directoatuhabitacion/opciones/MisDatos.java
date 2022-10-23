package com.equipo09.directoatuhabitacion.opciones;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.equipo09.directoatuhabitacion.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class MisDatos extends AppCompatActivity {

    EditText textoDatoUID, textoDatoNombre, textoDatoApellidos, textoDatoCorreo, textoDatoContraseña, textoDatoBiografía,
            textoDatoEdad, textoDatoTelefono, textoDatoDireccion, textoDatoInmueble;
    Button botonActualizatDatos, botonActualizarContraseña;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_datos);

        ActionBar actionBar = getSupportActionBar();
        assert  actionBar != null; //El titulo no es nulo
        actionBar.setTitle("Mis Datos");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        textoDatoApellidos = (EditText) findViewById(R.id.txtDatoApellidos);
        textoDatoContraseña = (EditText) findViewById(R.id.txtDatoContraseña);
        textoDatoCorreo = (EditText) findViewById(R.id.txtDatoCorreo);
        textoDatoDireccion = (EditText) findViewById(R.id.txtDatoDireccion);
        textoDatoBiografía = (EditText) findViewById(R.id.txtDatoBiografía);
        textoDatoEdad = (EditText) findViewById(R.id.txtDatoEdad);
        textoDatoInmueble = (EditText) findViewById(R.id.txtDatoInmueble);
        textoDatoNombre = (EditText) findViewById(R.id.txtDatoNombre);
        textoDatoTelefono = (EditText) findViewById(R.id.txtDatoTelefono);
        textoDatoUID = (EditText) findViewById(R.id.txtDatoUID);

        botonActualizatDatos = (Button) findViewById(R.id.buttonActualizarDatos);
        botonActualizarContraseña = (Button) findViewById(R.id.buttonActualizarContraseña);

        //Iniciamos las Instancias
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");
        progressDialog = new ProgressDialog(this);

        //Obtenemos los datos del usuario
        databaseReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Si el usuario existe
                if(snapshot.exists()){

                    //Obtenemos los datos de FireBase y apareceran tal cual fueron registrados
                    String uid = "" + snapshot.child("uID").getValue();
                    String nombre = "" + snapshot.child("Nombre").getValue();
                    String apellidos = "" + snapshot.child("Apellidos").getValue();
                    String correo = "" + snapshot.child("Correo").getValue();
                    String contraseña = "" + snapshot.child("Contraseña").getValue();
                    String biografía = "" + snapshot.child("Biografía").getValue();
                    String edad = "" + snapshot.child("Edad").getValue();
                    String teléfono = "" + snapshot.child("Teléfono").getValue();
                    String direccion = "" + snapshot.child("Dirección").getValue();
                    String inmueble = "" + snapshot.child("Departamento").getValue();


                    //Usamos set para los TextView
                    textoDatoUID.setText(uid);
                    textoDatoNombre.setText(nombre);
                    textoDatoApellidos.setText(apellidos);
                    textoDatoCorreo.setText(correo);
                    textoDatoContraseña.setText(contraseña);
                    textoDatoEdad.setText(edad);
                    textoDatoBiografía.setText(biografía);
                    textoDatoTelefono.setText(teléfono);
                    textoDatoDireccion.setText(direccion);
                    textoDatoInmueble.setText(inmueble);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        botonActualizarContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MisDatos.this, ActualizarContrasenia.class));
            }
        });

        botonActualizatDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> actualizar = new HashMap<>();

                actualizar.put("uID", textoDatoUID.getText().toString());
                actualizar.put("Nombre", textoDatoNombre.getText().toString());
                actualizar.put("Apellidos", textoDatoApellidos.getText().toString());
                actualizar.put("Correo", textoDatoCorreo.getText().toString());
                actualizar.put("Biografía", textoDatoBiografía.getText().toString());
                actualizar.put("Contraseña", textoDatoContraseña.getText().toString());
                actualizar.put("Edad", textoDatoEdad.getText().toString());
                actualizar.put("Teléfono", textoDatoTelefono.getText().toString());
                actualizar.put("Dirección", textoDatoDireccion.getText().toString());
                actualizar.put("Departamento", textoDatoInmueble.getText().toString());

                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

                DatabaseReference databaseReference = firebaseDatabase.getReference("Usuarios");

                databaseReference.child(firebaseAuth.getCurrentUser().getUid()).setValue(actualizar).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MisDatos.this, "Los datos se actualizaron correctamente", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MisDatos.this, "Algo Salio Mal... Intenatalo de Nuevo " + e, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    //Metodo para regresar a la actividad anterior
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}