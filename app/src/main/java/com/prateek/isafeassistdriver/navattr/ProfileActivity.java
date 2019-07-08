package com.prateek.isafeassistdriver.navattr;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.prateek.isafeassistdriver.R;
import com.prateek.isafeassistdriver.dao.Driver;

import java.io.IOError;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    EditText mobileno, email;
    Driver driver;
    CircleImageView profileimage;
    FirebaseAuth auth;
    CircleImageView profile;
    Button edit;
    TextView username;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    ActionBar actionBar;
    ProgressBar progressBar;
    Uri filepath;
    private static final int PICK_IMAGE_REQ = 71;
    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mobileno = findViewById(R.id.user_phone_no);
        email = findViewById(R.id.user_emailid);
        username = findViewById(R.id.userprofile_username);
        edit = findViewById(R.id.edit_profile_btn);
        profile = findViewById(R.id.user_circular_profile);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Profile");
        edit.setText("Edit");
        storageReference = FirebaseStorage.getInstance().getReference().child("ImageFolder");
    /*    storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl("gs://isafeassist.appspot.com/images");
    */
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

                    uploadPicture();
                    System.out.println(email.getText().toString());
                    System.out.println(mobileno.getText().toString());

                    Toast.makeText(ProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                    mobileno.setEnabled(false);
                    email.setEnabled(false);
                    edit.setText("Edit");

                }
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDp();
            }
        });
    }

    private void uploadPicture() {
        if (filepath != null) {
            final StorageReference ref = storageReference.child("images" + filepath.getLastPathSegment());
            ref.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Driver").child(auth.getCurrentUser().getUid());
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("imageurl", String.valueOf(uri));
                                    databaseReference.updateChildren(hashMap);
                                    Toast.makeText(ProfileActivity.this, "Successfully saved", Toast.LENGTH_SHORT).show();

                                }
                            });

                        }
                    });

        }
    }

    private void updateDp() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQ);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQ && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filepath = data.getData();
            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                profile.setImageBitmap(bitmap);
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
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
                String url = dataSnapshot.child("imageurl").getValue(String.class);
                if (url == null) {
                    Glide.with(ProfileActivity.this).load(R.drawable.userprof).into(profile);

                } else {

                    Glide.with(ProfileActivity.this).load(url).into(profile);

                }
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
