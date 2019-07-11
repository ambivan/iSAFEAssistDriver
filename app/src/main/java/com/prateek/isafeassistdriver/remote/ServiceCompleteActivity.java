package com.prateek.isafeassistdriver.remote;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prateek.isafeassistdriver.R;

public class ServiceCompleteActivity extends AppCompatActivity {

    EditText editText;
    Button button;
    FirebaseAuth auth;
    ProgressDialog dialog;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_complete);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        editText = findViewById(R.id.otptoendservice);
        button = findViewById(R.id.submit_otp);
        Window window = ServiceCompleteActivity.this.getWindow();
// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(ServiceCompleteActivity.this, R.color.mystatus));
        //PaymentActivity.count++;

        dialog= new ProgressDialog(ServiceCompleteActivity.this);
        dialog.setMessage("Verifying...");
        dialog.setCancelable(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = editText.getText().toString();

                dialog.show();
                submitforservice(message);
            }
        });
    }

    public void submitforservice(final String message) {


        System.out.println(UserRequestActivity.abc);
        if(UserRequestActivity.abc.equals("1")) {
            databaseReference.child("Requests").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    System.out.println("servicecompleteactivity" + dataSnapshot.getKey());
                    String eotp = dataSnapshot.child(UserRequestActivity.key).child("endotp").getValue(String.class);
                    System.out.println(UserRequestActivity.key);
                    System.out.println("ottp" + eotp);
                    if (eotp.equals(message)) {
                        dialog.dismiss();
                        Toast.makeText(ServiceCompleteActivity.this, "Service Ended Successfully", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(ServiceCompleteActivity.this, PaymentActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        dialog.dismiss();

                        Toast.makeText(ServiceCompleteActivity.this, "Enter Correct OTP", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else {
            databaseReference.child("Towing Requests").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String eotp = dataSnapshot.child(UserRequestActivity.key).child("endotp").getValue(String.class);
                    if (eotp.equals(message)) {
                        dialog.dismiss();

                        Toast.makeText(ServiceCompleteActivity.this, "Service Ended Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ServiceCompleteActivity.this, PaymentActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        dialog.dismiss();

                        Toast.makeText(ServiceCompleteActivity.this, "Enter Correct OTP", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }



    }
}
