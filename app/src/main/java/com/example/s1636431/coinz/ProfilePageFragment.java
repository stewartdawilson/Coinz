package com.example.s1636431.coinz;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;


public class ProfilePageFragment extends Fragment {

    private static final String TAG = "ProfilePageFragment";

    ImageView userProfile;
    TextView txEmail;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profilepage_fragment,container,false);


        txEmail = (TextView) view.findViewById(R.id.txProfileEmail);
        txEmail.setText(MainActivity.mainemail);
        getProfilePicture(view);


        return view;
    }

    public void getProfilePicture(View view) {
        userProfile = (ImageView) view.findViewById(R.id.userProfileImage);

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
                    userProfile.setImageDrawable(getContext().getDrawable(R.drawable.ic_user));
                }
            }
        });


    }

}