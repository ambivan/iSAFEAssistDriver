package com.prateek.isafeassistdriver.remote;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prateek.isafeassistdriver.MainActivity;
import com.prateek.isafeassistdriver.R;
import com.prateek.isafeassistdriver.dao.UserDetails;
import com.prateek.isafeassistdriver.navattr.FeedbackActivity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class UserRequestActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    AlertDialog dialogBuilder;

    FirebaseAuth auth;
    String uname, ucontact, ulat, ulong, requesting, distance;
    String endlat, endlong;
    Button endservice_btn;
    DatabaseReference databaseReference, reference;
    static String dname, dcontact;
    static String dlat, dlong, did;
    Geocoder geocoder;
    List<Address> addresses, destadd;
    static String add, finaladd;
    static String key, kkey;
    static int states = 0;
    static String abc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_request);

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2);
        mapFrag.getMapAsync(this);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        reference = FirebaseDatabase.getInstance().getReference();
        endservice_btn = findViewById(R.id.endservicebtn);
        final Bundle intent = getIntent().getExtras();
        String checkcs = intent.getString("cs");
        Window window = UserRequestActivity.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(UserRequestActivity.this, R.color.mystatus));


        geocoder = new Geocoder(UserRequestActivity.this, Locale.getDefault());


        databaseReference.child("Driver").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dname = dataSnapshot.child("name").getValue(String.class);
                dcontact = dataSnapshot.child("contact").getValue(String.class);
                dlat = dataSnapshot.child("latitude").getValue(String.class);
                dlong = dataSnapshot.child("longitude").getValue(String.class);
                did = dataSnapshot.child("did").getValue(String.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (checkcs.equals("0")) {
            //towing service code here

            reference.child("Towing Requests").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        uname = ds.child("uname").getValue(String.class);
                        ucontact = ds.child("ucontact").getValue(String.class);
                        ulat = ds.child("startlat").getValue(String.class);
                        endlat = ds.child("endlat").getValue(String.class);
                        endlong = ds.child("endlong").getValue(String.class);
                        ulong = ds.child("startlong").getValue(String.class);
                        requesting = ds.child("requesting").getValue(String.class);
                        distance = ds.child("distance").getValue(String.class);
                        try {
                            addresses = geocoder.getFromLocation(Double.parseDouble(ulat), Double.parseDouble(ulong), 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            destadd = geocoder.getFromLocation(Double.parseDouble(endlat), Double.parseDouble(endlong), 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        finaladd = destadd.get(0).getAddressLine(0);

                        add = addresses.get(0).getAddressLine(0);
                        Location location1 = new Location("");
                        location1.setLatitude(Double.parseDouble(dlat));
                        location1.setLongitude(Double.parseDouble(dlong));
                        try {
                            Location location2 = new Location("");
                            location2.setLatitude(Double.parseDouble(ulat));
                            location2.setLongitude(Double.parseDouble(ulong));
                            float distanceInMeters = location1.distanceTo(location2);
                            System.out.println(distanceInMeters);

                            if (distanceInMeters < 5000) {

                                showTowingServicePop(uname, ucontact, ulat, ulong);

                            }
                        } catch (Exception e) {

                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            Toast.makeText(UserRequestActivity.this, "Towing service request", Toast.LENGTH_SHORT).show();

            //showTowingPop();
        } else if (checkcs.equals("100")) {
            //call service code here
            databaseReference.child("Requests").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        System.out.println(ds.getKey());
                        uname = ds.child("name").getValue(String.class);
                        ucontact = ds.child("contactNo").getValue(String.class);
                        ulat = ds.child("lat").getValue(String.class);
                        ulong = ds.child("longi").getValue(String.class);
                        requesting = ds.child("requesting").getValue(String.class);
                        try {
                            addresses = geocoder.getFromLocation(Double.parseDouble(ulat), Double.parseDouble(ulong), 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        add = addresses.get(0).getAddressLine(0);
                        Location location1 = new Location("");
                        location1.setLatitude(Double.parseDouble(dlat));
                        location1.setLongitude(Double.parseDouble(dlong));
                        try {
                            Location location2 = new Location("");
                            location2.setLatitude(Double.parseDouble(ulat));
                            location2.setLongitude(Double.parseDouble(ulong));
                            float distanceInMeters = location1.distanceTo(location2);
                            System.out.println(distanceInMeters);

                            if (distanceInMeters < 5000) {

                                showCallServicePop(uname, ucontact, ulat, ulong);

                            }
                        } catch (Exception e) {

                        }


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            Toast.makeText(UserRequestActivity.this, "Call out service request", Toast.LENGTH_SHORT).show();
            System.out.println("Callservice" + uname + ucontact + ulat + ulong + requesting);
            //showCallServicePop(uname, ucontact, ulat, ulong);


        } else {
            //call service code here
            databaseReference.child("Requests").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        System.out.println(ds.getKey());
                        uname = ds.child("name").getValue(String.class);
                        ucontact = ds.child("contactNo").getValue(String.class);
                        ulat = ds.child("lat").getValue(String.class);
                        ulong = ds.child("longi").getValue(String.class);
                        requesting = ds.child("requesting").getValue(String.class);
                        System.out.println(uname + ucontact + ulat + ulong + requesting);
                        try {
                            addresses = geocoder.getFromLocation(Double.parseDouble(ulat), Double.parseDouble(ulong), 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        add = addresses.get(0).getAddressLine(0);
                        Location location1 = new Location("");
                        location1.setLatitude(Double.parseDouble(dlat));
                        location1.setLongitude(Double.parseDouble(dlong));
                        try {
                            Location location2 = new Location("");
                            location2.setLatitude(Double.parseDouble(ulat));
                            location2.setLongitude(Double.parseDouble(ulong));
                            float distanceInMeters = location1.distanceTo(location2);
                            System.out.println(distanceInMeters);

                            if (distanceInMeters < 5000) {

                                showCallServicePop(uname, ucontact, ulat, ulong);

                            }
                        } catch (Exception e) {

                        }


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            Toast.makeText(UserRequestActivity.this, "Call out service request", Toast.LENGTH_SHORT).show();


        }

        endservice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1= new Intent(UserRequestActivity.this,ServiceCompleteActivity.class);
                intent.putString("","");
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent1);
                finish();
            }
        });

    }

    private void showTowingServicePop(String uname, String ucontact, final String ulat, final String ulong) {
        final TextView username, phone, ulocation, totp, flocation;
        abc="0";
        final Button accept, decline, breqotp;
        dialogBuilder = new AlertDialog.Builder(UserRequestActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.towingdialog, null);
        dialogBuilder.setCancelable(true);
        username = dialogView.findViewById(R.id.utowingname);
        //totp= dialogView.findViewById(R.id.OTP);
        phone = dialogView.findViewById(R.id.utowingcontact);
        accept = dialogView.findViewById(R.id.towingacceptreqbtn);
        decline = dialogView.findViewById(R.id.towingdeclinereqbtn);
        flocation = dialogView.findViewById(R.id.towingto);
        //bdone = dialogView.findViewById(R.id.donereqbtn);
        //breqotp = dialogView.findViewById(R.id.towingreqbtn);
        ulocation = dialogView.findViewById(R.id.towingfrom);
        username.setText(uname);
        phone.setText(ucontact);
        ulocation.setText(add);
        flocation.setText(finaladd);
        System.out.println(add);
        Vibrator vibrator;
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(1000);
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                states = 1;
                reference.child("Towing Requests").addValueEventListener(new ValueEventListener() {
                    //abc= "0";
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            key = ds.getKey();

                        }
                        UserDetails details = new UserDetails();
                        HashMap<String, Object> hashM = new HashMap<>();
                        hashM.put("requesting", "0");
                        hashM.put("drivername", dname);
                        hashM.put("driverphone", dcontact);
                        hashM.put("driverlat", dlat);
                        hashM.put("driverlong", dlong);
                        hashM.put("did", did);
                        details.setRequesting("0");
                        //if (databaseReference.getKey() != auth.getCurrentUser().getUid()) {
//                                            dd.child(keyy).updateChildren(hashMap);

                        reference.child("Towing Requests").child(key).updateChildren(hashM);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                dialogBuilder.dismiss();

                databaseReference.child("Driver");
                final DatabaseReference d = databaseReference.child("Driver");
                d.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (!ds.getKey().equals(auth.getCurrentUser().getUid())) {
                                HashMap<String, Object> hashMap = new HashMap<>();

                                hashMap.put("request", "0");
                                System.out.println("ds.getkey()" + ds.getKey());
                                d.child(ds.getKey()).updateChildren(hashMap);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                final double otp;
                //int y= (int)otp;
                otp = Math.round(Math.random() * 100000);
                //totp.setText((String.valueOf(otp)));
                final DatabaseReference dd = reference.child("Towing Requests");
                dd.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            kkey = ds.getKey();
                        }

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("OTP", (String.valueOf((int) otp)));
                        dd.child(kkey).updateChildren(hashMap);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                notificaticationCall(String.valueOf(otp));

                showroute(ulat, ulong);

            }

        });


        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optoutfromservice();

                dialogBuilder.dismiss();
            }
        });

/*        breqotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final double otp;
                //int y= (int)otp;
                otp = Math.round(Math.random() * 100000);
                //totp.setText((String.valueOf(otp)));
                final DatabaseReference dd = reference.child("Towing Requests");
                dd.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            kkey = ds.getKey();
                        }

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("OTP", (String.valueOf((int)otp)));
                        dd.child(kkey).updateChildren(hashMap);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                notificaticationCall(String.valueOf(otp));
                dialogBuilder.dismiss();
            }
        });*/

    }


    private void showCallServicePop(String uname, String ucontact, final String ulat, final String ulong) {

        abc="1";
        final TextView username, phone, ulocation, totp;
        final Button accept, decline, breqotp;
        dialogBuilder = new AlertDialog.Builder(UserRequestActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.customdialog, null);
        dialogBuilder.setCancelable(true);
        username = dialogView.findViewById(R.id.dname);
        totp = dialogView.findViewById(R.id.OTP);
        phone = dialogView.findViewById(R.id.dphone);
        accept = dialogView.findViewById(R.id.acceptreqbtn);
        decline = dialogView.findViewById(R.id.declinereqbtn);
        //bdone = dialogView.findViewById(R.id.donereqbtn);
        // breqotp = dialogView.findViewById(R.id.reqotpbtn);
        ulocation = dialogView.findViewById(R.id.dloc);
        username.setText(uname);
        phone.setText(ucontact);
        ulocation.setText(add);
        System.out.println(add);
        Vibrator vibrator;
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(1000);
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                states = 1;
                reference.child("Requests").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            key = ds.getKey();

                        }
                        UserDetails details = new UserDetails();
                        HashMap<String, Object> hashM = new HashMap<>();
                        hashM.put("requesting", "0");
                        hashM.put("drivername", dname);
                        hashM.put("driverphone", dcontact);
                        hashM.put("driverlat", dlat);
                        hashM.put("did", did);
                        hashM.put("driverlong", dlong);
                        details.setRequesting("0");
                        //if (databaseReference.getKey() != auth.getCurrentUser().getUid()) {
//                                            dd.child(keyy).updateChildren(hashMap);

                        reference.child("Requests").child(key).updateChildren(hashM);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                dialogBuilder.dismiss();

                databaseReference.child("Driver");
                final DatabaseReference d = databaseReference.child("Driver");
                d.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (!ds.getKey().equals(auth.getCurrentUser().getUid())) {
                                HashMap<String, Object> hashMap = new HashMap<>();

                                hashMap.put("request", "0");
                                System.out.println("ds.getkey()" + ds.getKey());
                                d.child(ds.getKey()).updateChildren(hashMap);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                final int otp;
                otp = (int) Math.round(Math.random() * 100000);
                //totp.setText((String.valueOf(otp)));
                final DatabaseReference dd = reference.child("Requests");
                dd.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            kkey = ds.getKey();
                        }

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("OTP", (String.valueOf(otp)));
                        dd.child(kkey).updateChildren(hashMap);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                notificaticationCall(String.valueOf(otp));

                showroute(ulat, ulong);
            }
        });

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optoutfromservice();
                dialogBuilder.dismiss();
            }
        });

 /*       breqotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int otp;
                otp = (int) Math.round(Math.random() * 100000);
                //totp.setText((String.valueOf(otp)));
                final DatabaseReference dd = reference.child("Requests");
                dd.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            kkey = ds.getKey();
                        }

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("OTP", (String.valueOf(otp)));
                        dd.child(kkey).updateChildren(hashMap);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                notificaticationCall(String.valueOf(otp));
                dialogBuilder.dismiss();
            }
        });
 */
    }

    private void optoutfromservice() {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("request", "0");
        databaseReference.child("Driver").child(auth.getCurrentUser().getUid()).updateChildren(hashMap);
        Intent intent = new Intent(UserRequestActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("requestcanceled", "0");
        dialogBuilder.dismiss();
        startActivity(intent);
        finish();

    }

    private void showroute(String ulat, String ulong) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?&daddr=" + ulat + "," + ulong + ""));
        startActivity(intent);


    }

    public void notificaticationCall(String req) {
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentTitle("NOTIFICATION")
                .setSmallIcon(R.drawable.isafe_assist_logo)
                .setContentText("Verify this OTP with user " + req);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build());

    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

        /*DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        reference.child("Requests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(UserRequestActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


}
