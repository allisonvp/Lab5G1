package pe.pucp.tel306.firebox;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;

import pe.pucp.tel306.firebox.Clases.Files;

public class RecyclerFilesAdapter extends RecyclerView.Adapter<RecyclerFilesAdapter.FilesViewHolder> {

    private static ArrayList<Files> lista;
    private static Context contexto;

    public RecyclerFilesAdapter(ArrayList<Files> l, Context c) {
        lista = l;
        contexto = c;
    }

    @NonNull
    @Override
    public FilesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(contexto).inflate(R.layout.list_archivo, parent, false);
        FilesViewHolder fViewHolder = new FilesViewHolder(itemView);
        Log.d("debugeo", String.valueOf(lista.size()));
        return fViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FilesViewHolder holder, int position) {
        String name = lista.get(position).getName();
        long size = lista.get(position).getSize();
        String path = lista.get(position).getPath();

        holder.nombre.setText(name);
        holder.size.setText(String.valueOf(size)+" B");
        holder.path.setText(lista.get(position).getDate());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(lista.get(position).getPath());
        Glide.with(contexto)
                .load(storageReference)
                .into(holder.icon);
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    /////DIALOG

            }
        });
    }



    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class FilesViewHolder extends RecyclerView.ViewHolder {
        public TextView nombre;
        public TextView size;
        public TextView path;
        public ImageView icon;
        public View button;


        public FilesViewHolder(@NonNull View itemView) {
            super(itemView);
            this.nombre = itemView.findViewById(R.id.nameArchivo);
            this.path = itemView.findViewById(R.id.durationArchivo);
            this.size = itemView.findViewById(R.id.sizeArchivo);
            this.icon = itemView.findViewById(R.id.iconArchivo);
            this.button = itemView.findViewById(R.id.floatingActionButton);

        }
    }

}
