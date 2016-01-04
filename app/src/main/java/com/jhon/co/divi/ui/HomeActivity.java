package com.jhon.co.divi.ui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jhon.co.divi.LoginActivity;
import com.jhon.co.divi.R;
import com.jhon.co.divi.modelo.Config;
import com.jhon.co.divi.modelo.PermissionUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback{
    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after
     returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    LatLng latLng;
    GoogleMap mMap;
    SupportMapFragment supportMapFragment;
    Marker marker;

    //textview para mostrar el email, nombre, id del usuario conectado actualmente
    TextView textViewEmail;

    Context context;

    double latitudeF, longitudeF;

    TextView locationText;
    TextView tiempoText;
    TextView rangoText;
    TextView longitudeText;
    TextView latitudeText;

    //Float distance;

    Circle circle;

    String dist;

    private static final String TAG = "MyApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager
                ().findFragmentById(R.id.mapHomeUbication);
        supportMapFragment.getMapAsync(this);

        context = this;

        // inicializamos el textview
        textViewEmail = (TextView)findViewById(R.id.txt_EmailUsuario);
        locationText = (TextView) findViewById(R.id.location);
        longitudeText = (TextView) findViewById(R.id.longitude);
        latitudeText = (TextView) findViewById(R.id.latitude);
        tiempoText = (TextView) findViewById(R.id.tiempo);
        rangoText = (TextView )findViewById(R.id.rango);

        // cargamos el email desde sharedpreferences
        SharedPreferences sharedPreferences = getSharedPreferences
                (Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String email = sharedPreferences.getString(Config.EMAIL_SHARED_PREF, "No Disponible");

                // mostramos el email actualmente logueado
                textViewEmail.setText("Usuario : " + email);

        onMapReady(mMap);

    }



    // function salir
    private void logout(){
        //Creating an alert dialog to confirm logout
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Esta seguro que quiere salir?");
        alertDialogBuilder.setPositiveButton("Sí",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        //Getting out sharedpreferences
                        SharedPreferences preferences = getSharedPreferences
                                (Config.SHARED_PREF_NAME,Context.MODE_PRIVATE);
                        //Getting editor
                        SharedPreferences.Editor editor = preferences.edit();

                        //Puting the value false for loggedin
                        editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, false);

                        //Putting blank value to email
                        editor.putString(Config.EMAIL_SHARED_PREF, "");

                        //Saving the sharedpreferences
                        editor.commit();

                        //Starting login activity
                        Intent intent = new Intent(HomeActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                    }
                });

        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        //Showing the alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Adding our menu to toolbar
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.log_OutMenu) {
            //calling logout method when the logout button is clicked
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        finish();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        setUpMap();
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera.
     In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once.
     */
    private void setUpMap() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this,
                    LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }else if (mMap != null) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationChangeListener(myLocationChangeListener());
        }
    }

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener(){
        return new GoogleMap.OnMyLocationChangeListener(){
            @Override
            public void onMyLocationChange(Location location){
                LatLng latLngI  = new LatLng(location.getLatitude(),
                        location.getLongitude());
                longitudeF = location.getLongitude();
                latitudeF = location.getLatitude();

                DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date(location.getTime());
                String formatted = format.format(date);
                tiempoText.setText(formatted.toString());

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng
                        (latitudeF, longitudeF), 13));

                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker

                options.position(new LatLng(latitudeF, longitudeF));

                latLng = new LatLng(latitudeF, longitudeF);

                drawMarkerWithCircle(latLng);

                // Get back the mutable Circle
                //circle = mMap.addCircle(circleOptions);

                //getDistance(latLngI, latLng);

                float[] distance = new float[2];

                Location.distanceBetween( marker.getPosition().latitude,
                        marker.getPosition().longitude,
                        circle.getCenter().latitude, circle.getCenter
                                ().longitude, distance);

                if( distance[0] > circle.getRadius()  ){
                    Toast.makeText(getBaseContext(), "fuera del rango: "
                            + distance[0] + " radio: " + circle.getRadius(), Toast.LENGTH_LONG).show();
                } else if(distance[0] < circle.getRadius()){
                    Toast.makeText(getBaseContext(), "dentro del rango: "
                            + distance[0] + " radio: " + circle.getRadius() , Toast.LENGTH_LONG).show();
                }

                //mMap.addMarker(new MarkerOptions().position(latLng));
                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,8.0f));
                locationText.setText("estas en [" + longitudeF + " ; " + latitudeF
                        + "] ");
                Log.d(TAG, "onMyLocationChange: 1" + distance);

            }
        };
    }

    private void drawMarkerWithCircle(LatLng position){
        double radiusInMeters = 300;
        int strokeColor = 0xffff0000; //red outline
        int shadeColor = 0x44ff0000; //opaque red fill

        CircleOptions circleOptions = new CircleOptions().center(position).radius
                (radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
        circle = mMap.addCircle(circleOptions);

        MarkerOptions markerOptions = new MarkerOptions().position(position);
        marker = mMap.addMarker(markerOptions);
    }

}