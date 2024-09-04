package com.equipo09.directoatuhabitacion;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int tiempoCarga;
        if (isConnectedToWifi()) {
            // Si está conectado a Wi-Fi, establece un tiempo de carga más corto
            tiempoCarga = 1000;
        } else {
            // Si no está conectado a Wi-Fi, establece un tiempo de carga más largo
            tiempoCarga = 5000;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Verificar la conexión a Internet antes de cargar la actividad
                if (isConnectedToInternet()) {
                    startActivity(new Intent(getApplicationContext(), Principal.class));
                } else {
                    Toast.makeText(MainActivity.this, "Sin conexión a Internet. Inténtalo de nuevo más tarde.", Toast.LENGTH_SHORT).show();
                    // Puedes cerrar la actividad actual aquí si lo deseas
                }
            }
        }, tiempoCarga);
    }

    // Métodos para verificar la conexión a Internet y Wi-Fi
    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        }
        return false;
    }

    private boolean isConnectedToWifi() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
        }
        return false;
    }
}
