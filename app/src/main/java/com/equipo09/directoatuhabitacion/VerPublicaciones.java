package com.equipo09.directoatuhabitacion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.equipo09.directoatuhabitacion.Adapter.GaleriaAdapter;
import com.equipo09.directoatuhabitacion.Pojos.Galeria;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VerPublicaciones extends AppCompatActivity {

    RecyclerView registrosPublicaciones;
    GaleriaAdapter galeriaAdapter;
    ArrayList<Galeria> galeriaArrayList;
    LinearLayoutManager linearLayoutManager;

    //ActionBar actionBar = getSupportActionBar();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_publicaciones);

        //assert actionBar != null; //El titulo no es nulo
        //actionBar.setTitle("Publicaciones");
        //actionBar.setDisplayShowHomeEnabled(true);
        //actionBar.setDisplayHomeAsUpEnabled(true);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null; //El titulo no es nulo
        actionBar.setTitle("Publicaciones");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        registrosPublicaciones = findViewById(R.id.rvPublicaciones);
        registrosPublicaciones.setLayoutManager(linearLayoutManager);

        galeriaArrayList = new ArrayList<>();
        galeriaAdapter = new GaleriaAdapter(galeriaArrayList, this);
        registrosPublicaciones.setAdapter(galeriaAdapter);

        FirebaseDatabase dataBase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = dataBase.getReference("Publicaciones");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    galeriaArrayList.removeAll(galeriaArrayList);
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Galeria galeria = dataSnapshot.getValue(Galeria.class);
                        galeriaArrayList.add(galeria);
                    }
                    galeriaAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Log.d(TAG, error.getMessage());
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

