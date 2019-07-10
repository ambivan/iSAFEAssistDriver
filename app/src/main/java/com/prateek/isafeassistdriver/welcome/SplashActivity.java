package com.prateek.isafeassistdriver.welcome;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prateek.isafeassistdriver.MainActivity;
import com.prateek.isafeassistdriver.R;
import com.prateek.isafeassistdriver.dao.Driver;

public class SplashActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    ImageView bgapp, clover;
    LinearLayout textsplash, texthome, menus;
    Animation frombottom;
    Button signup, signin;
    FirebaseAuth auth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        signup = findViewById(R.id.signup_btn);
        signin = findViewById(R.id.login_btn);
        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser()!=null){
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Driver");
            Intent intent=new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();


        }else{
            animatewelcome();
        }

    }

    void animatewelcome() {

        frombottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);


        bgapp = (ImageView) findViewById(R.id.logo);
        menus = (LinearLayout) findViewById(R.id.menus);

        bgapp.animate().translationY(-400).setDuration(1800).setStartDelay(300);
        menus.startAnimation(frombottom);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashActivity.this, SignUpActivity.class);
/*
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
*/
                startActivity(intent);
/*
                finish();
*/
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashActivity.this, LogInActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
                //finish();
            }
        });
    }
}
