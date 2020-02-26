package com.example.mysul;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CurrentLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    int counter = 0;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    String mUserID;
    String latitude, longtitude;
    Double latitudeX, longtitudeX;
    LatLng sydney;

    private String FINELOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private String COARSELOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private double currentLatitude, currentLongtitude;
    private boolean locationGranted = false;
    private int PERMISSION_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        permission();


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
        mMap = googleMap;
        content();
        mMap.setMyLocationEnabled(true);


        // Add a marker in Sydney and move the camera

    }


    private void getLocation () {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserID = firebaseUser.getUid();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(mUserID).child("CurrentLocation");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 latitude = dataSnapshot.child("wayLatitude").getValue().toString();
                 longtitude = dataSnapshot.child("wayLongtitude").getValue().toString();

                Log.d ("llll" , "" + latitude);


                latitudeX = Double.parseDouble(latitude);
                longtitudeX = Double.parseDouble(longtitude);
                Log.d ("lll" , "" + latitudeX);

                sydney = new LatLng(latitudeX, longtitudeX);
                mMap.addMarker(new MarkerOptions().position(sydney).title("HERE!"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18), 1, null);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void content () {
        counter ++;
        getLocation();

        refresh (10000);

    }

    private void refresh(int i) {

        final Handler handler = new Handler();
        final  Runnable runnable = new Runnable() {
            @Override
            public void run() {
                content();
            }
        };
        handler.postDelayed(runnable, i);
    }

    private void currentLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (locationGranted) {
            final Task location = fusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    Location currentLocation = (Location) task.getResult();
                    currentLatitude = currentLocation.getLatitude();
                    currentLongtitude = currentLocation.getLongitude();
                    LatLng currentLatLng = new LatLng(currentLatitude, currentLongtitude);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18), 1, null);
                }
            });
        }
        else {
            Toast.makeText(CurrentLocationActivity.this, "Location Failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void permission() {
        String[] isPermissionGranted = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINELOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSELOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationGranted = true;
                currentLocation();
            } else {
                ActivityCompat.requestPermissions(CurrentLocationActivity.this, isPermissionGranted, PERMISSION_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(CurrentLocationActivity.this, isPermissionGranted, PERMISSION_CODE);
        }
    }



}
