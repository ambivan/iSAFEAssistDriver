package com.prateek.isafeassistdriver.navattr;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mobileno = findViewById(R.id.user_phone_no);
        email = findViewById(R.id.user_emailid);
        username = findViewById(R.id.userprofile_username);
        edit = findViewById(R.id.edit_profile_btn);
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
        progressDialog.setMessage("Loading Profile Info");
        progressDialog.show();
        progressDialog.setCancelable(false);
        loadinfo();

    }

    private void loadinfo(){

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String key = ds.getKey();
                    String sub = "-Lh";
                    String temp;
                    if (key.contains(sub)) {
                        temp = key;
                        System.out.println(temp);

                        DatabaseReference d = databaseReference.child(temp);

                        d.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                Driver driver = dataSnapshot.getValue(Driver.class);
                                username.setText(driver.getName());
                                email.setText(driver.getMail());
                                mobileno.setText(driver.getContact());
                                progressDialog.dismiss();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
