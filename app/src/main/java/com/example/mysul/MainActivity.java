package com.example.mysul;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.mysul.Model.User;
import com.example.mysul.utils.Common;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.Arrays;
import java.util.List;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 123;
    private static final int MY_REQUEST_OK = 111;
    List<AuthUI.IdpConfig> provider;
    FirebaseUser firebaseUser;
    DatabaseReference user_information;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Paper.init(this);
        user_information = FirebaseDatabase.getInstance().getReference(Common.USER_INFORMATION);
        provider = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        //request the permissions
        Dexter.withActivity(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                signInOptions();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(MainActivity.this, "Please accept the permission", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

            }
        }).check();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE){
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK){
                final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                user_information.orderByKey().equalTo(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null){
                            if (!dataSnapshot.child(firebaseUser.getUid()).exists()) {
                                Common.loggeduser = new User (firebaseUser.getUid(),firebaseUser.getEmail());

                                //add to database
                                user_information.child(Common.loggeduser.getUid()).setValue(Common.loggeduser);
                            }
                        }
                        else {
                            Common.loggeduser = dataSnapshot.child(firebaseUser.getUid()).getValue(User.class);
                        }

                        Paper.book().write(Common.USER_UID_SAVE_KEY,Common.loggeduser.getUid());
                        updateToken (firebaseUser);
                        setupUI();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    private void setupUI() {
        startActivity(new Intent(MainActivity.this, MenuActivity.class));
        finish();
    }

    private void updateToken(final FirebaseUser firebaseUser) { //update the token
        final DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(Common.TOKENS);
        //Get token
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() { //Do whatever you want with your token now
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                tokens.child(firebaseUser.getUid()).setValue(instanceIdResult.getToken());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signInOptions() {
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                .setIsSmartLockEnabled(false)
                .setAvailableProviders(provider)
                .setTheme(R.style.AppTheme).build(), REQUEST_CODE);


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseUser != null) {
            // If user is already logged in upon opening, go to home intent
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
            finish();
        } else {
        }

    }
}



