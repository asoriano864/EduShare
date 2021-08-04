package com.example.edushareproyect.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.edushareproyect.R;
import com.example.edushareproyect.RestApiMehotds;
import com.example.edushareproyect.databinding.FragmentHomeBinding;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ArchivosGrupo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArchivosGrupo extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ID = "id";
    private static final String GRUPO = "Grupo";
    private static final String CODIGO = "Codigo";

    // TODO: Rename and change types of parameters
    private String mid;
    private String mGrupo;
    private String mCodigo;

    private FragmentHomeBinding binding;
    TextView titulo;

    public ArchivosGrupo(String id, String grupo, String codigo) {
        // Required empty public constructor
        mid = id;
        mGrupo = grupo;
        mCodigo = codigo;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id Parameter 1.
     * @param Grupo Parameter 2.
     * @param Codigo Parameter 3.
     * @return A new instance of fragment ArchivosGrupo.
     */
    // TODO: Rename and change types and number of parameters
    public static ArchivosGrupo newInstance(String id, String Grupo, String Codigo) {
        ArchivosGrupo fragment = new ArchivosGrupo(id, Grupo, Codigo);
        Bundle args = new Bundle();
        args.putString(ID, id);
        args.putString(GRUPO, Grupo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mid = getArguments().getString(ID);
            mGrupo = getArguments().getString(GRUPO);
            mCodigo = getArguments().getString(CODIGO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root = inflater.inflate(R.layout.fragment_archivos_grupo, container, false);
        titulo = (TextView) root.findViewById(R.id.tituloClase);
        titulo.setText(mGrupo);


        return root;
    }

    //---------------------------------------------------------------------------------------------------------------//
    private void getArchivos(Integer grupoID, String token){
        JSONObject objReq = new JSONObject();
        try{
            objReq.put("grupoID",grupoID);
            objReq.put("token",token);
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, RestApiMehotds.ApiPOSTCrearGrupo, objReq, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }catch(JSONException e){
            mostrarDialogo("Error JSON",e.getMessage());
        }
    }
    //---------------------------------------------------------------------------------------------------------------//

    //-----------------------------------------------------------------------------------------------------------------------//
    private void mostrarDialogo(String title, String mensaje) {
        new AlertDialog.Builder(getActivity().getBaseContext())
                .setTitle(title)
                .setMessage(mensaje)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }
    //-----------------------------------------------------------------------------------------------------------------------//

}