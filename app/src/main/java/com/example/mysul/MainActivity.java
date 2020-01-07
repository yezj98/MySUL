package com.example.mysul;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 123;
    private static final int RESULT_CODE = 12;
    List<AuthUI.IdpConfig> provider;
    FirebaseUser firebaseUser;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        provider = Arrays.asList(
//                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build()
//                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        showSignIn();
    }

    private void showSignIn() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(provider).setTheme(R.style.AppTheme)
                        .build(), REQUEST_CODE
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_CODE) {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                Toast.makeText(this, "" + firebaseUser.getEmail(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, Menu.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "" + response.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.v("Onstart:", "mainact");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            // If user is already logged in upon opening, go to home intent
            Intent intent = new Intent(this, Menu.class);
            startActivity(intent);
            finish();
        } else {
        }

    }

}



