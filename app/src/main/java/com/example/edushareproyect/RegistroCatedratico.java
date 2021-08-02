package com.example.edushareproyect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class RegistroCatedratico extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_catedratico);
        validarSesion();
    }

    private void validarSesion(){
        SharedPreferences informacionSession = getSharedPreferences("MailValidation", Context.MODE_PRIVATE);
        String token = informacionSession.getString("token","");
        Integer perfilID = informacionSession.getInt("perfilID",0);
        Boolean active = informacionSession.getBoolean("active",false);
        if(active){
            Intent vistaP = new Intent(getApplicationContext(), VistaPrincipal.class);
            startActivity(vistaP);
        }
    }
}