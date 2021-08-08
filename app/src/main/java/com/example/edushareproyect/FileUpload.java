package com.example.edushareproyect;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.VoiceInteractor;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FileUpload extends AppCompatActivity {

    private static final int CREATE_FILE = 1;
    private static final int PICK_FILE = 2;

    byte[] byteArray;

    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload);

        SharedPreferences session = getSharedPreferences("session",MODE_PRIVATE);
        token = session.getString("token","");

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
            String Filename = cursor.getColumnName(nameIndex);

            try{
                InputStream inp = getContentResolver().openInputStream(tmpUri);
                try{
                    byte[] bytesFile = getBytes(inp);
                    Integer FileSize = bytesFile.length;
                    String FileData = Base64.encodeToString(bytesFile, Base64.DEFAULT);
                    Log.d("Tamaño",FileSize.toString());

                    /*Enviar archivo*/

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
    private void sendFile(String fileName, Integer grupoID, String token, String data){

        String url = RestApiMehotds.ApiPOSTUploadFile;

        JSONObject objReq = new JSONObject();
        try{
            objReq.put("nombre",fileName);
            objReq.put("grupoid",grupoID);
            objReq.put("token",token);
            objReq.put("data",data);

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, objReq, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    VolleyLog.d(response.toString());

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Error",error.toString());
                }
            });

        }catch(JSONException e){
            mostrarDialogo("Error","No se puede construir la peticion al servidor");
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









}