package com.example.mysul;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.telecom.RemoteConference;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Locale;


public class MenuActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SPEECH = 123;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    TextView title_TV;
    ImageButton voice_BTN;
    ImageView profile;

    String[] SMSPERMISSION = {Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE};
    public static final int PERMISSION_CODE = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        title_TV = findViewById(R.id.title_TV);
        voice_BTN = findViewById(R.id.voiceButton);
        drawerLayout = findViewById(R.id.drawer_layout);


        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        sendSMS();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(MenuActivity.this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);
        profile = headerView.findViewById(R.id.imageView3);

        Glide.with(this).load(firebaseUser.getPhotoUrl()).into(profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MenuActivity.this, profilePicture.class);
                intent.putExtra("FROM", "Menu");
                startActivity(intent);
            }
        });


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MapsFragmentActivity()).commit();
            voice_BTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    speak();
                }
            });

        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.drawer_location) {
                    Intent intent = new Intent(MenuActivity.this, MapsActivity.class);
                    startActivity(intent);
                }

                if (id == R.id.signOut) {
                    AuthUI.getInstance().signOut(MenuActivity.this).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }

                if (id == R.id.drawer_friend) {
                    Intent intent = new Intent(MenuActivity.this, PeopleActivity.class);
                    startActivity(intent);
                }

                if (id == R.id.drawer_contact) {
                    Intent intent = new Intent(MenuActivity.this, ContactActivity.class);
                    intent.putExtra("FROM", "Menu");
                    startActivity(intent);
                }

                if (id == R.id.health) {
                    Intent intent = new Intent(MenuActivity.this, HealthForm.class);
                    startActivity(intent);
                }

                return false;
            }
        });
    }

//    private void speak() {
//        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
////        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);// Given an hint to the recognizer about what the user is going to say
//        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-SG");
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak something"); // the title below the google speech icon
//
//        try {
//            startActivityForResult(intent, REQUEST_CODE_SPEECH);
//
//        } catch (Exception e) {
//            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        switch (requestCode) {
//            case REQUEST_CODE_SPEECH: {
//                if (requestCode == REQUEST_CODE_SPEECH && null != data) {
//                    //get text array from voice intent
//                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                    title_TV.setText((result.get(0)));
//                }
//                break;
//            }
//        }
//    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void sendSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {

            } else {
                ActivityCompat.requestPermissions(this, SMSPERMISSION, PERMISSION_CODE);
            }
        }
    }

//    private void sendSMS(String number, String secondNumber, String content) {// method for asking the SMS permission and READ phone state
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
//                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
//                ActivityCompat.requestPermissions(this, SMSPERMISSION, PERMISSION_CODE); // if not allowed, ask user for permission
//            } else {
//                ActivityCompat.requestPermissions(this, SMSPERMISSION, PERMISSION_CODE); // if not allowed, ask user for permission
//            }
//
//        } else {
//           String message = "My current location +\n" + "Latitude:" + currentLatitude + "\n" + "Longtitude:" + currentLongtitude + "\n Google Map link"
//                    + "\n" + googleLink; //default sms content
//            SmsManager smsManager = SmsManager.getDefault(); // initialise the sms manager API
//            smsManager.sendTextMessage(number, null, content, null, null);
//            smsManager.sendTextMessage(secondNumber, null, con, null, null);// declare the phone number and message content
//            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();// toast for showing the message is success sent
//        }
}
