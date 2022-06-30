package com.example.parkmeuser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter <MyAdapter.MyViewHolder>{
    ArrayList<Model> mList;
    Context context;
    public MyAdapter(Context context,ArrayList<Model> mList)
    {
        this.mList=mList;
        this.context=context;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.items,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Model model = mList.get(position);
        holder.vehicle.setText(model.getVehicle());
        holder.pmode.setText(model.getPayment());
        holder.status.setText(model.getBookingStatus());
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        holder.datetime.setText(sfd.format(new Date(Long.parseLong(model.getDateTime()))));
        holder.id.setText(model.getId());
        holder.otp.setText(model.getOtp());
        if (holder.status.getText().equals("Confirmed")) {
            holder.status.setTextColor(Color.rgb(0, 153, 0));
        }

        if(model.getBookingStatus().equals("Canceled"))
        {
            Log.d("vipulxx",model.getBookingStatus());
            holder.cancelbtn.setVisibility(View.GONE);
        }

        if(model.getBookingStatus().equals("CheckedOut"))
        {
            holder.cancelbtn.setText("Details");
            holder.cancelbtn.setBackgroundColor(Color.rgb(246,190,0));;
            holder.cancelbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context,BillPayment.class);
                    intent.putExtra("Id",model.getId());
                    context.startActivity(intent);
                }
            });
        }
        String[] lat=new String[1];
        String[] lon=new String[1];
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Booking").child(holder.id.getText().toString());
        db.child("OwnerId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DatabaseReference dc = FirebaseDatabase.getInstance().getReference(snapshot.getValue().toString());
                dc.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        lat[0]= snapshot.child("Latitude").getValue().toString();
                        lon[0] = snapshot.child("Longitude").getValue().toString();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.trackLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String geoUri = "http://maps.google.com/maps?q=loc:" + lat[0] + "," + lon[0] + " (" + "Parking" + ")";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static  class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView vehicle,datetime,status,pmode,id,otp;
        Button cancelbtn,trackLocation;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            vehicle= itemView.findViewById(R.id.vehicle);
            datetime= itemView.findViewById(R.id.datetime);
            status =itemView.findViewById(R.id.status);
            pmode = itemView.findViewById(R.id.paymentMode);
            id = itemView.findViewById(R.id.bookingId);
            otp =itemView.findViewById(R.id.otp);
            cancelbtn =itemView.findViewById(R.id.cancel);
            trackLocation = itemView.findViewById(R.id.trackLocation);
            trackLocation.setTag("40");
            trackLocation.setOnClickListener(this);
            cancelbtn.setTag(10);
            cancelbtn.setBackgroundColor(Color.rgb(237,0,8));
            cancelbtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {


            if(view.getId()==cancelbtn.getId()&&cancelbtn.getText().toString().equals("CANCEL"))
            {
                Log.d("vipulx",cancelbtn.getText().toString());

                   // cancelbtn.setText("CANCELED");
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference("Booking").child(id.getText().toString());

                    if(status.getText().equals("Confirmed"))
                    {
                     db.child("OwnerId").addValueEventListener(new ValueEventListener() {
                         @Override
                         public void onDataChange(@NonNull DataSnapshot snapshot) {
                             DatabaseReference dbs = FirebaseDatabase.getInstance().getReference(snapshot.getValue().toString());
                             boolean[] ck = {false};
                             if (vehicle.getText().equals("Two Wheeler")) {
                                 dbs.child("Slots").child("Two").addValueEventListener(new ValueEventListener() {
                                     @Override
                                     public void onDataChange(@NonNull DataSnapshot snapshot) {
                                         if (!ck[0]) {
                                             int x = Integer.parseInt(snapshot.getValue().toString());
                                             dbs.child("Slots").child("Two").setValue(x + 1);
                                             ck[0] = true;
                                         }

                                     }

                                     @Override
                                     public void onCancelled(@NonNull DatabaseError error) {

                                     }
                                 });


                             }
                             boolean[] lk={false};
                             Log.d("vipulxx",vehicle.getText().toString());
                             DatabaseReference dbf = FirebaseDatabase.getInstance().getReference(snapshot.getValue().toString());
                             if (vehicle.getText().equals("FourWheeler")) {

                                 dbf.child("Slots").child("Four").addValueEventListener(new ValueEventListener() {
                                     @Override
                                     public void onDataChange(@NonNull DataSnapshot snapshot) {
                                         if (!lk[0]) {

                                             int x = Integer.parseInt(snapshot.getValue().toString());
                                             dbf.child("Slots").child("Four").setValue(x +1);
                                             lk[0] = true;
                                         }

                                     }

                                     @Override
                                     public void onCancelled(@NonNull DatabaseError error) {

                                     }
                                 });

                             }
                         }

                         @Override
                         public void onCancelled(@NonNull DatabaseError error) {

                         }
                     });
                    }
                db.child("BookingStatus").setValue("Canceled");

            }
        }
    }

}
