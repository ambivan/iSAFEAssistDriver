package com.prateek.isafeassistdriver.navattr;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prateek.isafeassistdriver.R;
import com.prateek.isafeassistdriver.dao.Driver;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    EditText mobileno, email;
    Driver driver;
    CircleImageView profileimage;
    FirebaseAuth auth;
    Button edit;
    TextView username;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mobileno = findViewById(R.id.user_phone_no);
        email = findViewById(R.id.user_emailid);
        username = findViewById(R.id.userprofile_username);
        edit = findViewById(R.id.edit_profile_btn);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Profile");
        edit.setText("Edit");
        driver = new Driver();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //setTitle(Html.fromHtml("<font color='#000000'>Profile </font>"));
        auth = FirebaseAuth.getInstance();
        mobileno.setEnabled(false);
        email.setEnabled(false);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Driver").child(auth.getCurrentUser().getUid());
        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("Loading Profile Info..");
        progressDialog.show();
        progressDialog.setCancelable(false);
        loadinfo();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit.getText().toString().equals("Edit")) {

                    mobileno.setEnabled(true);
                    email.setEnabled(true);
                    edit.setText("Save");


                } else {
                    DatabaseReference databaseref = FirebaseDatabase.getInstance().getReference().child("Driver");
                    databaseref.child(auth.getCurrentUser().getUid()).child("mail").setValue(email.getText().toString());
                    databaseref.child(auth.getCurrentUser().getUid()).child("contact").setValue(mobileno.getText().toString());

                    System.out.println(email.getText().toString());
                    System.out.println(mobileno.getText().toString());

                    Toast.makeText(ProfileActivity.this,"Profile Updated Successfully",Toast.LENGTH_SHORT).show();
                    mobileno.setEnabled(false);
                    email.setEnabled(false);
                    edit.setText("Edit");

                }
            }
        });

    }

    private void loadinfo() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String key = dataSnapshot.getKey();
                System.out.println(key);
                String name = dataSnapshot.child("name").getValue(String.class);
                String mail = dataSnapshot.child("mail").getValue(String.class);
                String mobile = dataSnapshot.child("contact").getValue(String.class);
                System.out.println(name);
                System.out.println(mail);
                System.out.println(mobile);

                mobileno.setText(mobile);
                email.setText(mail);
                username.setText(name);
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
