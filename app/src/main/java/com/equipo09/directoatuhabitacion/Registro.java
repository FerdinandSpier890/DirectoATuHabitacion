package com.equipo09.directoatuhabitacion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class Registro extends AppCompatActivity {

    EditText txtNombre, txtApellidos, txtCorreo, txtContraseña, txtBiografía, txtEdad, txtTelefono,
            txtDirección, txtDepartamento;
    Button botonRegistrar;

    FirebaseAuth firebaseAuth;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        ActionBar actionBar = getSupportActionBar();
        assert  actionBar != null; //El titulo no es nulo
        actionBar.setTitle("Registro");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        txtNombre = (EditText) findViewById(R.id.txtNombre);
        txtApellidos = (EditText) findViewById(R.id.txtApellido);
        txtCorreo = (EditText) findViewById(R.id.txtCorreo);
        txtContraseña = (EditText) findViewById(R.id.txtContraseña);
        txtBiografía = (EditText) findViewById(R.id.txtBiografía);
        txtEdad = (EditText) findViewById(R.id.txtEdad);
        txtTelefono = (EditText) findViewById(R.id.txtTelefono);
        txtDirección = (EditText) findViewById(R.id.txtDireccion);
        txtDepartamento = (EditText) findViewById(R.id.txtDepartamento);

        botonRegistrar = (Button) findViewById(R.id.buttonRegistrar);

        firebaseAuth = FirebaseAuth.getInstance();

        //Iniciamos el ProgressDialog
        progressDialog = new ProgressDialog(Registro.this);

        botonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String correo = txtCorreo.getText().toString();
                String contraseña = txtContraseña.getText().toString();

                if(!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
                    txtCorreo.setError("Correo no Valido");
                    txtCorreo.setFocusable(true);
                }else if(contraseña.length() < 8){
                    txtContraseña.setError("La contraseña debe de ser mayor a 8 caracteres");
                    txtContraseña.setFocusable(true);
                }else{
                    RegistrarUsuarios(correo, contraseña);
                }
            }
        });
    }


    //Este es el metodo para registrar usuarios
    private void RegistrarUsuarios(String correo, String contraseña) {
        progressDialog.setTitle("Registrando");
        progressDialog.setMessage("Espere Por Favor...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(correo, contraseña).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){ //Si el registro es exitoso
                    FirebaseUser usuario = firebaseAuth.getCurrentUser();
                    progressDialog.dismiss();

                    //Estos son los datos que queremos registrar
                    assert usuario != null;
                    String user = usuario.getUid();
                    String nombre = txtNombre.getText().toString();
                    String apellidos = txtApellidos.getText().toString();
                    String correo = txtCorreo.getText().toString();
                    String contraseña = txtContraseña.getText().toString();
                    String biografia = txtBiografía.getText().toString();
                    String edad = txtEdad.getText().toString();
                    String telefono = txtTelefono.getText().toString();
                    String direccion =txtDirección.getText().toString();
                    String departamento = txtDepartamento.getText().toString();

                    //Creamos un HashMap para mandar los datos a FireBase
                    HashMap<Object, String> datosUsuario = new HashMap<>();
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

                    //Iniciamos la instancia a la base de datos FireBase
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

                    //Creamos la base de datos no relacional con el nombre Usuarios
                    DatabaseReference databaseReference = firebaseDatabase.getReference("Usuarios");

                    databaseReference.child(user).setValue(datosUsuario);
                    Toast.makeText(Registro.this, "El Usuario se Regristro Correctamente", Toast.LENGTH_SHORT).show();

                    //Una vez que se haya  validado los datos, nos redirigira a la pantalla de inicio
                    startActivity(new Intent(getApplicationContext(), Inicio.class));

                }else{
                    progressDialog.dismiss();
                    Toast.makeText(Registro.this, "Error al Registrar al Usuario", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Registro.this,  e.getMessage(), Toast.LENGTH_SHORT).show();
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