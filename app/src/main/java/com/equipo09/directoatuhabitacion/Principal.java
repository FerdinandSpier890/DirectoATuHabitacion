package com.equipo09.directoatuhabitacion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Principal extends AppCompatActivity{

    Button botonIniciarSesion, botonRegistrar, botonGoogle;
    EditText txtCorreoLogin, txtContraseñaLogin;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    Dialog dialog;

    private GoogleSignInClient googleSignInClient;
    private final static int SING_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        botonIniciarSesion= (Button) findViewById(R.id.btnIniciarSesion);
        botonRegistrar = (Button) findViewById(R.id.btnRegistrar);
        botonGoogle = (Button) findViewById(R.id.buttonIniciarSesionGoogle);

        txtCorreoLogin = (EditText) findViewById(R.id.txtEmalLogin);
        txtContraseñaLogin = (EditText) findViewById(R.id.txtPasswordLogin);

        ActionBar actionBar = getSupportActionBar();
        assert  actionBar != null; //El tituklo no es nulo
        actionBar.setTitle("Iniciar Sesión");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();

        //Iniciamos el ProgressDialog
        progressDialog = new ProgressDialog(Principal.this);

        //Iniciamos el Dialog
        dialog = new Dialog(Principal.this);

        crearSolicitud();

        botonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pantallaGoogleSignIn();
            }
        });

        botonIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Convertiremos a String el correo y la contraseña
                String correoIniciarSesion = txtCorreoLogin.getText().toString();
                String contraseñaIniciarSesion = txtContraseñaLogin.getText().toString();

                if(!Patterns.EMAIL_ADDRESS.matcher(correoIniciarSesion).matches()){
                    txtCorreoLogin.setError("Correo Invalido.... \nIntenta de Nuevo");
                    txtCorreoLogin.setFocusable(true);
                }else if(contraseñaIniciarSesion.length() < 8){
                    txtContraseñaLogin.setError("La contrseña debe ser mayor a 8 caracteres");
                    txtCorreoLogin.setFocusable(true);
                }else{
                    iniciarSesión(correoIniciarSesion, contraseñaIniciarSesion);
                }

            }
        });

        botonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Registro.class));
            }
        });
    }


    //Metodo para Iniciar Sesion
    private void iniciarSesión(String correoUsuario, String contraseñaUsuario) {
        progressDialog.setTitle("Ingresando");
        progressDialog.setMessage("Espere Por Favor...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(correoUsuario, contraseñaUsuario).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //Si la sesion se inicia correctamente
                if(task.isSuccessful()){
                    progressDialog.dismiss(); //El progreso se cierra

                    FirebaseUser usuarios = firebaseAuth.getCurrentUser();

                    //Cuando  iniciemos sesion nos va a mandar a la pantalla de inicio
                    startActivity(new Intent(Principal.this, Inicio.class));

                    //Afirmamos que el usuario no es nulo y obtenemos su correo electronico
                    assert usuarios != null;
                    Toast.makeText(Principal.this, "Bienvenido(a) " + usuarios.getEmail(), Toast.LENGTH_SHORT).show();
                    finish();

                }else{
                    progressDialog.dismiss();

                    //Nos muestra el mensaje
                    //dialog_okNoInicioSesion();
                    Toast.makeText(Principal.this, "Algo ha salido Mal... Intentalo de Nuevo", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Principal.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Metodo para regresar a la actividad anterior
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    //Creamos la solicitud
    private void crearSolicitud(){

        //Configuramos el inicio de sesion en google
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();

        //Crearemos un GoogleSignClient con las opciones especificadas de googleSignInClient
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

    //Crearemos la pantalla de google
    protected  void pantallaGoogleSignIn(){
        Intent signIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signIntent, SING_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Resultado devuelto al iniciar la intencion de GoogleSignInApp.getSignIntent();
        if(requestCode == SING_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                //Si el inicio de sesion fue exitoso, se autenticará con FireBase
                GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
                AuthenticationFirebase(googleSignInAccount); //Metodo para logueranos con Google
            }catch (ApiException e){
                Toast.makeText(Principal.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Metodo para autenticarnos con FireBase - Google
    private void AuthenticationFirebase(GoogleSignInAccount googleSignInAccount) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        firebaseAuth.signInWithCredential(authCredential).
                addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    //Obtenemos el usuario con el cual se inicia sesión
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                    //Si el usuario inicia sesion por primera vez
                    if(task.getResult().getAdditionalUserInfo().isNewUser()){

                        String uID = firebaseUser.getUid();
                        String nombre = firebaseUser.getDisplayName();
                        String correo = firebaseUser.getEmail();

                        //Parametros de Usuario
                        //Creamos un HashMap para mandar los datos a FireBase
                        HashMap<Object, String> datosUsuario = new HashMap<>();
                        datosUsuario.put("uID", uID);
                        datosUsuario.put("Nombre", nombre);
                        //datosUsuario.put("Apellidos", apellidos);
                        datosUsuario.put("Correo", correo);
                        //datosUsuario.put("contraseña", contraseña);
                        datosUsuario.put("Edad", "");
                        datosUsuario.put("Biografía", "");
                        datosUsuario.put("Teléfono", "");
                        datosUsuario.put("Dirección", "");
                        datosUsuario.put("Departamento", "");
                        datosUsuario.put("Imagen", "");

                        //Iniciamos la instancia a la base de datos FireBase
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

                        //Creamos la base de datos no relacional con el nombre Usuarios
                        DatabaseReference databaseReference = firebaseDatabase.getReference("Usuarios");

                        databaseReference.child(uID).setValue(datosUsuario);
                        Toast.makeText(Principal.this, "El Usuario se Regristro Correctamente", Toast.LENGTH_SHORT).show();
                    }

                    //Nos dirige a la actividad de inicio
                    startActivity( new Intent(Principal.this,  Inicio.class));
                }else{
                    Toast.makeText(Principal.this, "No se pudo Iniciar Sesion, Intente Nuevamente", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}