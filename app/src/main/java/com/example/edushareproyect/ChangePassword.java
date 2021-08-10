package com.example.edushareproyect;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePassword extends AppCompatActivity {

    EditText txtClave1;
    EditText txtClave2;

    Button btnChangePassword;

    String clave1;
    String clave2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        txtClave1 =  (EditText) findViewById(R.id.txtClave1);
        txtClave2 = (EditText) findViewById(R.id.txtClave2);

        btnChangePassword = (Button) findViewById(R.id.btnChangePassword);
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clave1 = txtClave1.getText().toString();
                clave2 = txtClave2.getText().toString();
                if(!clave1.equals(clave2)){
                    mostrarDialogo("Error","Las claves no coinciden");
                }else{

                }

            }
        });

    }


    //-----------------------------------------------------------------------------------------------------------------------//
    private void changePassword(String pass1, String pass2){
        JSONObject req = new JSONObject();
        RequestQueue queue = Volley.newRequestQueue(this);

        try{
            req.put("clave1",pass1);
            req.put("clave2",pass2);

        }catch (JSONException jex){
            mostrarDialogo("Error",jex.getMessage());
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