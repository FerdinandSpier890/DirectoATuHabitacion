package com.equipo09.directoatuhabitacion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.equipo09.directoatuhabitacion.Pojos.Galeria;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import id.zelory.compressor.Compressor;

public class CrearPublicaciones extends AppCompatActivity {

    EditText txtDescripcion;
    ImageView fotoPublicacion;
    Button botonSeleccionarFoto, botonPublicar, botonVerPublicacion;

    DatabaseReference databaseReference;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    Bitmap bitmapImagen = null;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_publicaciones);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null; //El titulo no es nulo
        actionBar.setTitle("Crear Publicaciones");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();

        txtDescripcion = (EditText) findViewById(R.id.txtDescripcionPublicacion);

        botonSeleccionarFoto = (Button) findViewById(R.id.buttonSeleccionarFoto);
        botonPublicar = (Button) findViewById(R.id.buttonPublicar);
        botonVerPublicacion = (Button) findViewById(R.id.buttonVerPublicaciones);

        fotoPublicacion = (ImageView) findViewById(R.id.fotoPublicacion);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Publicaciones");
        storageReference = FirebaseStorage.getInstance().getReference().child("fotosPublicaciones");

        progressDialog = new ProgressDialog(this);

        botonSeleccionarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.startPickImageActivity(CrearPublicaciones.this);
            }
        });

        botonVerPublicacion.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(CrearPublicaciones.this, VerPublicaciones.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Uri imagenUri = CropImage.getPickImageResultUri(this, data);
            // Recortar Imagen
            CropImage.activity(imagenUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setRequestedSize(1000, 1000)
                    .setAspectRatio(2, 2).start(CrearPublicaciones.this);
        }else {
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){
                Uri uriResult = result.getUri();
                File fileUrl = new File(uriResult.getPath());

                Picasso.get().load(uriResult).into(fotoPublicacion);
                // Compresón de Imagen
                try {
                    bitmapImagen = new Compressor(this)
                            .setMaxWidth(1000)
                            .setMaxHeight(1000)
                            .setQuality(100)
                            .compressToBitmap(fileUrl);
                } catch (IOException e){
                    e.printStackTrace();
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmapImagen.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                final byte[] thumbByte = byteArrayOutputStream.toByteArray();
                // Fin del Compresor

                int p = (int) (Math.random() * 25 + 1);
                int t = (int) (Math.random() * 25 + 1);
                int s = (int) (Math.random() * 25 + 1);
                int c = (int) (Math.random() * 25 + 1);

                int numberOne = (int) (Math.random() * 1012 + 2111);
                int numberTwo = (int) (Math.random() * 1012 + 2111);

                String[] elementos = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o",
                                        "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
                final String aleatorio = elementos[p] + elementos[s] + numberOne + elementos[t] + elementos[c] + numberTwo + ".jpg";

                botonPublicar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressDialog.setTitle("Publicando");
                        progressDialog.setMessage("Espere Por Favor");
                        progressDialog.show();

                        final StorageReference referenciaStorage = storageReference.child(aleatorio);
                        UploadTask uploadTask = referenciaStorage.putBytes(thumbByte);

                        //Publicar Imagen
                        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if(!task.isSuccessful()){
                                    throw Objects.requireNonNull(task.getException());
                                }
                                return referenciaStorage.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if(task.isSuccessful()){
                                    Uri downloadUri = task.getResult();

                                    progressDialog.dismiss();
                                    FirebaseUser publicacion = firebaseAuth.getCurrentUser();

                                    assert publicacion != null;
                                    String publicacionUid = publicacion.getUid();

                                    HashMap<Object, String> datosPublicacion = new HashMap<>();
                                    datosPublicacion.put("uID", publicacionUid);

                                    String descripcion = txtDescripcion.getText().toString();
                                    String usuario = publicacion.getDisplayName();
                                    Galeria galeria = new Galeria(publicacionUid, usuario, descripcion, downloadUri.toString());
                                    databaseReference.child(publicacionUid).setValue(galeria);

                                    Toast.makeText(CrearPublicaciones.this, "Publicación Existosa", Toast.LENGTH_SHORT).show();

                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(CrearPublicaciones.this)
                                            .setSmallIcon(R.drawable.habitacion)
                                            .setContentTitle(usuario + " publicó un departamento...")
                                            .setContentText("Entra y revisa el nuevo departamento, Tal vez te interese")
                                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(CrearPublicaciones.this);
                                    // notificationId is a unique int for each notification that you must define
                                    notificationManager.notify(1, builder.build());
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(CrearPublicaciones.this, "Error al Publicar, Intenta Nuevamente", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();

                                Toast.makeText(CrearPublicaciones.this,  e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }
    }
    //Metodo para regresar a la actividad anterior
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
