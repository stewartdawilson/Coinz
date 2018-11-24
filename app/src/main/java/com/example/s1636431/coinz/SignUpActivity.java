package com.example.s1636431.coinz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {


    private FirebaseAuth mAuth;
    EditText etEmail, etPass;
    Button btSignUp;
    Button btAlreadyAccount;

    static public String emailID;

// in the onCreate method


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_signup);

        etEmail = (EditText) findViewById(R.id.etRemail);
        etPass = (EditText) findViewById(R.id.etRpass);

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

    public void createAccount() {
        String email = etEmail.getText().toString().trim();
        String password = etPass.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPass.setError("Password is required");
            etPass.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPass.setError("Minimum length of password should be 6");
            etPass.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SIGN IN", "createUserWithEmail:success");
                            Toast.makeText(SignUpActivity.this, "Registration Successful",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            emailID = email;

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference dRef = db.collection("User").document(email);

                            Map<String, Object> data = new HashMap<>();
                            HashMap<String, Double> wallet = new HashMap<>();
                            ArrayList<String> collected =  new ArrayList<>();
                            ArrayList<HashMap<String, String>> friends =  new ArrayList<>();
                            String user_image = "";

                            data.put("wallet", wallet);
                            data.put("collected", collected);
                            data.put("friends", friends);
                            data.put("user_image", user_image);
                            dRef.set(data, SetOptions.merge());

                            startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("SIGN IN", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}

