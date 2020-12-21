package pe.pucp.tel306.firebox.Fragmentos;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import pe.pucp.tel306.firebox.Clases.Files;
import pe.pucp.tel306.firebox.R;
import pe.pucp.tel306.firebox.RecyclerFilesAdapter;

public class ListaArchivosPub extends Fragment {
    private static ArrayList<Files> lista=new ArrayList<>();
    private static Context contexto;


    public ListaArchivosPub() {
        // Required empty public constructor
    }

    public static ListaArchivosPub newInstance(ArrayList<Files> l, Context c) {
        ListaArchivosPub fragment = new ListaArchivosPub();
        lista = l;
        contexto = c;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lista_archivos, container, false);
        RecyclerFilesAdapter adapter = new RecyclerFilesAdapter(lista, contexto);
        RecyclerView rView = view.findViewById(R.id.listRecyclerView);
        rView.setAdapter(adapter);
        rView.setLayoutManager(new LinearLayoutManager(contexto));
        return view;



    }
}