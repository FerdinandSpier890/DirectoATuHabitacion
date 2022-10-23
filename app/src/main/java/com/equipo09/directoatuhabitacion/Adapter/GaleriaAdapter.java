package com.equipo09.directoatuhabitacion.Adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.equipo09.directoatuhabitacion.Pojos.Galeria;
import com.equipo09.directoatuhabitacion.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GaleriaAdapter extends RecyclerView.Adapter<GaleriaAdapter.FotosViewHolder> {

    List<Galeria> galeriaList;
    Context context;


    public GaleriaAdapter(List<Galeria> galeriaList, Context context) {
        this.galeriaList = galeriaList;
        this.context = context;
    }

    @NonNull
    @Override
    public FotosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_record, parent, false);
        return new FotosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FotosViewHolder holder, int position) {
        Galeria galeria = galeriaList.get(position);
        holder.txtUsuario.setText(galeria.getUsuario());
        holder.txtDescripcion.setText(galeria.getDescipcion());
        Picasso.get().load(galeria.getFoto()).into(holder.fotoPublicacion, new Callback() {
            @Override
            public void onSuccess() {
                holder.barraProgreso.setVisibility(View.GONE);
                holder.fotoPublicacion.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(context, "Error al Cargar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return galeriaList.size();
    }

    public class FotosViewHolder extends RecyclerView.ViewHolder {
        TextView txtDescripcion, txtUsuario;
        ImageView fotoPublicacion;
        ProgressBar barraProgreso;
        public FotosViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDescripcion = itemView.findViewById(R.id.txtTituloPublicacion);
            txtUsuario = itemView.findViewById(R.id.txtUsuarioPublicacion);
            fotoPublicacion = itemView.findViewById(R.id.imagenPublicacion);
            barraProgreso = itemView.findViewById(R.id.progressGaleria);
        }
    }
}
