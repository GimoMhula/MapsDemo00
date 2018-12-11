package com.example.tech.mapsdemo00;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;

import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MapsActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS =new LatLngBounds(
            new LatLng(-40,-168), new LatLng(71,136));


    //widget
    private AutoCompleteTextView mSearchText;
    private ImageView mGps;

    //vars
    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient; // Voce me deu Problemas
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter; //Adapter
    private GoogleApiClient mGoogleApiClient;


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mSearchText= (AutoCompleteTextView)findViewById(R.id.input_search);
        mGps= (ImageView) findViewById(R.id.ic_gps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getLocationPermission();

    }

private void init(){
    Log.d(TAG, "init: inicializando Search");
    // Fazendo a alteracao do Enter para fazer search

    mGoogleApiClient = new GoogleApiClient
            .Builder(this)
            .addApi(Places.GEO_DATA_API)
            .addApi(Places.PLACE_DETECTION_API)
            .enableAutoManage(this, this)
            .build();

    mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this,mGoogleApiClient.get,LAT_LNG_BOUNDS,null);


    mSearchText.setAdapter(mPlaceAutocompleteAdapter);

    mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId==EditorInfo.IME_ACTION_SEARCH
                    || actionId==EditorInfo.IME_ACTION_DONE
                    || event.getAction()==event.ACTION_DOWN
                    || event.getAction()==event.KEYCODE_ENTER) {
                // Execucao do metodo para pesquisa
                geoLocate();
                Toast.makeText(MapsActivity.this, "Pesquisa", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    mGps.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: icone do gps pressionado");
            getDeviceLocation();
            Toast.makeText(MapsActivity.this, "Chefe estou aqui", Toast.LENGTH_SHORT).show();
        }
    });
    //hideSoftKeyboard();
}

    private void geoLocate() {
        Log.d(TAG, "geoLocate: Localizacao Geografica");
        String searchString=mSearchText.getText().toString();

        Geocoder geocoder=new Geocoder(MapsActivity.this);
        List<Address> list=new ArrayList<>();

        try {
            list=geocoder.getFromLocationName(searchString,1);

        }catch (IOException e){
            Log.d(TAG, "geoLocate: IOException"+e.getMessage());

        }

        if (list.size()>0){
        Address address=list.get(0); // Localizacao pesquisada
            Log.d(TAG, "geoLocate: Localizacao Localizada "+address.toString());
            Toast.makeText(this, "Localizacao Localizada "+address.toString(), Toast.LENGTH_SHORT).show();
            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,address.getAddressLine(0));

        }
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: Buscando a localizacao Actual");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();

                ((Task) location).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Localizacao encontrada");
                            Location currentLocation = (Location) task.getResult();
                            if (currentLocation != null) {
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM,"My location");
                            }

                        } else {
                            Log.d(TAG, "onComplete: Local actual nulo");
                            Toast.makeText(MapsActivity.this, "Nao foi possivel carregar a localizacao actual", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: Excepcao de Seguranca" + e.getMessage());
        }
    }


    private void moveCamera(LatLng latLng, float zoom,String title) {// mover a camera
        Log.d(TAG, "moveCamera: movendo a camera para lattude:" + latLng.latitude + "longitude:" + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        //Opcoes de marcador
            if (!title.equals("My location")){

                MarkerOptions options=new MarkerOptions()
                        .position(latLng)
                        .title(title);
                mMap.addMarker(options); // Adicionando o marcador ao mapa

             }
            // hideSoftKeyboard();
    }

    private void initMap() {// inicializar o mapa
        Log.d(TAG, "initMap: inicializando o mapa");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);

    }


    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: buscando permissao de localizacao");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permissao falhou");
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    Log.d(TAG, "onRequestPermissionsResult: permissao consedida");
                    // Inicializa o mapa pois tudo esta bem
                    initMap();
                }
            }
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        Toast.makeText(this, "Chefe o Mapa Esta Pronto", Toast.LENGTH_LONG).show();
        // Add a marker in Maputo and move the camera
//        LatLng mpt = new LatLng(-25.96553, 32.58322);
//        mMap.addMarker(new MarkerOptions().position(mpt).title("Maputo"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(mpt));


        mMap = googleMap;
        if (mLocationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);// desabilitar localizacao My location

            init();
         // mMap.getUiSettings().

        }
    }


//    private void hideSoftKeyboard(){
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//    }
}
