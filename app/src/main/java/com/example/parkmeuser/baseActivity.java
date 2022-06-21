package com.example.parkmeuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class baseActivity extends AppCompatActivity implements pop.popListner {

    private FusedLocationProviderClient client;
    private SupportMapFragment mapFragment;
    private ConnectivityManager manager;
    private NetworkInfo networkInfo;
    private GoogleMap map;
    private double selectedLat,selectedLong;
    FirebaseFirestore firestore;
   ActionBarDrawerToggle actionBarDrawerToggle;
   DrawerLayout drawerLayout;
   FirebaseAuth firebaseAuth;
    String userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        drawerLayout = findViewById(R.id.dl);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout, R.string.Open, R.string.Close);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firebaseAuth= FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userid =firebaseAuth.getCurrentUser().getUid();
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        client = LocationServices.getFusedLocationProviderClient(this);
        permission();

        final NavigationView navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                 if(id==R.id.home)
                 {
                     //home
                 }
                if(id==R.id.profile){
                    //Profile
                }
                if(id==R.id.preBooking)
                {
                    startActivity(new Intent(getApplicationContext(),CurrentBooking.class));
                }
                 if(id==R.id.logout)
                 {
                     firebaseAuth.signOut();
                     startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                     finish();
                 }
                return true;
            }
        });
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return actionBarDrawerToggle.onOptionsItemSelected(item)||super.onOptionsItemSelected(item);
    }
    public void permission() {
        Dexter.withContext(this).withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
               getCurrentLocation();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null)
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            map = googleMap;
                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Your Current Location!");
                            selectedLat = latLng.latitude;
                            selectedLong = latLng.longitude;
                            nearestParkings(googleMap);

                           // getAddress(latLng.longitude,latLng.latitude);
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));
                            googleMap.addMarker(markerOptions).showInfoWindow();
                            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                @Override
                                public void onMapClick(@NonNull LatLng latLng) {
                                    checkConnectivity();
                                    if(networkInfo.isConnected()&&networkInfo.isAvailable())
                                    {
                                        selectedLat = latLng.latitude;
                                        selectedLong = latLng.longitude;

                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(), "Please Check Internet Connectivity!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });

            }
        });
    }
    private void checkConnectivity()
    {
        manager = (ConnectivityManager)getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        networkInfo = manager.getActiveNetworkInfo();
    }
    public  void nearestParkings(GoogleMap map)
    {
        final GeoLocation center = new GeoLocation(selectedLat, selectedLong);
        final double radiusInM = 10 * 1000;
        List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM);
        final List<Task<QuerySnapshot>> tasks = new ArrayList<>();
        for (GeoQueryBounds b : bounds) {
            Query q = firestore.collection("parking")
                    .orderBy("geohash")
                    .startAt(b.startHash)
                    .endAt(b.endHash);

            tasks.add(q.get());
        }
        Tasks.whenAllComplete(tasks)
                .addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Task<?>>> t) {

                        List<DocumentSnapshot> matchingDocs = new ArrayList<>();
                        for (Task<QuerySnapshot> task : tasks) {
                            QuerySnapshot snap = task.getResult();
                            for (DocumentSnapshot doc : snap.getDocuments()) {
                                double lat = doc.getDouble("lat");
                                double lng = doc.getDouble("lng");

                                // We have to filter out a few false positives due to GeoHash
                                // accuracy, but most will match
                                GeoLocation docLocation = new GeoLocation(lat, lng);
                                double distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center);
                                if (distanceInM <= radiusInM) {
                                    matchingDocs.add(doc);
                                }
                            }
                        }
                        Bitmap image = BitmapFactory.decodeResource(getResources(),R.drawable.marker);
                        for(int i=0;i<matchingDocs.size();i++)
                        {
                            LatLng latLng= new LatLng(matchingDocs.get(i).getDouble("lat"),matchingDocs.get(i).getDouble("lng"));
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Parking"+(i+1));
                            map.addMarker(markerOptions).setIcon(BitmapDescriptorFactory.fromBitmap(image));
                        }
                        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(@NonNull Marker marker) {
                               String hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(marker.getPosition().latitude,marker.getPosition().longitude));
                                Log.d("vipulxy",""+matchingDocs.size());

                              pop(hash,matchingDocs);
                                return false;
                            }
                        });
                      // Log.d("Address",""+matchingDocs.get(0).getDouble("lat"));
                        // matchingDocs contains the results
                        // ...
                    }
                });
    }

    @Override
    public void sendText(String userid,int book) {
        Intent intent = new Intent(this,Booknow.class);
        intent.putExtra("ownerid",userid);
     startActivity(intent);
    }
    void pop(String hash,List<DocumentSnapshot> matchingDocs )
    {
        String[] user= new String[1];
        for(int i=0;i<matchingDocs.size();i++)
        {

            String temp=matchingDocs.get(i).getString("geohash");
            if(!hash.equals(temp))
            {
                Log.d("vipul",""+temp.equals(hash));
                continue;
            }

            if(hash.equals(temp));
            {
                Log.d("vipul",""+temp.equals(hash));
                user[0]=matchingDocs.get(i).getString("userid");
                break;
            }
        }
        if(!user.equals(""))
        {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("INFO",Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = pref.edit();
            editor.putString("user",user[0]);
            editor.apply();
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("Owners").child(user[0]);

            db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String pname,two,four;

                    pname=snapshot.child("pname").getValue().toString();
                    two = snapshot.child("Slots").child("Two").getValue().toString();
                    four =snapshot.child("Slots").child("Four").getValue().toString();
                    Log.d("vipulx","pname"+pname);

                    Bundle bundle = new Bundle();
                    bundle.putString("pname",pname);
                    bundle.putString("two",two);
                    bundle.putString("four",four);
                    bundle.putString("owner",user[0]);

                    /*SharedPreferences pref = getApplicationContext().getSharedPreferences("INFO",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("two",two);
                    editor.putString("four",four);
                    editor.putString("pname",pname);
                    editor.apply();*/

                    pop p = new pop();
                    p.setArguments(bundle);
                    p.show(getSupportFragmentManager(),"pop");

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }
}