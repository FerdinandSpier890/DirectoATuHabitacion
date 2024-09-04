package com.equipo09.directoatuhabitacion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.equipo09.directoatuhabitacion.Pojos.Galeria;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class CrearPublicaciones extends AppCompatActivity {

    EditText txtDescripcion;
    ImageView fotoPublicacion;
    Button botonSeleccionarFoto, botonPublicar, botonVerPublicacion;

    DatabaseReference databaseReference;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    Uri imageUri;
    FirebaseAuth firebaseAuth;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_publicaciones);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Crear Publicaciones");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();

        txtDescripcion = findViewById(R.id.txtDescripcionPublicacion);
        botonSeleccionarFoto = findViewById(R.id.buttonSeleccionarFoto);
        botonPublicar = findViewById(R.id.buttonPublicar);
        botonVerPublicacion = findViewById(R.id.buttonVerPublicaciones);
        fotoPublicacion = findViewById(R.id.fotoPublicacion);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Publicaciones");
        storageReference = FirebaseStorage.getInstance().getReference().child("fotosPublicaciones");

        progressDialog = new ProgressDialog(this);

        botonSeleccionarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        botonVerPublicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CrearPublicaciones.this, VerPublicaciones.class));
            }
        });

        botonPublicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publicarPublicacion();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar imagen"), PICK_IMAGE_REQUEST);
    }

    private void publicarPublicacion() {
        progressDialog.setTitle("Publicando");
        progressDialog.setMessage("Espere por favor");
        progressDialog.show();

        final FirebaseUser publicacion = firebaseAuth.getCurrentUser();
        assert publicacion != null;
        final String publicacionUid = publicacion.getUid();
        final String descripcion = txtDescripcion.getText().toString().trim();
        final String fechaPublicacion = getCurrentDate();

        if (imageUri != null) {
            final StorageReference imageFilePath = storageReference.child(publicacionUid + "_" + System.currentTimeMillis() + ".jpg");
            imageFilePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        imageFilePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    assert downloadUri != null;
                                    final String fotoPublicacion = downloadUri.toString();

                                    obtenerNombreUsuario(new NombreUsuarioListener() {
                                        @Override
                                        public void onNombreUsuarioObtenido(String nombreUsuarioObtenido) {
                                            String nombreUsuario = nombreUsuarioObtenido;
                                            String fotoPerfilUsuario = publicacion.getPhotoUrl() != null ? publicacion.getPhotoUrl().toString() : "URL_DEFECTO";

                                            Galeria galeria = new Galeria(publicacionUid, descripcion, nombreUsuario, fechaPublicacion, fotoPublicacion, fotoPerfilUsuario);
                                            databaseReference.push().setValue(galeria);

                                            progressDialog.dismiss();
                                            Toast.makeText(CrearPublicaciones.this, "Publicación exitosa", Toast.LENGTH_SHORT).show();

                                            showNotification();

                                            txtDescripcion.setText("");
                                            startActivity(new Intent(CrearPublicaciones.this, VerPublicaciones.class));
                                        }
                                    });
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(CrearPublicaciones.this, "Error al obtener la URL de la imagen", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(CrearPublicaciones.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(CrearPublicaciones.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            progressDialog.dismiss();
            Toast.makeText(CrearPublicaciones.this, "Selecciona una imagen", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                fotoPublicacion.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.habitacion)
                .setContentTitle(firebaseAuth.getCurrentUser().getDisplayName() + " publicó un departamento...")
                .setContentText("Entra y revisa el nuevo departamento. ¡Tal vez te interese!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    interface NombreUsuarioListener {
        void onNombreUsuarioObtenido(String nombreUsuario);
    }

    private void obtenerNombreUsuario(final NombreUsuarioListener listener) {
        FirebaseUser usuarioActual = firebaseAuth.getCurrentUser();
        if (usuarioActual != null) {
            final String uid = usuarioActual.getUid();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios");

            reference.child(uid).child("Nombre").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String nombre = dataSnapshot.getValue(String.class);
                    if (nombre != null) {
                        listener.onNombreUsuarioObtenido(nombre);
                    } else {
                        listener.onNombreUsuarioObtenido("Nombre de usuario por defecto");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
}
