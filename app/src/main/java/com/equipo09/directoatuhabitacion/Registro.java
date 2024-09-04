package com.equipo09.directoatuhabitacion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.equipo09.directoatuhabitacion.opciones.MisDatos;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;

public class Registro extends AppCompatActivity {

    EditText txtNombre, txtApellidos, txtCorreo, txtContraseña, txtBiografía, txtEdad, txtTelefono,
            txtDirección, txtDepartamento;
    Button botonRegistrar;
    ImageView fotoPerfilUsuario;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;

    private Uri imageUri;
    private static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null; // El titulo no es nulo
        actionBar.setTitle("Registro");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        txtNombre = findViewById(R.id.txtNombre);
        txtApellidos = findViewById(R.id.txtApellido);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtContraseña = findViewById(R.id.txtContraseña);
        txtBiografía = findViewById(R.id.txtBiografía);
        txtEdad = findViewById(R.id.txtEdad);
        txtTelefono = findViewById(R.id.txtTelefono);
        txtDirección = findViewById(R.id.txtDireccion);
        txtDepartamento = findViewById(R.id.txtDepartamento);
        fotoPerfilUsuario = findViewById(R.id.fotoPerfilUsuario);

        botonRegistrar = findViewById(R.id.buttonRegistrar);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");

        progressDialog = new ProgressDialog(Registro.this);
        progressDialog.setTitle("Registrando");
        progressDialog.setMessage("Espere Por Favor...");
        progressDialog.setCancelable(false);

        fotoPerfilUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(Registro.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Registro.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    pickImageFromGallery();
                }
            }
        });

        botonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String correo = txtCorreo.getText().toString();
                String contraseña = txtContraseña.getText().toString();

                if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                    txtCorreo.setError("Correo no válido");
                    txtCorreo.setFocusable(true);
                } else if (contraseña.length() < 8) {
                    txtContraseña.setError("La contraseña debe tener al menos 8caracteres");
                    txtContraseña.setFocusable(true);
                } else {
                    RegistrarUsuarios(correo, contraseña);
                }
            }
        });
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImageFromGallery();
        } else {
            Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                fotoPerfilUsuario.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void RegistrarUsuarios(String correo, String contraseña) {
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(correo, contraseña).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser usuario = firebaseAuth.getCurrentUser();
                    progressDialog.dismiss();

                    if (usuario != null) {
                        String user = usuario.getUid();
                        String nombre = txtNombre.getText().toString();
                        String apellidos = txtApellidos.getText().toString();
                        String correo = txtCorreo.getText().toString();
                        String contraseña = txtContraseña.getText().toString();
                        String biografia = txtBiografía.getText().toString();
                        String edad = txtEdad.getText().toString();
                        String telefono = txtTelefono.getText().toString();
                        String direccion = txtDirección.getText().toString();
                        String departamento = txtDepartamento.getText().toString();

                        HashMap<String, Object> datosUsuario = new HashMap<>();
                        datosUsuario.put("uID", user);
                        datosUsuario.put("Nombre", nombre);
                        datosUsuario.put("Apellidos", apellidos);
                        datosUsuario.put("Correo", correo);
                        datosUsuario.put("Contraseña", contraseña);
                        datosUsuario.put("Biografía", biografia);
                        datosUsuario.put("Edad", edad);
                        datosUsuario.put("Teléfono", telefono);
                        datosUsuario.put("Dirección", direccion);
                        datosUsuario.put("Departamento", departamento);

                        if (imageUri != null) {
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("fotos_perfil").child(user + ".jpg");
                            storageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uriTask.isSuccessful()) ;
                                Uri downloadUri = uriTask.getResult();
                                if (downloadUri != null) {
                                    datosUsuario.put("FotoPerfil", downloadUri.toString());
                                    databaseReference.child(user).setValue(datosUsuario);
                                }
                            }).addOnFailureListener(e -> {
                                Toast.makeText(Registro.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                                databaseReference.child(user).setValue(datosUsuario);
                            });
                        } else {
                            databaseReference.child(user).setValue(datosUsuario);
                        }

                        Toast.makeText(Registro.this, "El Usuario se Registró Correctamente", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), Inicio.class));
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(Registro.this, "Error al Registrar al Usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Registro.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
