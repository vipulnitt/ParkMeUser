package com.example.parkmeuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class Profile extends AppCompatActivity {
     String UserId;
     TextView name,email,mobileno;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        name= findViewById(R.id.profilename);
        email = findViewById(R.id.profileemail);
        mobileno = findViewById(R.id.profilecontact);
        UserId = FirebaseAuth.getInstance().getUid().toString();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference db = firestore.collection("appusers").document(UserId);
        db.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                mobileno.setText(document.getString("mobileno"));
                email.setText(document.getString("email"));
                name.setText((document.getString("name")));
            }
        });
    }
}