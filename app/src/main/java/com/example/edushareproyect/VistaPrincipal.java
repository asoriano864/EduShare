package com.example.edushareproyect;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.edushareproyect.databinding.ActivityVistaPrincipalBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VistaPrincipal extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityVistaPrincipalBinding binding;

    String token;
    Integer perfilID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        validarSesion();

        binding = ActivityVistaPrincipalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarVistaPrincipal.toolbar);
        binding.appBarVistaPrincipal.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Intent fileUp = new Intent(getApplicationContext(), FileUpload.class);
                startActivity(fileUp);
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.listaContactos, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_vista_principal);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.vista_principal, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_vista_principal);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                logout();
                return true;
            case R.id.action_perfil:
                SharedPreferences informacionSession = getSharedPreferences("session", Context.MODE_PRIVATE);
                String token = informacionSession.getString("token","");
                Integer perfilID = informacionSession.getInt("perfilID",0);
                Boolean active = informacionSession.getBoolean("active",false);
                if(!active){
                    Intent vistaP = new Intent(getApplicationContext(), VistaPrincipal.class);
                    startActivity(vistaP);
                }
                Log.d("PerfilID: ",informacionSession.toString());
                Log.d("Token: ",token);
                Intent verPerfil = new Intent(getApplicationContext(), VerPerfil.class);
                if(perfilID==1){
                    /*estudiante*/
                    verPerfil.putExtra("usuario", "estudiante");

                }else{
                    /*Catedratico*/
                    verPerfil.putExtra("usuario", "catedratico");
                }
                startActivity(verPerfil);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }


    }


    //-----------------------------------------------------------------------------------------------------------------------//
    private void validarSesion(){
        SharedPreferences informacionSession = getSharedPreferences("session", Context.MODE_PRIVATE);
        String token = informacionSession.getString("token","");
        Integer perfilID = informacionSession.getInt("perfilID",0);
        Boolean active = informacionSession.getBoolean("active",false);
        if(!active){
            Intent vistaP = new Intent(getApplicationContext(), VistaPrincipal.class);
            startActivity(vistaP);
        }
        revisarToken(token);
    }

    //-----------------------------------------------------------------------------------------------------------------------//

    //-----------------------------------------------------------------------------------------------------------------------//
    private void logout(){
        SharedPreferences session = getSharedPreferences("session", Context.MODE_PRIVATE);
        session.edit().clear().commit();
        Intent main = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(main);
    }
    //-----------------------------------------------------------------------------------------------------------------------//

    public void revisarToken(String token){
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = RestApiMehotds.ApiPOSTToken;
        JSONObject postData = new JSONObject();
        try {
            postData.put("token", token);
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
                            if (!objeto.getString("existe").equalsIgnoreCase("1")) {
                                logout();
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
                Log.i("Error en Response", "onResponse: " +  error.getMessage() );
            }
        });
        queue.add(jsonObjectRequest);

    }

}
