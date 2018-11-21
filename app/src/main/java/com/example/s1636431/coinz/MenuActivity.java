package com.example.s1636431.coinz;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {



    TextView textUser, textWallet;
    ImageView userProfile;
    Button btCommunity;
    Button btBank;
    Button btPlay;
    Button btStat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        textUser = (TextView) findViewById(R.id.userID);
        textWallet = (TextView) findViewById(R.id.walletID);

        btCommunity = (Button) findViewById(R.id.btCommunity);
        btBank = (Button) findViewById(R.id.btBank);
        btPlay = (Button) findViewById(R.id.btPlay);
        btStat = (Button) findViewById(R.id.btStat);

        btCommunity.setOnClickListener(this);
        btBank.setOnClickListener(this);
        btPlay.setOnClickListener(this);
        btStat.setOnClickListener(this);



        textUser.setText(MainActivity.email);
        Log.d("USER", MainActivity.email);
        // Got from stack overflow
        textWallet.setText("Wallet: " + Double.toString(Math.round(MainActivity.wallet.values().stream().mapToDouble(Number::doubleValue).sum())) + " Gold");
        getProfilePicture();
    }


    @Override
    public void onClick(View view) {
        if (view == btBank) {
            startActivity(new Intent(MenuActivity.this, BankActivity.class));
        } else if (view == btCommunity) {
            startActivity(new Intent(MenuActivity.this, CommunityActivity.class));
        } else if (view == btPlay) {
            startActivity(new Intent(MenuActivity.this, MainActivity.class));
        } else if (view == btStat) {
            startActivity(new Intent(MenuActivity.this, StatisticsActivity.class));
        }
    }

    public void getProfilePicture() {
        userProfile = (ImageView) findViewById(R.id.profile);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("User").document(MainActivity.email);
        dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().getData().get("profile_image")!=null) {
                    String profile_url = task.getResult().getData().get("profile_image").toString();


                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReference();

                    StorageReference path = storageReference.child(profile_url);

                    final long one_megabyte = 1024*1024;

                    path.getBytes(one_megabyte).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                            Bitmap bitmap;
                            if ( inputStream !=null) {
                                bitmap = BitmapFactory.decodeStream(inputStream);
                                userProfile.setImageBitmap(bitmap);
                            }
                        }
                    });
                } else {
                    userProfile.setImageDrawable(getDrawable(R.drawable.ic_user));
                }


            }
        });


    }


}
