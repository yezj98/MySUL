package com.example.mysul;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.mysul.Model.uploadLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private boolean locationGranted = false;
    private double currentLatitude, currentLongtitude;
    FirebaseUser firebaseUser;
    private String FINELOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private String COARSELOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private FusedLocationProviderClient fusedLocationProviderClient;
    int zoom = 20;
    private int PERMISSION_CODE = 123;
    private static final int REQUEST_CODE_SPEECH = 123;
    private final static int CALLING_CODE = 111;
    private final static int PERMISSIONS_REQUEST_SMS = 0;
    String[] SMSPERMISSION = {Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE};
    EditText title_ED;
    String number;
    int i, counter = 0;
    Button mSendLocation;
    private String message;
    MediaPlayer mediaPlayer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        title_ED = findViewById(R.id.editText12);
        mSendLocation = findViewById(R.id.sendLocation);
        mSendLocation.setVisibility(View.VISIBLE);
        title_ED.setVisibility(View.VISIBLE);
        permission();
        callingPermission();

        content();

        ImageButton imageButton = findViewById(R.id.voiceButton);
        imageButton.setVisibility(View.VISIBLE);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speak();

            }
        });

        mSendLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                i ++;
                Runnable runnable = new Runnable() {

                    @Override
                    public void run() {
                        i = 0;

                    }
                };

                if (i == 1) {
                    final String mcarNumber = title_ED.getText().toString();
                    Log.d("gg", "" + mcarNumber);


                    if (!mcarNumber.isEmpty()) {
                        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        String userID = currentFirebaseUser.getUid();

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(userID).child("Contact");

                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                String number = dataSnapshot.child("phoneNumber").getValue().toString();
                                String secondNumber = dataSnapshot.child("secondphoneNumber").getValue().toString();
                                String thridnumber = dataSnapshot.child("thridphoneNumber").getValue().toString();
                                Log.v("Number", number);
                                createObject();
                                sendCar(number, secondNumber, thridnumber, mcarNumber);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } else {
                        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        String userID = currentFirebaseUser.getUid();

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(userID).child("Contact");

                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                String number = dataSnapshot.child("phoneNumber").getValue().toString();
                                String secondNumber = dataSnapshot.child("secondphoneNumber").getValue().toString();
                                String thridnumber = dataSnapshot.child("thridphoneNumber").getValue().toString();
                                Log.v("Number", number);
                                createObject();
                                sendSMS(number, secondNumber, thridnumber);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
                else if (i == 2) {
                    alarm();
                    i = 0;
                    Toast.makeText(MapsActivity.this, "Alarm", Toast.LENGTH_LONG).show();
                    AlertDialog dialog = new AlertDialog.Builder(MapsActivity.this).setTitle("Stop the alarm").setPositiveButton("Stop", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            stopMedia();
                        }
                    }).setNegativeButton("Emergency calling", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MapsActivity.this, "Calling", Toast.LENGTH_SHORT).show();
                            stopMedia();
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:911"));
                            startActivity(intent);

                        }
                    }).show();
                }

            }
        });


    }

    private void alarm() {
        mediaPlayer = MediaPlayer.create(this, R.raw.song);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
    }

    public void stopMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
    }

    public void permission() {
        String[] isPermissionGranted = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINELOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSELOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationGranted = true;
                currentLocation();
            } else {
                ActivityCompat.requestPermissions(MapsActivity.this, isPermissionGranted, PERMISSION_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(MapsActivity.this, isPermissionGranted, PERMISSION_CODE);
        }
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
            Toast.makeText(MapsActivity.this, "Location Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void speak() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);// Given an hint to the recognizer about what the user is going to say
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-SG");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak something"); // the title below the google speech icon

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH);

        } catch (Exception e) {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_SPEECH: {
                if (requestCode == REQUEST_CODE_SPEECH && null != data) {
                    //get text array from voice intent
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    title_ED.setText((result.get(0)));
                }
                break;
            }
        }
    }

    private void sendSMS(String number, String secondNumber, String thridNumber) {// method for asking the SMS permission and READ phone state
        String googleLink = "https://maps.google.com/?q=" + currentLatitude + "," + currentLongtitude;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                ActivityCompat.requestPermissions(this, SMSPERMISSION, PERMISSION_CODE); // if not allowed, ask user for permission
            } else {
                ActivityCompat.requestPermissions(this, SMSPERMISSION, PERMISSION_CODE); // if not allowed, ask user for permission
            }
        } else {
            message = "My current location +\n" + "Latitude:" + currentLatitude + "\n" + "Longtitude:" + currentLongtitude + "\n Google Map link"
                    + "\n" + googleLink; //default sms content
            SmsManager smsManager = SmsManager.getDefault(); // initialise the sms manager API
            smsManager.sendTextMessage(number, null, message, null, null);
            smsManager.sendTextMessage(secondNumber, null, message, null, null);
            smsManager.sendTextMessage(thridNumber, null, message, null, null);// declare the phone number and message content
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();// toast for showing the message is success sent
        }
    }

    private void createObject() {
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userID = currentFirebaseUser.getUid();
        Log.v("Lat", "" + currentLatitude);
        Log.v("Long", "" + currentLongtitude);
        uploadLocation obj = new uploadLocation(currentLatitude, currentLongtitude, userID);
        uploadpost(obj);
    }


    private void uploadpost(uploadLocation obj) {
        String userID = FirebaseAuth.getInstance().getUid(); //get the firebase user account ID
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("User").child(userID).child("CurrentLocation");//  declare the path of database

        databaseReference.setValue(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) { //put the parameter of the obj class
                //show the toast when the update is success
            }
        });
    }

    private void sendCar (String number, String secondNumber, String thridNumber, String car) {// method for asking the SMS permission and READ phone state
        String googleLink = "https://maps.google.com/?q=" + currentLatitude + "," + currentLongtitude +"\n" + "Vehicle registration plate is:" + car;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                ActivityCompat.requestPermissions(this, SMSPERMISSION, PERMISSION_CODE); // if not allowed, ask user for permission
            } else {
                ActivityCompat.requestPermissions(this, SMSPERMISSION, PERMISSION_CODE); // if not allowed, ask user for permission
            }
        } else {
            message = "My current location +\n" + "Latitude:" + currentLatitude + "\n" + "Longtitude:" + currentLongtitude + "\n Google Map link"
                    + "\n" + googleLink; //default sms content
            SmsManager smsManager = SmsManager.getDefault(); // initialise the sms manager API
            smsManager.sendTextMessage(number, null, message, null, null);
            smsManager.sendTextMessage(secondNumber, null, message, null, null);
            smsManager.sendTextMessage(thridNumber, null, message, null, null);// declare the phone number and message content
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();// toast for showing the message is success sent
        }
    }

    private void callingPermission() {
        final String[] permission = {Manifest.permission.CALL_PHONE}; //Array for store the 2 permissions
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MapsActivity.this, permission, CALLING_CODE);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this, Manifest.permission.CALL_PHONE)) {
                AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Please accept the permission").setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MapsActivity.this, permission, CALLING_CODE);
                    }
                }).setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MapsActivity.this, "User deny", Toast.LENGTH_SHORT).show();
                    }
                }).show();
            }

        }
    }

    private void content () {
        counter ++;
        createObject();

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
