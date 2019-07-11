package com.prateek.isafeassistdriver.remote;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.prateek.isafeassistdriver.MainActivity;
import com.prateek.isafeassistdriver.R;

public class PaymentActivity extends AppCompatActivity {

    Button button;
    EditText editText;
    CheckBox upi, cash, membershipid, netbanking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        button = findViewById(R.id.finalcompletebtn);
        editText = findViewById(R.id.membershipidtext);
        upi = findViewById(R.id.upicheck);
        cash = findViewById(R.id.cashcheck);
        membershipid = findViewById(R.id.membershipcheck);
        netbanking = findViewById(R.id.netbankingcheck);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!upi.isChecked() && !cash.isChecked() && !netbanking.isChecked() && !membershipid.isChecked()) {
                    Toast.makeText(PaymentActivity.this, "Please Select a Payment Method", Toast.LENGTH_SHORT).show();
                } else if (upi.isChecked()) {
                    Toast.makeText(PaymentActivity.this, "Thank You for your service", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else if (cash.isChecked()) {
                    Toast.makeText(PaymentActivity.this, "Thank You for your service", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else if (netbanking.isChecked()) {
                    Toast.makeText(PaymentActivity.this, "Thank You for your service", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else if (membershipid.isChecked() && editText.getText().toString().isEmpty()) {

                    Toast.makeText(PaymentActivity.this, "Enter Membership id", Toast.LENGTH_SHORT).show();
                    editText.setError("Enter a valid id");
                    editText.requestFocus();

                } else {
                    Toast.makeText(PaymentActivity.this, "Thank You for your service", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }


}
