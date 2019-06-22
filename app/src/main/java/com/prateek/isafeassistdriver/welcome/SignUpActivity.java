package com.prateek.isafeassistdriver.welcome;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.prateek.isafeassistdriver.MainActivity;
import com.prateek.isafeassistdriver.R;
import com.prateek.isafeassistdriver.dao.Driver;

public class SignUpActivity extends AppCompatActivity {

    private EditText name, contact, mail, pass, confirmpass;
    private Button signup;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    boolean checkstatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        name = findViewById(R.id.signup_name);
        contact = findViewById(R.id.signup_mobileno);
        mail = findViewById(R.id.signup_email);
        pass = findViewById(R.id.signup_pass);
        confirmpass = findViewById(R.id.signup_confirm_pass);
        signup = findViewById(R.id.signup);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(SignUpActivity.this);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(name.getText().toString()) || TextUtils.isEmpty(contact.getText().toString()) || TextUtils.isEmpty(mail.getText().toString()) ||
                        TextUtils.isEmpty(pass.getText().toString()) || TextUtils.isEmpty(confirmpass.getText().toString())) {

                    name.setError("Check All fields");
                    name.requestFocus();
                    checkstatus = false;

                } else {

                    if (!pass.getText().toString().equals(confirmpass.getText().toString())) {
                        Toast.makeText(SignUpActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();

                        checkstatus = false;
                    } else {
                        checkstatus = true;
                    }
                }

                if (checkstatus) {

                    UserRegistrationfunction();
                } else {
                    Toast.makeText(SignUpActivity.this, "Enter All the fields", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    public void UserRegistrationfunction() {

        // Showing progress dialog at user registration time.
        progressDialog.setMessage("Please Wait, We are Registering Your Data on Server");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(mail.getText().toString(), pass.getText().toString()).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                progressDialog.dismiss();

                if (task.isSuccessful()) {
                    final Driver driver = new Driver();
                    driver.setName(name.getText().toString());
                    driver.setMail(mail.getText().toString());
                    driver.setContact(contact.getText().toString());
                    driver.setPass(pass.getText().toString());
                    driver.setDid(firebaseAuth.getCurrentUser().getUid());

                    databaseReference.child("Driver").child(firebaseAuth.getCurrentUser().getUid()).setValue(driver, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                Toast.makeText(SignUpActivity.this, "Data saved Successfully", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);

                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                   /* intent.putExtras(bundle);*/
                    startActivity(intent);
                    finish();
                    // If user registered successfully then show this toast message.
                    Toast.makeText(SignUpActivity.this, "User Registration Successfully", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SignUpActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();

                }
            }
        });
    }
}
