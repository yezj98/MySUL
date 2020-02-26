package com.example.mysul;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


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
                mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

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



}
