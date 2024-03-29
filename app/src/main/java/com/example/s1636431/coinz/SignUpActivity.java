package com.example.s1636431.coinz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*
    Sign up page activity, used when user first create an account.
 */
public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {


    private FirebaseAuth mAuth;
    private EditText etEmail;
    private EditText etPass;
    private EditText etHeight;
    private EditText etWeight;
    private Button btSignUp;
    private Button btAlreadyAccount;

    static public String emailID;
    static public Boolean signedIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_signup);

        etEmail = (EditText) findViewById(R.id.etRemail);
        etPass = (EditText) findViewById(R.id.etRpass);
        etHeight = (EditText) findViewById(R.id.etRheight);
        etWeight = (EditText) findViewById(R.id.etRweight);

        btSignUp = (Button) findViewById(R.id.btSignUp);
        btAlreadyAccount = (Button) findViewById(R.id.btAlreadyAccount);

        btSignUp.setOnClickListener(this);
        btAlreadyAccount.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == btSignUp) {
            createAccount();
        }
        if (view == btAlreadyAccount) {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        }
    }

    @SuppressLint("SimpleDateFormat")
    public void createAccount() {
        String email = etEmail.getText().toString().trim();
        String password = etPass.getText().toString().trim();
        String height_text = etHeight.getText().toString().trim();
        String weight_text = etWeight.getText().toString().trim();

        // Make sure emails not empty
        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }
        // Make sure passwords not empty
        if (password.isEmpty()) {
            etPass.setError("Password is required");
            etPass.requestFocus();
            return;
        }

        // Make sure heights not empty
        if (height_text.isEmpty()) {
            etHeight.setError("Height is required");
            etHeight.requestFocus();
            return;
        }

        // Make sure weights not empty
        if (weight_text.isEmpty()) {
            etWeight.setError("Weight is required");
            etWeight.requestFocus();
            return;
        }

        // Make sure passwords length is at least 6
        if (password.length() < 6 && !password.isEmpty()) {
            etPass.setError("Minimum length of password should be 6");
            etPass.requestFocus();
            return;
        }

        // Make sure email is valid
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Valid email is required");
            etEmail.requestFocus();
            return;
        }



        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, create new user in firebase with fields add initial values
                            Log.d("SIGN IN", "createUserWithEmail:success");

                            emailID = email;
                            signedIn = true;
                            LoginActivity.loggedIn = false;

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference dRef = db.collection("User").document(email);

                            Map<String, Object> data = new HashMap<>();
                            HashMap<String, Double> wallet = new HashMap<>();
                            ArrayList<String> collected =  new ArrayList<>();
                            ArrayList<HashMap<String, String>> friends =  new ArrayList<>();
                            Double distance = 0.0;
                            Double gold = 0.0;
                            Integer bank = 0;
                            Integer coins_collected = 0;
                            Integer amount_banked = 0;
                            Double height= Double.parseDouble(height_text);
                            Integer weight = Integer.parseInt(weight_text);
                            String user_image = "";
                            Date last_login = new Date();
                            String modifiedDate= new SimpleDateFormat("yyyy/MM/dd").format(last_login);


                            data.put("wallet", wallet);
                            data.put("collected", collected);
                            data.put("friends", friends);
                            data.put("user_image", user_image);
                            data.put("distance", distance);
                            data.put("gold_alltime", gold);
                            data.put("bank", bank);
                            data.put("weight", weight);
                            data.put("height", height);
                            data.put("amount_banked", amount_banked);
                            data.put("coins_collected", coins_collected);
                            data.put("last_login", modifiedDate);

                            dRef.set(data, SetOptions.merge()); // Add user to firebase
                            Toast.makeText(SignUpActivity.this, R.string.toastSignUpSuccess,
                                    Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(SignUpActivity.this,ImageActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("SIGN IN", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, R.string.toastSignUpFail,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}

