package com.example.s1636431.coinz;

import android.content.Intent;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;

    private EditText etEmail;
    private EditText etPass;
    private Button btLogin;
    private Button btCreateAccount;

    static public String emailID;
    static public Boolean loggedIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);

        etEmail = (EditText) findViewById(R.id.etLemail);
        etPass = (EditText) findViewById(R.id.etLpass);

        btLogin = (Button) findViewById(R.id.btLogin);
        btCreateAccount = (Button) findViewById(R.id.btCreateAccount);

        btLogin.setOnClickListener(this);
        btCreateAccount.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view==btLogin) {
            loginIn();
        }
        if(view==btCreateAccount){
            startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
        }
    }

    private void loginIn() {

        String email = etEmail.getText().toString().trim();
        String password = etPass.getText().toString().trim();

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

        // Make sure passwords length is at least 6
        if (password.length() < 6) {
            etPass.setError("Minimum length of password should be 6");
            etPass.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("SIGN IN", "signInWithEmail:success");
                            emailID = email;
                            loggedIn = true;
                            SignUpActivity.signedIn = false;


                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference dRef = db.collection("User").document(email);

                            Map<String, Object> data = new HashMap<>();
                            Date last_login = new Date();
                            String modifiedDate= new SimpleDateFormat("yyyy/MM/dd").format(last_login);


                            data.put("last_login", modifiedDate); // update last login date


                            dRef.set(data, SetOptions.merge());
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("SIGN IN", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

}