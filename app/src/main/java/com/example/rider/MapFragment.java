package com.example.rider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;


public class MapFragment extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = "";
    private GoogleMap mMap;
    private Location mlocation;
    private GoogleApiClient mgoogleApiClient;
    private LocationRequest mlocationRequest;
    private Marker currentloactionmarker;
    private static final int Permission_code = 7001;

    private static final int Play_service_request = 7002;

    private static final int Update_interval = 5000;

    private static final int Fastest_Interval = 3000;

    private static final int Displacement = 10;
    private ImageView gps;
    private double  latitude = 24.893325;
     private double longitude = 66.978646;;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_fragment);


        gps = findViewById(R.id.gpsLocation);


        String api_key = getString(R.string.places_api_key);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), api_key);

            PlacesClient placesClient = Places.createClient(this);
        } else {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();

        }

        /////Initailize the AutoCompletFragment over here////////////////////////

        AutocompleteSupportFragment autocompleteSupportFragment =
                (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));


        try {

            autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    displayLocation(place.getLatLng().latitude, place.getLatLng().longitude, place.getName());
                    mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                    //Toast.makeText(getApplicationContext(), "getName" + place.getName() + "getLatgLng" + place.getLatLng(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(@NonNull Status status) {

                    Toast.makeText(getApplicationContext(), status.toString(), Toast.LENGTH_LONG).show();

                }
            });

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();

        }

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.maps);
        supportMapFragment.getMapAsync(this);
        setUpLocation();
        HideSoftkeyboard();
        GPSLOCATION();
    }

    ///////GPSLOCATION IMageiew when user click on it  it will enabled user current Location/////////////////
    public void GPSLOCATION() {

        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    buildGoogleApiClient();
                }
                else
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                if(currentloactionmarker !=null)
                {
                    currentloactionmarker.remove();
                }

                LatLng karachi = new LatLng(mlocation.getLatitude(),mlocation.getLongitude());
                MarkerOptions markerOptions= new MarkerOptions();
                markerOptions.position(karachi);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                currentloactionmarker= mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(karachi));
                mMap.animateCamera(CameraUpdateFactory.zoomBy(10));
//                if(mgoogleApiClient!=null)
//                {
//                    LocationServices.FusedLocationApi.removeLocationUpdates(mgoogleApiClient,this);
//
//                }

            }
});

    }





///////////////////////////////////////////////////////////////////////////////////////

    private void displayLocation(final double latitude, final double longitude, String name) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            mlocation = LocationServices.FusedLocationApi.getLastLocation(mgoogleApiClient);
            if (mlocation != null) {
                mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                        .title(name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 10.0f));
            }
        } catch (Exception e) {

        }

    }





    private void setUpLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    Permission_code);
        } else {

            if (checkPlayService()) {
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();

            }

        }
    }





    private void createLocationRequest() {


        mlocationRequest = new LocationRequest();
        mlocationRequest.setInterval(Update_interval);
        mlocationRequest.setFastestInterval(Fastest_Interval);
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mlocationRequest.setSmallestDisplacement(Displacement);

    }


    private void buildGoogleApiClient() {
        mgoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mgoogleApiClient.connect();
    }


    private Boolean checkPlayService() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode,
                        this, Play_service_request).show();
            else {
                Toast.makeText(getApplicationContext(), "THE Device is not Supported", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;

        }
        return true;

    }






    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           // return;
        }

        mlocation = LocationServices.FusedLocationApi.getLastLocation(mgoogleApiClient);
        if (mlocation != null) {




            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Karachi")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 10.0f));

        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
displayLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    mgoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        buildGoogleApiClient();

  //      final double latitude=;
//        final double longitude=;
        mMap= googleMap;
        //mMap.addMarker(new MarkerOptions().position(new LatLng()))

//mMap.addMarker(new MarkerOptions().title("").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

    }

    @Override
    public void onLocationChanged(Location location) {

        mlocation=location;
        displayLocation();
    }

    ///hide keyboard after putting some value in searchbar////////////////////////////////////
    private void HideSoftkeyboard()
    {

this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }
}
