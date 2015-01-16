package com.example.majkel.gpsapplication;


import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private EditText p_name;
    private EditText p_desc;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private GoogleMap mMap;
    private final String FILE_NAME = "location_data2";
    List<Place> cachedPlaces;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //pobranie mapy
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
        //inicjalizacja zmiennych
        p_name = (EditText) findViewById(R.id.place_name);
        p_desc = (EditText) findViewById(R.id.place_description);
        cachedPlaces = new ArrayList<Place>();
        //utworzenie klienta api
        buildGoogleApiClient();
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    //callback po pobraniu mapy - naniesienie punktów z pliku
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        try {
            readPlacesFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        addMarkers();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //połączenie z api, callback z utworzenia apiclienta
    @Override
    protected void onStart() {
        super.onStart();
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    //reset formatek
    public void resetForm(View view) {
        p_name.setText("");
        p_desc.setText("");
    }


    //metoda do dodawania aktualnej lokalizacji do mapy i do pliku
    public void addPlace(View view) throws IOException {
//        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
//                mGoogleApiClient);
        if (mLastLocation != null) {
            Place place = new Place(mLastLocation.getLatitude(), mLastLocation.getLongitude(), p_name.getText().toString(), p_desc.getText().toString());
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(place.getmLat(), place.getmLong()))
                    .title(place.getP_name())
                    .snippet(place.getP_desc()));
            toast = Toast.makeText(this, "Dodano miejsce " + place.getP_name(), Toast.LENGTH_LONG);
            toast.show();
            cachedPlaces.add(place);
            try {
                savePlacesToFile();
            }
            catch (IOException e) {
                Log.e("Error while loading db",e.toString());
            }
        }
    }
    //zapis miejsc do pliku
    private void savePlacesToFile() throws IOException {
        InternalStorage.writeObject(this,FILE_NAME,cachedPlaces);
    }

    //odczytanie miejsc z pliku
    private void readPlacesFromFile() throws IOException, ClassNotFoundException {
        cachedPlaces = (ArrayList<Place>) InternalStorage.readObject(this,FILE_NAME);
    }

    public void addMarkers() {
        if (!cachedPlaces.isEmpty()){
            for (Place pl : cachedPlaces){
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(pl.getmLat(), pl.getmLong()))
                        .title(pl.getP_name())
                        .snippet(pl.getP_desc()));
            }
            toast = Toast.makeText(this, "Wczytano miejsca z pliku", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        //pobranie ostatniej znanej lokalizacji i zapisanie jej do zmiennej
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("Connection failed",connectionResult.toString());
    }
}
