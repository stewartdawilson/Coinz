package com.example.s1636431.coinz;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TradeActivity extends AppCompatActivity {

    private static final String TAG = "TradeActivity";

    ImageView friendImage;
    TextView txEmail;
    Spinner spinner;

    private String email;
    private List<String> spinnerData;
    private ArrayAdapter<CharSequence> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);

        if(getIntent().hasExtra("byteArray")) {
            friendImage = (ImageView) findViewById(R.id.userTradeImage);
            Bitmap _bitmap = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("byteArray"),0,getIntent().getByteArrayExtra("byteArray").length);
            friendImage.setImageBitmap(_bitmap);
        }

        email = this.getIntent().getStringExtra("email");

        txEmail = (TextView) findViewById(R.id.userTradeID);
        txEmail.setText(email);


        spinner = (Spinner) findViewById(R.id.spinnerTrade);
        //renderSpinner(spinner);

//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//                String criteria = parent.getItemAtPosition(pos).toString();
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                return;
//
//            }
//        });



    }



    public void renderSpinner(Spinner spinner) {

        // Change this to trading wallet shit

        adapter = ArrayAdapter.createFromResource(this,
                R.array.options_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }
}
