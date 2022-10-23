package com.equipo09.directoatuhabitacion;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Contactar extends AppCompatActivity {

    EditText txtMensaje, txtNumeroWhatsApp;
    Button botonMandarMensajeWhatsApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactar);

        txtMensaje = (EditText) findViewById(R.id.txtMensaje);
        txtNumeroWhatsApp = (EditText) findViewById(R.id.txtNumeroWhatsApp);

        botonMandarMensajeWhatsApp = (Button) findViewById(R.id.buttonMandarMensajeWhatsApp);

        ActionBar actionBar = getSupportActionBar();
        assert  actionBar != null; //El titulo no es nulo
        actionBar.setTitle("Contactar Personas");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        botonMandarMensajeWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtNumeroWhatsApp.getText().toString().isEmpty()){
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, txtMensaje.getText().toString());
                    sendIntent.setType("text/plain"); startActivity(sendIntent);
                    sendIntent.setPackage("com.whatsapp");
                    startActivity(sendIntent);
                }else{
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_VIEW);
                    String enlace = "whatsapp://send?phone=" + txtNumeroWhatsApp.getText().toString() +
                            "&text=" + txtMensaje.getText().toString();
                    sendIntent.setData(Uri.parse(enlace));
                    startActivity(sendIntent);
                }
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