package com.example.s1636431.coinz;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ImageActivity extends AppCompatActivity implements View.OnClickListener {

    Button btUpload, btGo;
    ImageView imgUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imgUpload = (ImageView) findViewById(R.id.imgUpload);


        btUpload = (Button) findViewById(R.id.btUpload);
        btGo = (Button) findViewById(R.id.btGo);
        btGo.setOnClickListener(this);
        btUpload.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.equals(btGo)) {
            startActivity(new Intent(ImageActivity.this, MainActivity.class));
        } else if(v.equals(btUpload)) {
            startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), 1);
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data_intent) {
        super.onActivityResult(requestCode, resultCode, data_intent);


        //Detects request codes
        if(requestCode==1 && resultCode == ImageActivity.RESULT_OK) {
            Uri selectedImage = data_intent.getData(); // get image from users storage
            Bitmap bitmap = null;
            try {

                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage); // store selected image as bitmap

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference dRef = db.collection("User").document(SignUpActivity.emailID);

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                StorageReference imgRef = storageRef.child(SignUpActivity.emailID.replaceAll("\\.|com", "") + ".jpg"); // using regex to remove "." and "com"


                ByteArrayOutputStream b =  new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,50,b);
                byte[] to_upload  = b.toByteArray();


                Map<String, Object> data = new HashMap<>();
                data.put("user_image", SignUpActivity.emailID.replaceAll("\\.|com", "")+ ".jpg");

                dRef.set(data, SetOptions.merge()); // Add image to firebase


                UploadTask uploadTask = imgRef.putBytes(to_upload); // upload image to firebase storage
                imgUpload.setImageBitmap(bitmap);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(ImageActivity.this, "Upload success!", Toast.LENGTH_SHORT).show();
                    }
                });


            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}