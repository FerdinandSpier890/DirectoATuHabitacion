package com.equipo09.directoatuhabitacion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.equipo09.directoatuhabitacion.opciones.MisDatos;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class Inicio extends AppCompatActivity {

    Button botonCerrarSesion, botonMisDatos, botonCrearPublicacion,
            botonVerPublicaciones, botonContactar, botonPersonas;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    ImageView fotoPefil;
    TextView textoUID, textoNombreUsuario, textoApellidoUsuario, textoCorreoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Inicio");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        botonCerrarSesion = (Button) findViewById(R.id.buttonCerrarSesion);
        botonMisDatos = (Button) findViewById(R.id.buttonMisDatos);
        botonCrearPublicacion = (Button) findViewById(R.id.buttonCrearPublicacion);
        botonVerPublicaciones = (Button) findViewById(R.id.buttonPublicaciones);
        botonContactar = (Button) findViewById(R.id.buttonContactar);
        botonPersonas = (Button) findViewById(R.id.buttonPersonas);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance(); //Iniciamos la instancia
        databaseReference = firebaseDatabase.getReference("Usuarios");

        fotoPefil = (ImageView) findViewById(R.id.fotoPerfil);

        textoUID = (TextView) findViewById(R.id.txtUID);
        textoNombreUsuario = (TextView) findViewById(R.id.txtNombrePerfil);
        textoCorreoUsuario = (TextView) findViewById(R.id.txtCorreoPerfil);
        textoApellidoUsuario = (TextView) findViewById(R.id.txtApellidoPerfil);

        botonMisDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Inicio.this, MisDatos.class));
            }
        });


        botonCrearPublicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Inicio.this, CrearPublicaciones.class));
            }
        });

        botonVerPublicaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Inicio.this, VerPublicaciones.class));
            }
        });

        botonPersonas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Inicio.this, Personas.class));
            }
        });

        botonContactar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Inicio.this, Contactar.class));
            }
        });

        botonCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cerrarSesion();
            }
        });
    }

    //Hacemos uso del metodo onStart
    @Override
    protected void onStart() {

        //Hacemos uso del metodo verificacionInicioSesion para que se ejecute cuando se inicia la actividad
        verificacionInicioSesion();
        super.onStart();
    }

    //Metodo para verificar si el usuario ha ingresado correctamente
    private void verificacionInicioSesion() {

        //Si el usuario ha iniciado sesion correctamente, lo redirigira aqui mismo
        if (firebaseUser != null) {
            cargarDatos();
            Toast.makeText(Inicio.this, "Se ha iniciado Sesión Correctamente", Toast.LENGTH_SHORT).show();

            //Si no, nos reedigira a la Actividad Principal
        } else {
            startActivity(new Intent(Inicio.this, Principal.class));
            finish();
        }
    }



    //Metodo para cerrar sesion
    protected void cerrarSesion() {
        //Cierra la sesion del usuario
        firebaseAuth.signOut();
        Toast.makeText(Inicio.this, "Ha cerrado sesión ", Toast.LENGTH_SHORT).show();
        //Regresar a la Actividad Principal
        startActivity(new Intent(Inicio.this, Principal.class));
    }

    //Metodo para obtener los datos del usuario actual de FireBase
    private void cargarDatos() {
        Query query = databaseReference.orderByChild("Correo").equalTo(firebaseUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Recorremos los usuarios registrados en la base de datos hasta encontrar el usuario actual
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    //Obtenemos los valores
                    String uid = "" + dataSnapshot.child("uID").getValue();
                    String nombre = "" + dataSnapshot.child("Nombre").getValue();
                    String apellido = "" + dataSnapshot.child("Apellidos").getValue();
                    String correo = "" + dataSnapshot.child("Correo").getValue();
                    String imagen = "" + dataSnapshot.child("Imagen").getValue();

                    //Hacemos uso del set para poner los datos en nuestras vistas
                    textoUID.setText(uid);
                    textoNombreUsuario.setText(nombre);
                    textoApellidoUsuario.setText(apellido);
                    textoCorreoUsuario.setText(correo);

                    //Hacemos uso de un try catch para gestionar nuestra foto de perfil
                    try {

                        //Si el usuario cuenta con una imagen en la base de datos
                        Picasso.get().load(imagen).placeholder(R.drawable.usuario).into(fotoPefil);
                    } catch (Exception e) {

                        //Si el usuario no cuenta con una imagen en la base de datos
                        Picasso.get().load(R.drawable.perfiles);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}