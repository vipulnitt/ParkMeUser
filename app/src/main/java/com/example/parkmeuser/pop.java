package com.example.parkmeuser;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class pop extends AppCompatDialogFragment {
    private popListner listner;
    private TextView info;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.pop,null);
        Bundle bundle = getArguments();
        String owner = bundle.getString("owner");

        //DatabaseReference db = FirebaseDatabase.getInstance().getReference("Owners").child(owner);

       // Log.d("vipulz",s);
        builder.setView(view)
                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Book Now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                     listner.sendText(owner,1);
                    }
                });
        info = view.findViewById(R.id.info);
        String pname,two,four;

        pname = bundle.getString("pname","");
        two = bundle.getString("two","0");
        four = bundle.getString("four","0");
        Log.d("vipulll",""+pname);
        info.setText("Parking Name:"+pname+"\nSlots:\nTwo Wheeler:"+two+"\nFour Wheeler:"+four);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listner = (popListner) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+"Implement listner");
        }
    }

    public interface popListner{
        void sendText(String userid,int book);
    }
}
