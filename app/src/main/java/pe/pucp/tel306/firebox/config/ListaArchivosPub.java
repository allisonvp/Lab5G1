package pe.pucp.tel306.firebox.config;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pe.pucp.tel306.firebox.R;

public class ListaArchivosPub extends Fragment {

    public ListaArchivosPub() {
        // Required empty public constructor
    }

    public static ListaArchivosPub newInstance() {
        ListaArchivosPub fragment = new ListaArchivosPub();

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
        return inflater.inflate(R.layout.fragment_lista_archivos, container, false);
    }
}