package com.equipo09.directoatuhabitacion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Personas extends AppCompatActivity {

    private final List<PersonasModule> listPersonas = new ArrayList<PersonasModule>();
    ArrayAdapter<PersonasModule> arrayAdapterPersona;

    ImageView fotoPerfil;

    TextView txtVerNombrePersona, txtVerApellidoPersona, txtVerTelefonoPersona, txtVerBiografiaPersonas;
    ListView listV_personas;

    LinearLayout layoutDetallesPersona;

    Button botonContactarPersona;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personas);

        txtVerNombrePersona = (TextView) findViewById(R.id.txtVerNombrePersona);
        txtVerApellidoPersona = (TextView) findViewById(R.id.txtVerApellidoPersona);
        txtVerTelefonoPersona = (TextView) findViewById(R.id.txtVerTelefonoPersona);
        txtVerBiografiaPersonas = (TextView) findViewById(R.id.txtVerBiografiaPersona);

        fotoPerfil = findViewById(R.id.imgFotoPerfil);

        layoutDetallesPersona = findViewById(R.id.layoutDetallesPersona);

        listV_personas = (ListView) findViewById(R.id.listViewVerDatosPersonas);

        botonContactarPersona = (Button) findViewById(R.id.buttonContactarPersona);

        databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");

        listarPersonas();
        inicializarFirebase();

        ActionBar actionBar = getSupportActionBar();
        assert  actionBar != null; //El titulo no es nulo
        actionBar.setTitle("Personas Registradas");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        listV_personas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Obtener el usuario seleccionado
                PersonasModule personaSeleccionada = listPersonas.get(position);

                // Mostrar los detalles del usuario seleccionado
                mostrarDetallesPersona(personaSeleccionada);
            }
        });

        botonContactarPersona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Personas.this, Contactar.class));
            }
        });
    }

    private void mostrarDetallesPersona(PersonasModule persona) {
        // Mostrar los detalles del usuario seleccionado en los TextViews
        txtVerNombrePersona.setText(persona.getNombre());
        txtVerApellidoPersona.setText(persona.getApellidos());
        txtVerTelefonoPersona.setText(persona.getTeléfono());
        txtVerBiografiaPersonas.setText(persona.getBiografía());

        // Cargar la foto de perfil utilizando Picasso (con una imagen de placeholder en caso de que no haya foto)
        if (persona.getFotoPerfil() != null && !persona.getFotoPerfil().isEmpty()) {
            Picasso.get().load(persona.getFotoPerfil()).placeholder(R.drawable.usuario).into(fotoPerfil);
        } else {
            // Si no hay foto de perfil, cargar una imagen de placeholder
            fotoPerfil.setImageResource(R.drawable.usuario);
        }

        // Mostrar el layout de detalles del usuario
        layoutDetallesPersona.setVisibility(View.VISIBLE);
    }

    private void listarPersonas(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listPersonas.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    PersonasModule personasModule = dataSnapshot.getValue(PersonasModule.class);

                    listPersonas.add(personasModule);

                }

                arrayAdapterPersona = new ArrayAdapter<PersonasModule>(Personas.this,
                        android.R.layout.simple_list_item_1, listPersonas);
                listV_personas.setAdapter(arrayAdapterPersona);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inicializarFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    //Metodo para regresar a la actividad anterior
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }




}