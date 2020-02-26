package com.example.mysul;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mysul.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class profilePicture extends AppCompatActivity {

    final static int PERMISSIONCODE = 123;
    final static int REQUESTCODE = 123;
    String[] readPermission = {Manifest.permission.READ_EXTERNAL_STORAGE};
    Button upload;
    ImageView imageView;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture);
        getSupportActionBar().hide();

        imageView = findViewById(R.id.imageView5);
        upload = findViewById(R.id.button5);
        firebaseAuth = FirebaseAuth.getInstance();



        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    permission();
                } else {
                    openGallery();
                }
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uri == null) {
                    Toast.makeText(profilePicture.this, "Please complete the requirement ", Toast.LENGTH_SHORT).show();
                } else {
                    update(uri, firebaseAuth.getCurrentUser());
                    Toast.makeText(profilePicture.this, "Success", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    private void permission() {
        if (ContextCompat.checkSelfPermission(profilePicture.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {// check is the permission granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(profilePicture.this, Manifest.permission.READ_EXTERNAL_STORAGE)) { //if user deny the permission
                Toast.makeText(profilePicture.this, "please accept the permission", Toast.LENGTH_LONG).show();
                AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Please accept the permission").setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(profilePicture.this,
                                readPermission, PERMISSIONCODE);
                    }
                }).setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(profilePicture.this, "User deny", Toast.LENGTH_SHORT).show();
                    }
                }).show();
            } else { //if the user haven't set the permission yet
                ActivityCompat.requestPermissions(this, readPermission, PERMISSIONCODE);
            }
        } else {
            openGallery();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT); //open outside the application
        intent.setType("image/*"); // open the gallery
        startActivityForResult(intent, REQUESTCODE); // start the new intent, use the startActivityForResult for passing the data from another activity to current activity
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUESTCODE && resultCode == RESULT_OK && data != null) {
            uri = data.getData();
            imageView.setImageURI(uri);
        }
    }

    private void update(Uri image, final FirebaseUser firebaseUser) {

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("UserPhoto");
        final StorageReference path = storageReference.child(image.getLastPathSegment());
        path.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(uri)
                                .build();

                        firebaseUser.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(profilePicture.this, HealthForm.class);
                                    intent.putExtra("FROM","P");
                                    startActivity(intent);
                                    finish();

                                }
                            }
                        });

                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        final String previous  = intent.getStringExtra("FROM");


        if (previous.equals("Menu")) {


        }
        else if (previous.equals("Sign")){
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            String mUserID = firebaseUser.getUid();
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(mUserID);
            if (firebaseUser.getPhotoUrl() == null) {

            } else {
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("InformationFrom")) {
                            if (dataSnapshot.hasChild("Contact")) {
                                Intent intent = new Intent(profilePicture.this, MenuActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Intent intent = new Intent(profilePicture.this, HealthForm.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }


        }




    }
}
