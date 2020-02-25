package com.example.mysul;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mysul.Model.post;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ContactActivity extends AppCompatActivity {

    EditText firstNumber, secondNumber, thridNumber;
    Button upload;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    String number1, number2, number3, userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        firstNumber = findViewById(R.id.editText2);
        secondNumber = findViewById(R.id.editText4);
        thridNumber = findViewById(R.id.editText7);
        upload = findViewById(R.id.button);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                number1 = firstNumber.getText().toString();
                number2 = secondNumber.getText().toString();
                number3 = thridNumber.getText().toString();

                if (number1.isEmpty() || number2.isEmpty() || number3.isEmpty()) {
                    Toast.makeText(ContactActivity.this, "Please enter the contact number", Toast.LENGTH_SHORT).show();
                }
                else {
                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    userID = FirebaseAuth.getInstance().getUid();
                    Toast.makeText(ContactActivity.this, "" + firebaseUser.getDisplayName() + "\n" +firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
                    post obj = new post(number1, number2, number3);
                    upload(obj);

                }
            }
        });
    }

    private void upload (post obj) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        userID = FirebaseAuth.getInstance().getUid();

        databaseReference = firebaseDatabase.getReference().child("User").child(userID).child("Contact");

        databaseReference.setValue(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ContactActivity.this, "Success", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ContactActivity.this, MenuActivity.class);
                startActivity(intent);

            }
        });
    }




}
