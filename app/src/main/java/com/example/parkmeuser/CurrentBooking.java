package com.example.parkmeuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CurrentBooking extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private ArrayList<Model> mlist;
    private List<String> idList;
    String userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_booking);
        userid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        DatabaseReference root =FirebaseDatabase.getInstance().getReference(userid).child("Booking");
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mlist = new ArrayList<>();
       adapter = new MyAdapter(this,mlist);
        recyclerView.setAdapter(adapter);

           root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList = new ArrayList<>();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {

                    idList.add(  dataSnapshot.child("id").getValue().toString());
                }
                Fect((ArrayList<String>) idList);
           //
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    public void Fect(ArrayList<String> idList)
    {
        int z= idList.size();

            DatabaseReference root= FirebaseDatabase.getInstance().getReference("Booking");
            root.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mlist.clear();
                    for(DataSnapshot dataSnapshot:snapshot.getChildren())
                    {

                        //vehicle = dataSnapshot.child("Vehicle").getValue().toString();

                        int index= Collections.binarySearch(idList,dataSnapshot.getKey());
                        if(index>=0)
                        {
                            Model model = new Model();
                            if(!dataSnapshot.child("BookingStatus").getValue().toString().equals("Canceled"))
                            {

                                    model.setBookingStatus(dataSnapshot.child("BookingStatus").getValue().toString());
                                model.setDateTime(dataSnapshot.child("DateTime").getValue().toString());
                                model.setVehicle(dataSnapshot.child("Vehicle").getValue().toString());
                                model.setPayment(dataSnapshot.child("Payment").getValue().toString());
                                model.setId(dataSnapshot.getKey().toString());
                                mlist.add(model);
                            }

                        }
                        //Log.d("viplulx",dataSnapshot.getKey());


                    }
                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


    }
}