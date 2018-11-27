package com.example.s1636431.coinz;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Comparator.reverseOrder;
import static java.util.Map.Entry.comparingByValue;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MenuActivity";

    private Integer number;
    private Double bank_amount = 0.0;
    private Long bank_amount_final;




    TextView textUser, textBank;
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
        textBank = (TextView) findViewById(R.id.bankID);

        btCommunity = (Button) findViewById(R.id.btCommunity);
        btBank = (Button) findViewById(R.id.btBank);
        btPlay = (Button) findViewById(R.id.btPlay);
        btStat = (Button) findViewById(R.id.btStat);

        btCommunity.setOnClickListener(this);
        btBank.setOnClickListener(this);
        btPlay.setOnClickListener(this);
        btStat.setOnClickListener(this);



        Log.d("USER", MainActivity.mainemail);
        textUser.setText(MainActivity.mainemail);
        // Got from stack overflow
        textBank.setText("Bank: " + MainActivity.bank_amount + " Gold");
        getProfilePicture();
    }


    @Override
    public void onClick(View view) {
        if (view == btBank) {
            updateBank();

        } else if (view == btCommunity) {
            startActivity(new Intent(MenuActivity.this, CommunityActivity.class));
        } else if (view == btPlay) {
            startActivity(new Intent(MenuActivity.this, MainActivity.class));
        } else if (view == btStat) {
            startActivity(new Intent(MenuActivity.this, StatisticsActivity.class));
        }
    }

    private void updateBank() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View inflate_view = layoutInflater.inflate(R.layout.bank_box, null);
        builder.setMessage("BANKING!")
                .setView(inflate_view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Deposit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText etBank = inflate_view.findViewById(R.id.etBank);
                        number = Integer.parseInt(etBank.getText().toString());

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference dRef = db.collection("User").document(MainActivity.mainemail);
                        dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Map<String, Object> data = new HashMap<>();


                                HashMap<String, Double> wallet = (HashMap<String, Double>) task.getResult().getData().get("wallet");
                                Long amount_banked = (Long) task.getResult().getData().get("amount_banked");

                                Log.d(TAG, wallet.toString());
                                if (!wallet.isEmpty()) {
                                    if (!(amount_banked>25)) {
                                        if(number<=wallet.size() && number>=0) {

                                            Map<String, Double> sortedWallet = new LinkedHashMap<>();
                                            wallet.entrySet()
                                                    .stream()
                                                    .sorted(comparingByValue(reverseOrder()))
                                                    .forEachOrdered(x -> sortedWallet.put(x.getKey(), x.getValue()));

                                            int count = 0;
                                            bank_amount = 0.0;
                                            bank_amount_final = 0L;
                                            for (String key : sortedWallet.keySet()) {
                                                if(count==number) {
                                                    break;
                                                }
                                                bank_amount += sortedWallet.get(key);
                                                count++;
                                                wallet.remove(key);
                                            }
                                            Log.d(TAG, bank_amount.toString());

                                            bank_amount_final = Math.round(bank_amount);

                                            Long bank = (Long) task.getResult().getData().get("bank");
                                            bank += bank_amount_final;

                                            amount_banked = number.longValue();

                                            data.put("amount_banked", amount_banked);
                                            data.put("bank", bank);
                                            data.put("wallet", wallet);
                                            dRef.update(data);

                                            textBank.setText("Bank: " + bank.toString() + " Gold");

                                        } else {
                                            Toast.makeText(MenuActivity.this, "Can't bank " + number + " coins because either amount is negative or number exceeds wallet size.",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(MenuActivity.this, "You've already banked 25 coins today!",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Toast.makeText(MenuActivity.this, "Your wallet is empty.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .show();
    }

    public void getProfilePicture() {
        userProfile = (ImageView) findViewById(R.id.userImage);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("User").document(MainActivity.mainemail);
        dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(!task.getResult().getData().get("user_image").toString().isEmpty()) {
                    String profile_url = task.getResult().getData().get("user_image").toString();


                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReference();

                    StorageReference path = storageReference.child(profile_url);

                    final long one_megabyte = 1024*1024;

                    path.getBytes(one_megabyte).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                            Bitmap bitmap;
                            bitmap = BitmapFactory.decodeStream(inputStream);
                            userProfile.setImageBitmap(bitmap);
                        }
                    });
                } else {
                    userProfile.setImageDrawable(getDrawable(R.drawable.ic_user));
                }


            }
        });


    }


}
