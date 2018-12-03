package com.example.s1636431.coinz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
/*
    Tutorial screen for new users of app.
 */
public class TutorialActivity extends AppCompatActivity {

    Button btGo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        btGo = (Button) findViewById(R.id.btLetsPlay);
        btGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TutorialActivity.this,MainActivity.class));
            }
        });
    }
}
