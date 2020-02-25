package com.example.mysul;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mysul.Model.uploadHealth;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class HealthForm extends AppCompatActivity {
    EditText name_ED, date_ED, weight_ED, height_ED, medicine_ED, illness_ED, skin_ED, blood_ED;
    String name, date, weight, height, medicine, illness, skin, blood;
    Button done_BT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_health_form);

        name_ED = findViewById(R.id.editText3);
        date_ED = findViewById(R.id.editText5);
        weight_ED = findViewById(R.id.editText8);
        height_ED = findViewById(R.id.editText9);
        medicine_ED = findViewById(R.id.editText10);
        illness_ED = findViewById(R.id.editText11);
        skin_ED = findViewById(R.id.editText);
        blood_ED = findViewById(R.id.editText6);
        done_BT = findViewById(R.id.button6);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String mUserName = firebaseUser.getDisplayName();

        name_ED.setText(mUserName);

        TextWatcher tw = new TextWatcher() {
            private String current = new String();
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();


            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // If  the edit text not equal to empty
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length());
                    } else {
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day = Integer.parseInt(clean.substring(0, 2));
                        int mon = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));

                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon - 1);
                        year = (year < 1900) ? 1900 : (year > 2020) ? 2020 : year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                        clean = String.format("%02d%02d%02d", day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    date_ED.setText(current);
                    date_ED.setSelection(sel < current.length() ? sel : current.length());
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        date_ED.addTextChangedListener(tw);

        done_BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = name_ED.getText().toString();
                date = date_ED.getText().toString();
                weight = weight_ED.getText().toString();
                height = height_ED.getText().toString();
                medicine = medicine_ED.getText().toString();
                illness = illness_ED.getText().toString();
                skin = skin_ED.getText().toString();
                blood = blood_ED.getText().toString();

                if (name.isEmpty() || date.isEmpty() || weight.isEmpty() || height.isEmpty() || medicine.isEmpty() || illness.isEmpty() || skin.isEmpty() || blood.isEmpty()) {

                    Toast.makeText(HealthForm.this, "The fields are not allowed to be empty", Toast.LENGTH_SHORT).show();
                } else {
                    uploadHealth obj = new uploadHealth(name, date, weight, height, medicine, illness, skin, blood);
                    upload(obj);

                }

            }
        });
    }

    private void upload(uploadHealth obj) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        String mUserID = FirebaseAuth.getInstance().getUid();

        DatabaseReference databaseReference = firebaseDatabase.getReference().child("User").child(mUserID).child("InformationFrom");
        databaseReference.setValue(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(HealthForm.this, "Success", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HealthForm.this, ContactActivity.class);
                intent.putExtra("FROM", "Health");
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String mUserID = firebaseUser.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(mUserID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("InformationFrom")) {
                    showHealth();

                    if (!dataSnapshot.hasChild("Contact")) {
                        Intent intent = new Intent(HealthForm.this, ContactActivity.class);
                        startActivity(intent);
                    }
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showHealth() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String mUserID = firebaseUser.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(mUserID).child("InformationFrom");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String blood = dataSnapshot.child("blood").getValue().toString();
                String date = dataSnapshot.child("date").getValue().toString();
                String height = dataSnapshot.child("height").getValue().toString();
                String illness = dataSnapshot.child("illness").getValue().toString();
                String medicine = dataSnapshot.child("medicine").getValue().toString();
                String name = dataSnapshot.child("name").getValue().toString();
                String skin = dataSnapshot.child("skin").getValue().toString();
                String weight = dataSnapshot.child("weight").getValue().toString();

                name_ED.setText(name);
                date_ED.setText(date);
                weight_ED.setText(weight);
                height_ED.setText(height);
                medicine_ED.setText(medicine);
                illness_ED.setText(illness);
                skin_ED.setText(skin);
                blood_ED.setText(blood);
                done_BT.setText("Update");


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

