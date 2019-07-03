package com.prateek.isafeassistdriver;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.prateek.isafeassistdriver.dao.Driver;
import com.prateek.isafeassistdriver.dao.DriverLocation;
import com.prateek.isafeassistdriver.dao.UserDetails;
import com.prateek.isafeassistdriver.maps.MapsActivity;
import com.prateek.isafeassistdriver.navattr.EarningsActivity;
import com.prateek.isafeassistdriver.navattr.FeedbackActivity;
import com.prateek.isafeassistdriver.navattr.HelpActivity;
import com.prateek.isafeassistdriver.navattr.NotiFicationActivity;
import com.prateek.isafeassistdriver.navattr.ProfileActivity;
import com.prateek.isafeassistdriver.welcome.SignUpActivity;
import com.prateek.isafeassistdriver.welcome.SplashActivity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    private GoogleMap mMap;
    int status = 1;
    String keyy;
    final DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("Driver");

    ImageButton swipeup;
    Switch statuschange;
    Geocoder geocoder;
    List<Address> addresses;
    AlertDialog dialogBuilder;
    TextView textView;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    private BottomSheetBehavior bottomSheetBehavior;
    ProgressDialog progressDialog;
    String username, contactno, ulat, ulong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        swipeup = findViewById(R.id.dropupbtn);
        statuschange = findViewById(R.id.onlinestatusswitch);
        View bottom = findViewById(R.id.bottom_sheet);
        textView = findViewById(R.id.status_text);
        bottomSheetBehavior = BottomSheetBehavior.from(bottom);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Getting your Current Location");

        progressDialog.setCancelable(false);
        progressDialog.show();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //mapcode
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {

                if (status == 1) {
                    swipeup.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
                    status = 0;

                } else {
                    swipeup.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
                    status = 1;

                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
        swipeup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status == 1) {
                    swipeup.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);

                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    status = 0;
                } else if (status == 0) {
                    swipeup.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    status = 1;
                }
            }
        });

        statuschange.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                HashMap<String, Object> hashMap = new HashMap<>();
                Driver driver = new Driver();

                if (isChecked) {
                    hashMap.put("status", 1);
                    driver.setStatus("1");
                    databaseReference.child("Driver").child(auth.getCurrentUser().getUid()).updateChildren(hashMap);
                    startlocationUpdates();
                    textView.setText("You are Online");
                } else {
                    hashMap.put("status", 0);
                    driver.setStatus("1");
                    databaseReference.child("Driver").child(auth.getCurrentUser().getUid()).updateChildren(hashMap);

                    removelocationUpdates();
                    textView.setText("You are Offline");
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Notify", "Notify", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        FirebaseMessaging.getInstance().subscribeToTopic("general")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Success";
                        if (!task.isSuccessful()) {
                            msg = "Failure";
                        }
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_notification) {
            startActivity(new Intent(MainActivity.this, NotiFicationActivity.class));


        } else if (id == R.id.nav_profile) {

            startActivity(new Intent(MainActivity.this, ProfileActivity.class));


        } else if (id == R.id.nav_logout) {
            alertbuilder();


        } else if (id == R.id.nav_earnings) {

            startActivity(new Intent(MainActivity.this, EarningsActivity.class));

        } else if (id == R.id.nav_help) {

            startActivity(new Intent(MainActivity.this, HelpActivity.class));

        } else if (id == R.id.nav_feedback) {

            startActivity(new Intent(MainActivity.this, FeedbackActivity.class));

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
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
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
//Showing Current Location Marker on Map
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location locations = locationManager.getLastKnownLocation(provider);
        List<String> providerList = locationManager.getAllProviders();
        if (null != locations && null != providerList && providerList.size() > 0) {
            double longitude = locations.getLongitude();
            double latitude = locations.getLatitude();
            Geocoder geocoder = new Geocoder(getApplicationContext(),
                    Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(latitude,
                        longitude, 1);
                if (null != listAddresses && listAddresses.size() > 0) {
                    String state = listAddresses.get(0).getAdminArea();
                    String country = listAddresses.get(0).getCountryName();
                    String subLocality = listAddresses.get(0).getSubLocality();
                    markerOptions.title("" + latLng + "," + subLocality + "," + state
                            + "," + country);

                    saveDriverInfo(latLng, subLocality, state, country);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
        progressDialog.dismiss();
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "permission denied",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void alertbuilder() {
        new AlertDialog.Builder(this)
                .setTitle("LogOut")
                .setMessage("Are you sure you want to LogOut?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.signOut();
                        Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                        // Continue with delete operation
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void removelocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    private void startlocationUpdates() {
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
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void saveDriverInfo(LatLng latLng, String subLocality, String state, String country) {

        Double l1, l2;
        l1 = latLng.latitude;
        l2 = latLng.longitude;

/*
        DriverLocation driverLocation= new DriverLocation();
*/
        //Driver driverLocation = new Driver(l1.toString(), l2.toString(), auth.getCurrentUser().getUid(), subLocality, state, country);
        DriverLocation driverLocation = new DriverLocation();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("latitude", l1.toString());
        hashMap.put("longitude", l2.toString());
        hashMap.put("driverid", auth.getCurrentUser().getUid());
        hashMap.put("sublocality", subLocality);
        hashMap.put("state", state);
        hashMap.put("country", country);
        driverLocation.setCountry(country);
        driverLocation.setLatitude(l1.toString());
        driverLocation.setLongitude(l2.toString());
        driverLocation.setState(state);
        driverLocation.setSublocality(subLocality);
        driverLocation.setDriverid(auth.getCurrentUser().getUid());

        databaseReference.child("Driver").child(auth.getCurrentUser().getUid()).updateChildren(hashMap);


    }

    @Override
    protected void onResume() {
        super.onResume();

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Driver");
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Requests");
        geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    final String key = ds.getKey();
                    username = ds.child("name").getValue(String.class);
                    contactno = ds.child("contactNo").getValue(String.class);
                    ulat = ds.child("lat").getValue(String.class);
                    ulong = ds.child("longi").getValue(String.class);
                    final String requesting = ds.child("requesting").getValue(String.class);

                    System.out.println("requesting " + requesting);
                    databaseReference.child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            String reqid = dataSnapshot.child("request").getValue(String.class);
                            final String lat = dataSnapshot.child("latitude").getValue(String.class);
                            final String longitude = dataSnapshot.child("longitude").getValue(String.class);
                            final String name = dataSnapshot.child("name").getValue(String.class);
                            final String phoneno = dataSnapshot.child("contact").getValue(String.class);
                            final int status= dataSnapshot.child("status").getValue(Integer.class);

                            Location location1 = new Location("");
                            location1.setLatitude(Double.parseDouble(lat));
                            location1.setLongitude(Double.parseDouble(longitude));
                            try {
                                Location location2 = new Location("");
                                location2.setLatitude(Double.parseDouble(ulat));
                                location2.setLongitude(Double.parseDouble(ulong));
                                float distanceInMeters = location1.distanceTo(location2);
                                System.out.println(distanceInMeters);

                                if (reqid.equals("1") && distanceInMeters < 5000 && requesting.equals("1") && status==1) {

                                    Toast.makeText(MainActivity.this, "User requested", Toast.LENGTH_SHORT).show();
                                    //customdialogbuilder(username, contactno);

                                    try {
                                        addresses = geocoder.getFromLocation(Double.parseDouble(ulat), Double.parseDouble(ulong), 1);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    final DatabaseReference dd= FirebaseDatabase.getInstance().getReference().child("Driver");
                                    dd.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for(DataSnapshot dd: dataSnapshot.getChildren()){
                                                 keyy= dd.getKey();
                                                System.out.println(keyy);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    String add = addresses.get(0).getAddressLine(0);
                                    final TextView uname, phone, ulocation;
                                    final Button accept, decline, bdone;
                                    dialogBuilder = new AlertDialog.Builder(MainActivity.this).create();
                                    LayoutInflater inflater = getLayoutInflater();
                                    final View dialogView = inflater.inflate(R.layout.customdialog, null);
                                    dialogBuilder.setCancelable(false);
                                    uname = dialogView.findViewById(R.id.dname);
                                    phone = dialogView.findViewById(R.id.dphone);
                                    accept = dialogView.findViewById(R.id.acceptreqbtn);
                                    decline = dialogView.findViewById(R.id.declinereqbtn);
                                    bdone= dialogView.findViewById(R.id.donereqbtn);
                                    ulocation = dialogView.findViewById(R.id.dloc);
                                    uname.setText(username);
                                    phone.setText(contactno);
                                    ulocation.setText(add);
                                    /*final MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.notifsound);
                                    mp.start();
*/
                                    Vibrator vibrator;
                                    vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                    vibrator.vibrate(1000);
                                    dialogBuilder.setView(dialogView);
                                    dialogBuilder.show();

                                    accept.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            UserDetails details = new UserDetails();
                                            HashMap<String, Object> hashM = new HashMap<>();
                                            hashM.put("requesting", "0");
                                            hashM.put("drivername", name);
                                            hashM.put("driverphone", phoneno);
                                            hashM.put("driverlat", lat);
                                            hashM.put("driverlong", longitude);
                                            details.setRequesting("0");
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("request", "0");

                                            //if (databaseReference.getKey() != auth.getCurrentUser().getUid()) {
//                                            dd.child(keyy).updateChildren(hashMap);

                                            reference.child(key).updateChildren(hashM);

                                            Toast.makeText(MainActivity.this, "Finding Route till User", Toast.LENGTH_SHORT).show();
                                            dialogBuilder.dismiss();


                                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                                    Uri.parse("http://maps.google.com/maps?&daddr="+ulat+","+ulong+""));
                                            startActivity(intent);

                                        }
                                    });

                                    /*decline.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {



                                        }
                                    });*/

                                    bdone.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialogBuilder.dismiss();
                                        }
                                    });


                                }
                            } catch (Exception e) {
                                System.out.println(e);
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });


    }

    private void customdialogbuilder(String username, String contactno) {
        TextView uname, phone;
        Button accept, decline;
        final AlertDialog dialogBuilder = new AlertDialog.Builder(MainActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.customdialog, null);
        dialogBuilder.setCancelable(false);
        uname = dialogView.findViewById(R.id.dname);
        phone = dialogView.findViewById(R.id.dphone);
        accept = dialogView.findViewById(R.id.acceptreqbtn);
        decline = dialogView.findViewById(R.id.declinereqbtn);
        uname.setText(username);
        phone.setText(contactno);
        final MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.notifsound);
        mp.start();

        Vibrator vibrator;
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(1000);
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();

    }

    public void declinereq(View view) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("request", "0");
        databaseRef.child(auth.getCurrentUser().getUid()).updateChildren(hashMap);
        Toast.makeText(MainActivity.this, "You Declined the request", Toast.LENGTH_SHORT).show();


        dialogBuilder.dismiss();


    }
}