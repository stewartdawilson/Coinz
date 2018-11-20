package com.example.s1636431.coinz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {



    TextView textUser, textWallet;
    Button btFriends;
    Button btBank;
    Button btPlay;
    Button btStat;


    static public String emailID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        textUser = (TextView) findViewById(R.id.userID);
        textWallet = (TextView) findViewById(R.id.walletID);


        textUser.setText(MainActivity.email);
        Log.d("USER", MainActivity.email);
        // Got from stack overflow
        textWallet.setText("Wallet: " + Double.toString(Math.round(MainActivity.wallet.values().stream().mapToDouble(Number::doubleValue).sum())) + " Gold");
    }
}
