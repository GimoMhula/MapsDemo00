package com.example.tech.mapsdemo00;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import android.support.design.widget.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.support.design.widget.Snackbar.LENGTH_SHORT;

//import com.google.android.gms.common.api.;



public class MapsActivity extends FragmentActivity implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener,TaskLoadedCallback {

    private static final String TAG = "MapsActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    private static final LatLngBounds LAT_LNG_BOUNDS =new LatLngBounds(
            new LatLng(-40,-168), new LatLng(71,136));



    //widget
   // private AutoCompleteTextView mSearchText;
    private AutoCompleteTextView mSearchText;
   // private TextView mSearchText;
    private ImageView mGps;
    private DrawerLayout mDrawerLayout;
    private ImageView mPerfilFoto;
    private  ImageView navigation_menu;
    private CardView mSettings;

    //vars
    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient; // Voce me deu Problemas
  //  private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter; //Adapter
   // private GoogleApiClient mGoogleApiClient;

    private  String destination;
    private MarkerOptions place1, place2;
    Button getDirection;
    private Polyline currentPolyline;


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        mSearchText= (AutoCompleteTextView)findViewById(R.id.input_search);
     navigation_menu= (ImageView) findViewById(R.id.navigation_menu);

        mGps= (ImageView) findViewById(R.id.ic_gps);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mPerfilFoto=(ImageView) findViewById(R.id.perfil_foto) ;

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        // Crindo circunferencia na foto de perfil
       // Bitmap bitmap=new BitmapFactory.decodeResource(getResources(),R.drawable.stlsm);


        // AutoComplite CardView
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                destination=place.getName().toString();
                 geoLocate();
                Toast.makeText(MapsActivity.this, "Pesquisa", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Place: " + place.getName());
                Toast.makeText(MapsActivity.this, place.getName().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

    // Filtro para Mocambique
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry("MZ")
                .build();

        autocompleteFragment.setFilter(typeFilter);

        // Nevegation Drawer
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        int id = menuItem.getItemId();

                        if (id == R.id.nav_home) {
                            Toast.makeText(MapsActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        } else if (id == R.id.nav_termos_de_uso) {
                            Toast.makeText(MapsActivity.this, "Termos de Uso", Toast.LENGTH_SHORT).show();
                        } else if (id == R.id.nav_definicoes) {
                            Toast.makeText(MapsActivity.this, "Definicoes", Toast.LENGTH_SHORT).show();
                        } else if (id == R.id.nav_promocao) {
                            Toast.makeText(MapsActivity.this, "Promocao", Toast.LENGTH_SHORT).show();
                        } else if (id == R.id.nav_pagamento) {
                            Toast.makeText(MapsActivity.this, "Pagamento", Toast.LENGTH_SHORT).show();
                        } else if (id == R.id.nav_feedback) {
                            Toast.makeText(MapsActivity.this, "Feedback", Toast.LENGTH_SHORT).show();
                        } else if (id == R.id.nav_historico) {

                            Toast.makeText(MapsActivity.this, "Historico", Toast.LENGTH_SHORT).show();
                        }

                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                        return true;
                    }
                });

            // Botao get direction Funcionalidade
        getDirection = findViewById(R.id.btnGetDirection);
        getDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MapsActivity.this, "Problema", Toast.LENGTH_SHORT).show();
                new FetchURL(MapsActivity.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");
            }
        });

        //coope -25.9527746,32.58862495
        // Alto mae -25.95634688,32.56630898

        place1 = new MarkerOptions().position(new LatLng(-25.9527746,32.58862495)).title("Location 1");
        place2 = new MarkerOptions().position(new LatLng(-25.95634688,32.56630898)).title("Location 2");
        // Fim Botao direction
        String url=getUrl(place1.getPosition(),place2.getPosition(),"driving");
        new FetchURL(MapsActivity.this).execute(url,"driving");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getLocationPermission();

    }

// Metodos para Rota
    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
    // Fim metodos para rotas

private void init(){
    Log.d(TAG, "init: inicializando Search");


//    mGoogleApiClient = new GoogleApiClient
//            .Builder(this)
//            .addApi(Places.GEO_DATA_API)
//            .addApi(Places.PLACE_DETECTION_API)
//            .enableAutoManage(this, this)
//            .build();

   // mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this,mGoogleApiClient,LAT_LNG_BOUNDS,null);


    //mSearchText.setAdapter(mPlaceAutocompleteAdapter);

    // Fazendo a alteracao do Enter para fazer search
//    mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//        @Override
//        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//            if (actionId==EditorInfo.IME_ACTION_SEARCH
//                    || actionId==EditorInfo.IME_ACTION_DONE
//                    || event.getAction()==event.ACTION_DOWN
//                    || event.getAction()==event.KEYCODE_ENTER) {
//                // Execucao do metodo para pesquisa
//                geoLocate();
//                Toast.makeText(MapsActivity.this, "Pesquisa", Toast.LENGTH_SHORT).show();
//            }
//            return false;
//        }
//    });

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


        Geocoder geocoder=new Geocoder(MapsActivity.this);
        List<Address> list=new ArrayList<>();

        try {
            list=geocoder.getFromLocationName(destination,1);

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

      mMap.addMarker(place1);
      mMap.addMarker(place2);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
  return true;
    }

    public void openDrawer(View view) {
        mDrawerLayout.openDrawer(GravityCompat.START,true);
    }









//    private void hideSoftKeyboard(){
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//    }
}
