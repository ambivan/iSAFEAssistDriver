package com.prateek.isafeassistdriver.navattr;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.prateek.isafeassistdriver.R;
import com.prateek.isafeassistdriver.remote.MyTripsActivity;

public class FeedbackActivity extends AppCompatActivity {

    EditText feedback;
    Button submit;
    FirebaseAuth auth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Toolbar toolbar = findViewById(R.id.toolbarfeedback);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Feedback");
        Window window = FeedbackActivity.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(FeedbackActivity.this, R.color.mystatus));

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        feedback = findViewById(R.id.driver_feedback);
        submit = findViewById(R.id.feedback_submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ss= feedback.getText().toString();
                System.out.println(ss);
                if(TextUtils.isEmpty(ss)){
                    Toast.makeText(FeedbackActivity.this,"Enter a valid Feedback",Toast.LENGTH_SHORT).show();
                }else{
                    databaseReference.child("Driver").child(auth.getCurrentUser().getUid()).push().setValue(feedback.getText().toString(), new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if (databaseError == null) {
                                Toast.makeText(FeedbackActivity.this, "Thank you for the Feedback", Toast.LENGTH_SHORT).show();

                                feedback.setText("");
                            }
                        }
                    });
                }

            }
        });
    }
}
