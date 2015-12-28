package com.jhon.co.divi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jhon.co.divi.modelo.Config;
import com.jhon.co.divi.ui.HomeActivity;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //Definimos los atributos
    private EditText editTextUsuario, editTextContrasena;
    private Button buttonLogin;

    //Variable boolean para revisar si el usuario esta registrado o no
    //inicia en false
    private boolean loggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Inicializamos los atributos
        editTextUsuario = (EditText)findViewById(R.id.et_NombreUsuario);
        editTextContrasena = (EditText)findViewById(R.id.et_ContraseñaUsuario);

        buttonLogin =(Button)findViewById(R.id.btn_Login);

        //Añadimos la accion del listener al boton
        buttonLogin.setOnClickListener(this);

    }

    @Override
    protected void onResume(){
        super.onResume();

        //En onResume traemos el valor de desde sharedpreferences
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        //Traemos el valor booleano desde sharedpreferences
        loggedIn = sharedPreferences.getBoolean(Config.LOGGEDIN_SHARED_PREF, false);

        //si es verdadero
        if (loggedIn){
            //iniciamos la actividad HomeActivity
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }

    }

    private void Login(){

        //tomamos los valores de los edittext
        final String email = editTextUsuario.getText().toString().trim();
        final String password = editTextContrasena.getText().toString().trim();

        //creamos una cadena request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.LOGIN_URL,
                new Response.Listener<String>(){

            public void onResponse(String response){

            //si el acceso al servidor es exitoso
                if (response.equalsIgnoreCase(Config.LOGIN_SUCCESS)){

                    //creamos el sharedpreference
                    SharedPreferences sharedPreferences = LoginActivity.this.getSharedPreferences(Config.SHARED_PREF_NAME,
                            Context.MODE_PRIVATE);

                    //creamos editor para guardar los valores de sharedpreference
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    //añadimos los valores al editor
                    editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, true);
                    editor.putString(Config.EMAIL_SHARED_PREF, email);

                    //guardamos los valores en el editor
                    editor.commit();

                    //iniciamos la actividad HomeActivity
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);

                }else {
                    //si el servidor no responde exitosamente mostramos un mensaje de error
                    Toast toast = Toast.makeText(LoginActivity.this, "Los datos ingresados son incorrectos", Toast.LENGTH_SHORT);
                    toast.show();
                }

            }

        },
                new Response.ErrorListener(){

                    @Override
                public void onErrorResponse(VolleyError error){



                    }

                }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                //Añadimos los atributos al request
                params.put(Config.KEY_EMAIL, email);
                params.put(Config.KEY_PASSWORD, password);

                //retornamos los atributos
                return params;
            }
        };

        //añadimos la cadena request al queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

        }

    @Override
    public void onClick(View v) {

        //llamamos login
        Login();

    }
}
