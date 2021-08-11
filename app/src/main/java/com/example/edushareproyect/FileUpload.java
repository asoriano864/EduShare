package com.example.edushareproyect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.VoiceInteractor;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.edushareproyect.Objetos.Grupo;
import com.example.edushareproyect.ui.ArchivosGrupo;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class FileUpload extends AppCompatActivity {

    private static final int CREATE_FILE = 1;
    private static final int PICK_FILE = 2;

    byte[] byteArray;

    Integer PerfilID;
    String token;
    String FileData;
    String FileName;
    String FileExtension;
    String Grupo ="";

    FirebaseDatabase database;
    DatabaseReference reference;
    int id;


    ImageButton btnFileUploadAction;
    Spinner GruposList;
    TextView txtFileSelected;
    TextView txtFileSize;

    ArrayList ListaGrupos = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload);

        SharedPreferences session = getSharedPreferences("session",MODE_PRIVATE);
        token = session.getString("token","");
        PerfilID = session.getInt("perfilID",0);

        GruposList = findViewById(R.id.spinnerGruposFile);

        ListaGrupos.add("");
        ObtenerGrupos();
        ArrayAdapter<CharSequence> adp = new ArrayAdapter(this, android.R.layout.simple_spinner_item, ListaGrupos);
        GruposList.setAdapter(adp);

        btnFileUploadAction = (ImageButton) findViewById(R.id.btnLoadFile);
        btnFileUploadAction.setEnabled(false);
        btnFileUploadAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFileUploadAction.setEnabled(false);
                sendFile();
            }
        });

        txtFileSelected = findViewById(R.id.txtFileSelected);
        txtFileSize = findViewById(R.id.txtFileSize);


        Button btnFileSelect = (Button) findViewById(R.id.btnSelectFile);
        btnFileSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFile(Uri.parse("/storage"));

            }
        });

    }

    private void openFile(Uri urlInicial){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, urlInicial);
        startActivityForResult(intent, PICK_FILE);
    }

    //-----------------------------------------------------------------------------------------------------------------------//
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE && resultCode == RESULT_OK) {

            Uri tmpUri = data.getData();
            /*Retorna el tipo de archivo*/
            String mimetype = getContentResolver().getType(tmpUri);
            /*Obtener tamaño del archivo*/
            Cursor cursor = getContentResolver().query(tmpUri,null,null,null,null);

            Integer nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            cursor.moveToFirst();

            this.FileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            btnFileUploadAction.setEnabled(true);

            try{
                InputStream inp = getContentResolver().openInputStream(tmpUri);
                try{
                    byte[] bytesFile = getBytes(inp);
                    Integer FileSize = bytesFile.length;
                    String FileData = Base64.encodeToString(bytesFile, Base64.DEFAULT);
                    this.FileData = FileData;
                    this.FileExtension = mimetype;

                    Log.d("Nombre Archivo: ",this.FileName);
                    Log.d("Tamaño",FileSize.toString());
                    Log.d("Datos: ",this.FileData);
                    Log.d("Tipo:", this.FileExtension);
                    DecimalFormat df = new DecimalFormat("#.##");
                    df.setRoundingMode(RoundingMode.CEILING);

                    Float Size = (FileSize.floatValue()/1000)/1000;

                    txtFileSelected.setText("Archivo: "+ FileName);
                    txtFileSize.setText("Tamaño: "+df.format(Size)+" Mb");


                }catch(IOException e){
                    mostrarDialogo("Error","El archivo no tiene un formato valido o es muy grande");
                }

            }catch(Exception e){
                mostrarDialogo("Error",e.getMessage());
            }

        }
    }
    //-----------------------------------------------------------------------------------------------------------------------//

    //-----------------------------------------------------------------------------------------------------------------------//
    public byte[] getBytes(InputStream inputStream) throws IOException{
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        Integer bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        Integer len = 0;
        while ((len = inputStream.read(buffer)) != -1){

            byteBuffer.write(buffer, 0, len);


        }
        return byteBuffer.toByteArray();
    }
    //-----------------------------------------------------------------------------------------------------------------------//

    //-----------------------------------------------------------------------------------------------------------------------//
    private void sendFile(){

        String url = RestApiMehotds.ApiPOSTUploadFile;
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        Grupo = GruposList.getSelectedItem().toString();

        if(Grupo.equals("")){

            Snackbar sn = Snackbar.make(findViewById(android.R.id.content), "Debe Seleccionar un grupo", Snackbar.LENGTH_LONG);
            sn.show();


            //mostrarDialogo("Error","Debe seleccionar un grupo");
            return;
        }

        JSONObject objReq = new JSONObject();
        try{
            objReq.put("nombre",FileName);
            objReq.put("grupo",Grupo);
            objReq.put("token",token);
            objReq.put("data",FileData);
            objReq.put("extension",this.FileExtension);

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, objReq, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        JSONArray ar = response.getJSONArray("response");
                        JSONObject r = ar.getJSONObject(0);
                        Integer status = r.getInt("STATUS");
                        if(status==1){

                            String idGrupo = r.getString("GRUPOID");
                            String nombreGrupo = r.getString("GRUPO");
                            String codigoGrupo = r.getString("CODIGO");

                            //DATABASE FIREBASE//
                            database = FirebaseDatabase.getInstance();
                            reference = database.getReference().child("Archivos");
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        id = (int) snapshot.getChildrenCount();
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            reference.child(String.valueOf(id)).setValue(FileName);
                            reference.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                    if(!FileName.isEmpty()){
                                        notificacion();
                                    }
                                }
                                @Override
                                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                }
                                @Override
                                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                                }
                                @Override
                                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });


                            //Abrir fragmento nuevo
                            Intent intent = new Intent(getApplicationContext(),VistaPrincipal.class);
                            intent.putExtra("REDIRECT","ARCHIVOS_GRUPOS");
                            intent.putExtra("GRUPOID",idGrupo);
                            intent.putExtra("GRUPO",nombreGrupo);
                            intent.putExtra("CODIGO",codigoGrupo);
                            startActivity(intent);


                        }else{
                            mostrarDialogo("Error",r.getString("MESSAGE"));
                        }



                    } catch (JSONException e) {
                        mostrarDialogo("Error",e.getMessage());
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mostrarDialogo("Error",error.toString());
                    Log.e("Error",error.toString());
                }
            });

            queue.add(objectRequest);
        }catch(JSONException e){
            mostrarDialogo("Error","No se puede construir la peticion al servidor");
        }
        btnFileUploadAction.setEnabled(true);
    }
    //-----------------------------------------------------------------------------------------------------------------------//

    //-----------------------------------------------------------------------------------------------------------------------//
    private void ObtenerGrupos(){
        String url;
        if(this.PerfilID == 1){
            url = RestApiMehotds.ApiGruposUser;
        }else{
            url = RestApiMehotds.ApiPOSTListaGrupos;
        }
        Log.d("url",url);


        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject objreq = new JSONObject();
        try{
            objreq.put("nombre",FileName);
            objreq.put("token",token);
            objreq.put("grupo", Grupo);
            objreq.put("data",FileData);

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, objreq, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("data",response.toString());
                    try {
                        if(response.getString("status").equalsIgnoreCase("1")) {
                            JSONArray grupoArrayJSON = response.getJSONArray("data");

                            if(grupoArrayJSON.length()>=1) {

                                /*llenamos la lista con los datos de los grupos registrados*/
                                for (int i = 0; i < grupoArrayJSON.length(); i++) {
                                    JSONObject grupoObject = grupoArrayJSON.getJSONObject(i);

                                    Grupo grupo = new Grupo(grupoObject.getString("GRUPOID"),
                                            grupoObject.getString("GRUPO"),
                                            grupoObject.getString("CODIGO"),
                                            grupoObject.getString("USUARIOID"));

                                    ListaGrupos.add(grupoObject.getString("GRUPO"));

                                }


                            }else{
                                /*No hay grupos registrados, mostramos un boton para agregar*/
                                mostrarDialogo("Error", "No tiene grupos registrados");
                            }
                        }else{
                            mostrarDialogo("Error", "No se pueden obtener los grupos");
                        }

                    } catch (JSONException ex) {
                        mostrarDialogo("Error",ex.getMessage());

                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mostrarDialogo("Error","No se puede obtener informacion");
                }
            });


            queue.add(objectRequest);

        }catch(JSONException ex){
            mostrarDialogo("Error","No se puede crear la peticion");
        }


    }
    //-----------------------------------------------------------------------------------------------------------------------//

    //-----------------------------------------------------------------------------------------------------------------------//
    private void mostrarDialogo(String title, String mensaje) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(mensaje)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }
    //-----------------------------------------------------------------------------------------------------------------------//

    private void notificacion(){
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = RestApiMehotds.ApiPostTokenExist;
        JSONObject postData = new JSONObject();
        try {
            postData.put("token", token);
            postData.put("grupo", Grupo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getString("status").equals("1")) {
                        JSONArray data = response.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject objeto = data.getJSONObject(i);
                            if (objeto.getString("existe").equalsIgnoreCase("1")) {

                                Intent intent = new Intent(getApplicationContext(), VistaPrincipal.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                                    NotificationChannel channel =
                                            new NotificationChannel("n", "n", NotificationManager.IMPORTANCE_HIGH);

                                    NotificationManager manager = getSystemService(NotificationManager.class);
                                    manager.createNotificationChannel(channel);
                                }
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "n")
                                        .setContentText("Se ha cargado el archivo " + FileName)
                                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                                        .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.logo_v1))
                                        .setAutoCancel(true)
                                        .setContentTitle("Nuevo Archivo en la clase " + Grupo)
                                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                        .setContentIntent(pendingIntent);

                                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                                notificationManagerCompat.notify(999, builder.build());

                            }
                        }
                    }
                } catch (JSONException ex) {
                    Log.d("EXCEPTION", ex.getMessage());
                    Toast.makeText(getApplicationContext(), "Excepción en Response", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Error en Response", "onResponse: " +  error.getMessage().toString() );
            }
        });
        queue.add(jsonObjectRequest);
    }









}