package com.example.parkmeuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class Booknow extends AppCompatActivity {
    private TextView details;
    private RadioGroup group,paymentgroup;
    RadioButton radioTwo,radioFour;
    Button book;
    TextView fourView,twoView;
    TextView twoPrice,fourPrice;
    String counter="0";
    String userid;
    String parkingOwner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booknow);
        details = findViewById(R.id.details);
        group = findViewById(R.id.radioGroup);
        paymentgroup = findViewById(R.id.payment);
        book = findViewById(R.id.bookNow);
        fourView = findViewById(R.id.textView);
        twoView = findViewById(R.id.textView3);
        twoPrice = findViewById(R.id.twoPrice);
        fourPrice = findViewById(R.id.fourPrice);
         radioTwo =findViewById(R.id.twoRadio);
         radioFour = findViewById(R.id.fourRadio);
        parkingOwner=getIntent().getExtras().getString("ownerid");
        DatabaseReference db = FirebaseDatabase.getInstance().getReference(parkingOwner);
        userid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        DatabaseReference cdb =FirebaseDatabase.getInstance().getReference("counter");
        cdb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                counter=snapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

                Log.d("vipulll",counter);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

               String pname= snapshot.child("pname").getValue().toString();
               String address=snapshot.child("address").getValue().toString();
                String two=snapshot.child("Slots").child("Two").getValue().toString();
                String four=snapshot.child("Slots").child("Four").getValue().toString();
                String twop=snapshot.child("Price").child("two").getValue().toString();
                String fourp = snapshot.child("Price").child("four").getValue().toString();
                details.setText("Parking Name: "+pname+"\nAddress: "+address);
                fourView.setText("Available: "+four);
                twoView.setText("Available: "+two);
                twoPrice.setText(twop);
                fourPrice.setText(fourp);
                if(Integer.parseInt(two)<=0)
                {
                    radioTwo.setEnabled(false);
                }
                if(Integer.parseInt(four)<=0)
                {
                    radioFour.setEnabled(false);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
       book.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               int selectedId= group.getCheckedRadioButtonId();
               int paymentId= paymentgroup.getCheckedRadioButtonId();
               if(selectedId==-1)
               {
                   Toast.makeText(getApplicationContext(),"Please Select Vehicle Type!",Toast.LENGTH_SHORT).show();
               }else if(paymentId==-1)
               {
                   Toast.makeText(getApplicationContext(),"Please Payment Mode!",Toast.LENGTH_SHORT).show();
               }else
               {

                   RadioButton btn= group.findViewById(selectedId);
                   RadioButton pay = paymentgroup.findViewById(paymentId);
                   long otp=generatePin();
                   Log.d("vipulo",""+btn.getText().toString());

                   Log.d("vipulll",""+counter);
                   int c = Integer.parseInt(counter);
                   c++;
                   cdb.setValue(c);
                   DatabaseReference book = FirebaseDatabase.getInstance().getReference("Booking");
                   DatabaseReference user = FirebaseDatabase.getInstance().getReference(userid).child("Booking");
                   user.child(""+c).child("id").setValue(c);
                   Log.d("vipulo",pay.getText().toString());

                   book.child(String.valueOf(c)).child("UserId").setValue(userid);

                   book.child(String.valueOf(c)).child("DateTime").setValue(com.google.firebase.Timestamp.now().getSeconds()*1000);
                   book.child(String.valueOf(c)).child("Payment").setValue(pay.getText().toString());
                   book.child(String.valueOf(c)).child("Vehicle").setValue(btn.getText().toString());
                   book.child(String.valueOf(c)).child("BookingStatus").setValue("Pending");
                   book.child(String.valueOf(c)).child("OwnerId").setValue(parkingOwner);
                   book.child(String.valueOf(c)).child("otp").setValue(String.valueOf(otp));
                   if(btn.getText().toString().equals("FourWheeler"))
                   book.child(String.valueOf(c)).child("Price").setValue(fourPrice.getText().toString());
                   else
                       book.child(String.valueOf(c)).child("Price").setValue(twoPrice.getText().toString());
                 DatabaseReference d = FirebaseDatabase.getInstance().getReference();
                   d.child("OwnerBooking").child(parkingOwner).child(""+c).setValue(c);

                  startActivity(new Intent(getApplicationContext(),CurrentBooking.class));
                  finish();

               }

           }
       });
    }
    private static final Random generator = new Random();

    public static long generatePin() {
        return 100000 + generator.nextInt(900000);
    }
}