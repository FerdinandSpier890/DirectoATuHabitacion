package com.equipo09.directoatuhabitacion.opciones;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class MisDatos extends AppCompatActivity {

    EditText textoDatoUID, textoDatoNombre, textoDatoApellidos, textoDatoCorreo, textoDatoContraseña, textoDatoBiografía,
            textoDatoEdad, textoDatoTelefono, textoDatoDireccion, textoDatoInmueble;
    Button botonActualizatDatos, botonActualizarContraseña, botonSeleccionarFoto;

    ImageView fotoPerfil;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    ProgressDialog progressDialog;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_datos);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null; //El titulo no es nulo
        actionBar.setTitle("Mis Datos");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        textoDatoApellidos = findViewById(R.id.txtDatoApellidos);
        textoDatoContraseña = findViewById(R.id.txtDatoContraseña);
        textoDatoCorreo = findViewById(R.id.txtDatoCorreo);
        textoDatoDireccion = findViewById(R.id.txtDatoDireccion);
        textoDatoBiografía = findViewById(R.id.txtDatoBiografía);
        textoDatoEdad = findViewById(R.id.txtDatoEdad);
        textoDatoInmueble = findViewById(R.id.txtDatoInmueble);
        textoDatoNombre = findViewById(R.id.txtDatoNombre);
        textoDatoTelefono = findViewById(R.id.txtDatoTelefono);
        textoDatoUID = findViewById(R.id.txtDatoUID);

        fotoPerfil = findViewById(R.id.datoFotoPerfil);

        botonActualizatDatos = findViewById(R.id.buttonActualizarDatos);
        botonActualizarContraseña = findViewById(R.id.buttonActualizarContraseña);
        botonSeleccionarFoto = findViewById(R.id.buttonSeleccionarFoto);

        //Iniciamos las Instancias
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");
        storageReference = FirebaseStorage.getInstance().getReference("fotosPerfil");
        progressDialog = new ProgressDialog(this);

        //Obtenemos los datos del usuario
        databaseReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Si el usuario existe
                if (snapshot.exists()) {
                    //Obtenemos los datos de Firebase y aparecerán tal cual fueron registrados
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
                    String foto = "" + snapshot.child("FotoPerfil").getValue();

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
                    Picasso.get().load(foto).placeholder(R.drawable.usuario).into(fotoPerfil);
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
                actualizarDatosUsuario();
            }
        });

        botonSeleccionarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarFotoPerfil();
            }
        });
    }

    private void actualizarDatosUsuario() {
        // Crear un HashMap solo para los campos de datos del usuario
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

        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).updateChildren(actualizar)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MisDatos.this, "Los datos se actualizaron correctamente", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MisDatos.this, "Algo salió mal... Inténtalo de nuevo " + e, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void seleccionarFotoPerfil() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar Foto"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            subirFotoPerfil(imageUri);
        }
    }

    private void subirFotoPerfil(Uri imageUri) {
        progressDialog.setTitle("Subiendo Foto");
        progressDialog.setMessage("Espere por favor");
        progressDialog.show();

        StorageReference fileReference = storageReference.child(firebaseUser.getUid() + ".jpg");
        fileReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String downloadUrl = uri.toString();

                                HashMap<String, Object> actualizar = new HashMap<>();
                                actualizar.put("FotoPerfil", downloadUrl);

                                databaseReference.child(firebaseUser.getUid()).updateChildren(actualizar)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Toast.makeText(MisDatos.this, "La foto de perfil se actualizó correctamente", Toast.LENGTH_SHORT).show();
                                                Picasso.get().load(downloadUrl).into(fotoPerfil);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(MisDatos.this, "Error al actualizar la foto de perfil", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MisDatos.this, "Error al subir la foto de perfil", Toast.LENGTH_SHORT).show();
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
