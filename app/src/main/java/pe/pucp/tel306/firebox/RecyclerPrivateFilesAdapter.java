package pe.pucp.tel306.firebox;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

import pe.pucp.tel306.firebox.Clases.Files;

public class RecyclerPrivateFilesAdapter extends RecyclerView.Adapter<RecyclerPrivateFilesAdapter.FilesViewHolder> {
    private static ArrayList<Files> lista;
    private static Context contexto;

    public RecyclerPrivateFilesAdapter(ArrayList<Files> l, Context c) {
        lista = l;
        contexto = c;
    }

    @NonNull
    @Override
    public RecyclerPrivateFilesAdapter.FilesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(contexto).inflate(R.layout.list_private, parent, false);
        RecyclerPrivateFilesAdapter.FilesViewHolder fViewHolder = new RecyclerPrivateFilesAdapter.FilesViewHolder(itemView);
        return fViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerPrivateFilesAdapter.FilesViewHolder holder, int position) {
        String name = lista.get(position).getName();
        long size = lista.get(position).getSize();

        holder.nombre.setText(name);
        holder.size.setText(String.valueOf(size)+" B");
        holder.path.setText(lista.get(position).getDate());

    }
    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class FilesViewHolder extends RecyclerView.ViewHolder {
        public TextView nombre;
        public TextView size;
        public TextView path;


        public FilesViewHolder(@NonNull View itemView) {
            super(itemView);
            this.nombre = itemView.findViewById(R.id.nameArchivo);
            this.path = itemView.findViewById(R.id.durationArchivo);
            this.size = itemView.findViewById(R.id.sizeArchivo);

        }
    }
}
