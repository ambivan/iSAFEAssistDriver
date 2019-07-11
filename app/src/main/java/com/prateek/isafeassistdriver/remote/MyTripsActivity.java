package com.prateek.isafeassistdriver.remote;

import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prateek.isafeassistdriver.MainActivity;
import com.prateek.isafeassistdriver.R;
import com.prateek.isafeassistdriver.dao.TripDetails;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyTripsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TripAdapter adapter;
    List<TripDetails> list = new ArrayList<>();
    ProgressDialog dialog;
    FirebaseAuth auth;
    TextView textView;
    Toolbar toolbar;

    Geocoder geocoder;
    DatabaseReference reference, databaseReference;
    List<Address> addresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trips);
        recyclerView = findViewById(R.id.triprecycler);
        dialog = new ProgressDialog(MyTripsActivity.this);
        textView = findViewById(R.id.texttohide);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbartrips);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Past Trips");

        Window window = MyTripsActivity.this.getWindow();
// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(MyTripsActivity.this, R.color.mystatus));
        auth = FirebaseAuth.getInstance();
        geocoder = new Geocoder(MyTripsActivity.this, Locale.getDefault());

        databaseReference = FirebaseDatabase.getInstance().getReference();
        dialog.setMessage("Loading Previous trip Details...");
        dialog.setCancelable(false);
        dialog.show();

        loaddetails(recyclerView);

        System.out.println("acjnakcnkajnkjand");
    }

    private void loaddetails(final RecyclerView recyclerView) {
        databaseReference.child("Requests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue()==null){
                    dialog.dismiss();
                }

                for (DataSnapshot ds : dataSnapshot.getChildren()) {


                        System.out.println("keyyysssssss" + ds.getKey());
                        String uname = ds.child("name").getValue(String.class);
                        String price = "₹ 500/-";
                        String ucontact = ds.child("contactNo").getValue(String.class);
                        String time = ds.child("time").getValue(String.class);
                        String did = ds.child("did").getValue(String.class);
                        String ulat = ds.child("lat").getValue(String.class);
                        String ulong = ds.child("longi").getValue(String.class);

                        if (did.equals(auth.getCurrentUser().getUid().toString())) {
                            try {
                                addresses = geocoder.getFromLocation(Double.parseDouble(ulat), Double.parseDouble(ulong), 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            System.out.println(uname + ucontact + time + did + ulat + ulong);

                            String add = addresses.get(0).getAddressLine(0);

                            TripDetails details = new TripDetails();
                            if (uname.length() > 0 && uname != null) {

                                details.setPrice(price);
                                details.setUsername(uname);
                                details.setUserphone(ucontact);
                                details.setLocateservice(add);
                                details.setTimeservice(time);
                                list.add(details);
                            }

                            System.out.println(list.size());
                            System.out.println(list);
                            if (list == null) {

                                dialog.dismiss();

                            } else {
                                textView.setVisibility(View.GONE);
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setLayoutManager(new LinearLayoutManager(MyTripsActivity.this, LinearLayoutManager.VERTICAL, false));

                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                adapter = new TripAdapter(MyTripsActivity.this, list);
                                recyclerView.setAdapter(adapter);
                                dialog.dismiss();

                            }



                    }else{
                            dialog.dismiss();
                            textView.setVisibility(View.VISIBLE);
                        }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("Towing Requests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    System.out.println("keyyysssssss" + ds.getKey());
                    String uname = ds.child("uname").getValue(String.class);
                    String price = "₹ 1200/-";
                    String ucontact = ds.child("ucontact").getValue(String.class);
                    String time = ds.child("time").getValue(String.class);
                    String did = ds.child("did").getValue(String.class);
                    String ulat = ds.child("startlat").getValue(String.class);
                    String ulong = ds.child("startlong").getValue(String.class);

                    if (did.equals(auth.getCurrentUser().getUid().toString())) {
                        try {
                            addresses = geocoder.getFromLocation(Double.parseDouble(ulat), Double.parseDouble(ulong), 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println(uname + ucontact + time + did + ulat + ulong);

                        String add = addresses.get(0).getAddressLine(0);

                        TripDetails details = new TripDetails();
                        if (uname.length() > 0 && uname != null) {

                            details.setPrice(price);
                            details.setUsername(uname);
                            details.setUserphone(ucontact);
                            details.setLocateservice(add);
                            details.setTimeservice(time);
                            list.add(details);
                        }

                        System.out.println(list.size());
                        System.out.println(list);
                        if (list == null) {

                            dialog.dismiss();

                        } else {
                            textView.setVisibility(View.GONE);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(MyTripsActivity.this, LinearLayoutManager.VERTICAL, false));

                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            adapter = new TripAdapter(MyTripsActivity.this, list);
                            recyclerView.setAdapter(adapter);
                            dialog.dismiss();

                        }


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
