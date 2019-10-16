package com.example.mynew;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.button.MaterialButton;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
//, OnConnectionFailedListener
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleApiClient mGoogleApiClient;

    private GoogleMap map;
    private FusedLocationProviderClient locClient;
    private PlacesClient placesClient;
    private Location lastLoc;
    private LocationCallback locCallback;

    //GoogleApiClient client=new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this)
    //.addApi(LocationServices.API).addApi(Places.GEO_DATA_API).enableAutoManage(this,this).build();

    private View mView;

  //  private List<AutocompletePrediction> predictionList;
//    private MaterialSearchBar searchBar;
    // private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //    btn=findViewById(R.id.find);
        // searchBar = findViewById(R.id.searchBar);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mView = mapFragment.getView();

        locClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
        // if (!Places.isInitialized()) {
       // Places.initialize(MapsActivity.this, getString(R.string.google_maps_key));
      //  placesClient = Places.createClient(this);

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);

        if(mView!=null && mView.findViewById(Integer.parseInt("1"))!=null)
        {
            View locBtn=((View) mView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) locBtn.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP,0);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
            params.setMargins(0,0,40,180);
        }


        LocationRequest request=LocationRequest.create();
        request.setInterval(10000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder=new LocationSettingsRequest.Builder().addLocationRequest(request);
        SettingsClient client= LocationServices.getSettingsClient(MapsActivity.this);
        Task<LocationSettingsResponse> task= client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
            getCurrentLoc();
            }

        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e instanceof ResolvableApiException)
                {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(MapsActivity.this,27);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });


    }
    public void getCurrentLoc(){

        locClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful())
                {
                    lastLoc=task.getResult();
                    if(lastLoc!=null)
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLoc.getLatitude(),lastLoc.getLongitude()),25));
                    else{
                        LocationRequest req=LocationRequest.create();
                        req.setInterval(10000);
                        req.setFastestInterval(5000);
                        req.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                        locCallback=new LocationCallback(){
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                if(locationResult==null)
                                    return;
                                lastLoc=locationResult.getLastLocation();
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLoc.getLatitude(),lastLoc.getLongitude()),15));
                                locClient.removeLocationUpdates(locCallback);

                            }
                        };

                        locClient.requestLocationUpdates(req,locCallback,null);
                    }
                }
                else
                    Toast.makeText(MapsActivity.this,"Unable to get your location!",Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 27 && resultCode==RESULT_OK) {
            getCurrentLoc();
        }
    }

}
