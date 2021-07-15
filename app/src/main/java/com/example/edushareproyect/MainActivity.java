package com.example.edushareproyect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    String url;
    String token;
    String correo = "";
    String password = "";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText txtCorreo = (EditText) findViewById(R.id.LoginCorreo);
        EditText txtPassword = (EditText) findViewById(R.id.LoginPassword);

        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                correo = txtCorreo.getText().toString();
                password = txtPassword.getText().toString();
                if(correo.equals("") || password.equals("")){
                    Toast.makeText(getApplicationContext(), "Debe ingresar el correo y password",Toast.LENGTH_LONG).show();
                }else{
                    Login(correo,password);

                }



            }
        });

    }

    public void Login(String correo, String password){
        url = RestApiMehotds.ApiPOSTLogin;
        HashMap<String, String> params =new HashMap<String, String>();
        params.put("correo",correo);
        params.put("password",password);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getApplicationContext(), "Respuesta: " + response, Toast.LENGTH_LONG).show();
                VolleyLog.d("Status "+ response.toString());
                try{
                    String statusRespuesta = response.getString("STATUS");
                    if(statusRespuesta.equals("1")){
                        token = response.getString("DATA");
                        /*Datos validos iniciamos la sesion*/
                        Intent intent = new Intent(getApplicationContext(),VistaPrincipal.class);
                        startActivity(intent);

                    }else{
                        Toast.makeText(getApplicationContext(), "Usuario o clave no valida", Toast.LENGTH_LONG).show();
                    }

                }catch(Exception e){
                    Toast.makeText(getApplicationContext(), "Respuesta: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error"+error.getMessage(), "Error: "+error.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        RequestQueue requestQueue =Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);

    }

}