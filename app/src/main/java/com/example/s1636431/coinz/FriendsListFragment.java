package com.example.s1636431.coinz;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FriendsListFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "FriendsListFragment";
    private FirebaseAuth mAuth;

    SearchView search_list;

    RecyclerView friendlist;

    private ArrayList<HashMap<String, Bitmap>> data = new ArrayList<>();

    private FriendsListAdapter friendadapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friendslist_fragment,container,false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("User").document(MainActivity.mainemail);

        getUsers();
        Log.d(TAG, data.toString());
        search_list = (SearchView) view.findViewById(R.id.search_list);
        friendlist = (RecyclerView) view.findViewById(R.id.friendlist);

        search_list.setOnSearchClickListener(this);

        friendlist.setLayoutManager(new LinearLayoutManager(getContext()));
        friendadapter = new FriendsListAdapter(getContext(), data);

        friendlist.setAdapter(friendadapter);

        return view;
    }


    @Override
    public void onClick(View v) {
        if(v== search_list) {
            search_user();
        }
    }

    private void search_user() {


    }

    public void getUsers(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("User")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                HashMap<String, Bitmap> user = new HashMap<>();

                                if (document.get("profile_image") != null) {

                                    String profile_url = document.get("profile_image").toString();


                                    FirebaseStorage storage = FirebaseStorage.getInstance();
                                    StorageReference storageReference = storage.getReference();

                                    StorageReference path = storageReference.child(profile_url);

                                    final long one_megabyte = 1024 * 1024;



                                    path.getBytes(one_megabyte).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                        @Override
                                        public void onSuccess(byte[] bytes) {
                                            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                                            Bitmap bitmap;
                                            if (inputStream != null) {
                                                bitmap = BitmapFactory.decodeStream(inputStream);
                                                user.put(document.getId(),bitmap);
                                                data.add(user);

                                            }
                                        }

                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            int errorCode = ((StorageException) exception).getErrorCode();
                                            String errorMessage = exception.getMessage();
                                            Log.d(TAG, errorMessage + errorCode);
                                        }
                                    });

                                } else {
                                    user.put(document.getId(),null);
                                    Log.d(TAG, "Adding user: " + document.getId() + " to data: " + data.toString());
                                    data.add(user);
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }
}