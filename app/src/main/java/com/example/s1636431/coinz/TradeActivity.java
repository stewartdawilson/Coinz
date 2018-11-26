package com.example.s1636431.coinz;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.mapbox.geojson.Feature;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.Entry.comparingByValue;

public class TradeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TradeActivity";

    ImageView friendImage;
    TextView txEmail;
    Button btTrade;

    private String email;
    private int number;
    private Double trade_amount = 0.0;
    private Long trade_amount_final;
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

        btTrade =  (Button) findViewById(R.id.btTradePage);

        btTrade.setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View inflate_view = layoutInflater.inflate(R.layout.trade_box, null);
        builder.setMessage("TRADING!")
                .setView(inflate_view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Trade", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText etTrade = inflate_view.findViewById(R.id.etTrade);
                        number = Integer.parseInt(etTrade.getText().toString());

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference dRef = db.collection("User").document(MainActivity.mainemail);
                        dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Map<String, Object> data = new HashMap<>();


                                HashMap<String, Double> wallet = (HashMap<String, Double>) task.getResult().getData().get("wallet");
                                Log.d(TAG, wallet.toString());
                                if (!wallet.isEmpty()) {
                                    if(number<=wallet.size() && number>=0) {

                                        Map<String, Double> sortedWallet = new LinkedHashMap<>();
                                        wallet.entrySet()
                                                .stream()
                                                .sorted(comparingByValue())
                                                .forEachOrdered(x -> sortedWallet.put(x.getKey(), x.getValue()));


                                        int count = 0;
                                        trade_amount = 0.0;
                                        trade_amount_final = 0L;
                                        for (String key : sortedWallet.keySet()) {
                                            if(count==number) {
                                                break;
                                            }
                                            trade_amount += sortedWallet.get(key);
                                            count++;
                                            wallet.remove(key);
                                        }
                                        Log.d(TAG, trade_amount.toString());

                                        trade_amount_final = Math.round(trade_amount);


                                        data.put("wallet", wallet);
                                        dRef.update(data);



                                        DocumentReference dRef = db.collection("User").document(email);
                                        dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                Map<String, Object> data = new HashMap<>();

                                                Long bank = (Long) task.getResult().getData().get("bank");
                                                bank += trade_amount_final;

                                                data.put("bank", bank);
                                                dRef.set(data, SetOptions.merge());
                                            }
                                        });

                                    } else {
                                        Toast.makeText(TradeActivity.this, "Can't trade " + number + " coins because either amount is negative or number exceeds wallet size.",
                                                Toast.LENGTH_LONG).show();

                                    }


                                } else {
                                    Toast.makeText(TradeActivity.this, "Your wallet is empty.",
                                            Toast.LENGTH_SHORT).show();
                                }




                            }
                        });
                    }
                })
                .show();
    }
}
