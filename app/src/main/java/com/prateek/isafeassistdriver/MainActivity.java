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

import com.bumptech.glide.Glide;
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
import com.prateek.isafeassistdriver.remote.MyTripsActivity;
import com.prateek.isafeassistdriver.remote.UserRequestActivity;
import com.prateek.isafeassistdriver.welcome.SignUpActivity;
import com.prateek.isafeassistdriver.welcome.SplashActivity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

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
    View header;
    CircleImageView circleImageView;
    final DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("Driver");

    ImageButton swipeup;
    Switch statuschange;
    Geocoder geocoder;
    List<Address> addresses;
    AlertDialog dialogBuilder;
    TextView textView, dname;
    DatabaseReference databaseReference, ref;
    FirebaseAuth auth;
    static String isOn="1";

    private BottomSheetBehavior bottomSheetBehavior;
    ProgressDialog progressDialog;
    String username, contactno, ulat, ulong;
    Button tripdetails;
    NavigationView navigationView;

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
        tripdetails = findViewById(R.id.tripdetailsbtn);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Getting your Current Location");
        ref = FirebaseDatabase.getInstance().getReference().child("Driver").child(auth.getCurrentUser().getUid());
        progressDialog.setCancelable(false);
        progressDialog.show();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue(String.class);
                String url = dataSnapshot.child("imageurl").getValue(String.class);
                //textView.setText(name);
                header = navigationView.getHeaderView(0);
                dname = header.findViewById(R.id.header_name);
                circleImageView = header.findViewById(R.id.header_profileimg);
                circleImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));

                    }
                });
                if (url != null) {
                    Glide.with(getApplicationContext()).load(url).into(circleImageView);

                } else {
                    Glide.with(getApplicationContext()).load(R.drawable.userprof).into(circleImageView);
                    //startActivity(new Intent(MainActivity.this, ProfileActivity.class));

                }
                dname.setText(name);
                System.out.println(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



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
                    hashMap.put("status", "1");
                    isOn="1";
                    driver.setStatus("1");
                    databaseReference.child("Driver").child(auth.getCurrentUser().getUid()).updateChildren(hashMap);
                    startlocationUpdates();
                    textView.setText("You are Online");
                } else {
                    hashMap.put("status", "0");
                    driver.setStatus("0");
                    isOn="0";
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

        tripdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyTripsActivity.class);
                startActivity(intent);

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

    boolean cs = false;
    boolean ts = false;

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Towing Requests");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Requests");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Driver").child(auth.getCurrentUser().getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String reqid = ds.child("requesting").getValue(String.class);
                    System.out.println("reqid " + reqid);
                    if (reqid.equals("1")) {
                        //cs = false;
                        ts = true;
                    } else {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String reqid = ds.child("requesting").getValue(String.class);
                    System.out.println("towing reqid " + reqid);
                    if (reqid.equals("1")) {
                        //ts = false;
                        cs = true;
                    } else {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

/*        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue()==null){

                }else{
                    isOn = dataSnapshot.child("status").getValue(String.class);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        System.out.println(cs + " " + ts);
        if (cs == true && ts == false && isOn.equals("1")) {
            Intent intent = new Intent(MainActivity.this, UserRequestActivity.class);
            intent.putExtra("cs", "1");
            startActivity(intent);
            finish();
        } else if (cs == false && ts == true && isOn.equals("1")) {
            Intent intent = new Intent(MainActivity.this, UserRequestActivity.class);
            intent.putExtra("cs", "0");
            startActivity(intent);
            finish();

        } else if (cs && ts && isOn.equals("1")) {
            Intent intent = new Intent(MainActivity.this, UserRequestActivity.class);
            intent.putExtra("cs", "100");
            startActivity(intent);
            finish();

        }
        //}


    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}