package com.example.s1636431.coinz;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
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
import android.widget.Toast;

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
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FriendsListFragment extends Fragment {

    private static final String TAG = "FriendsListFragment";
    private FirebaseAuth mAuth;

    SearchView search_list;

    RecyclerView friendlist;

    private ArrayList<HashMap<String, Bitmap>> data = new ArrayList<>();

    static public ArrayList<HashMap<String, String>> searched_user = new ArrayList<>();

    private FriendsListAdapter friendadapter;

    public Boolean task_running = true;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friendslist_fragment,container,false);


        task_running = true;
        data.clear();
        getFriends(getContext());

        Log.d(TAG, data.toString());


        search_list = (SearchView) view.findViewById(R.id.search_list);
        friendlist = (RecyclerView) view.findViewById(R.id.friendlist);


        search_list.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search_user(query, getContext());
                search_list.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                data.clear();
                if (newText.isEmpty() && (data.isEmpty()) && !task_running ) {
                    getFriends(getContext());
                }
                return false;
            }
        });
        search_list.clearFocus();




        friendlist.setLayoutManager(new LinearLayoutManager(getContext()));
        friendadapter = new FriendsListAdapter(getContext(), data);

        friendlist.setAdapter(friendadapter);

        return view;
    }


    private void search_user(String query, Context context) {
        searched_user.clear();

        String searched_email = query;
        if (!searched_email.isEmpty()) {
            data.clear();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference dRef = db.collection("User").document(searched_email);
            dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.getResult().exists()) {
                        HashMap<String, String> user = new HashMap<>();
                        user.put("email", searched_email);

                        HashMap<String, Bitmap> user_data = new HashMap<>();
                        if (!task.getResult().getData().get("user_image").toString().isEmpty()) {

                            String search_image_url = task.getResult().getData().get("user_image").toString();
                            user.put("user_image", search_image_url);
                            searched_user.add(user);

                            Log.d(TAG, "Getting user: " + searched_email);

                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageReference = storage.getReference();

                            StorageReference path = storageReference.child(search_image_url);

                            final long one_megabyte = 1024 * 1024;

                            path.getBytes(one_megabyte).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                                    Bitmap bitmap;
                                    if (inputStream != null) {
                                        bitmap = BitmapFactory.decodeStream(inputStream);
                                        user_data.put(searched_email, bitmap);
                                        data.add(user_data);
                                        Log.d(TAG, "Adding searched user: " + searched_email + " to data: " + data.toString());
                                        friendadapter.notifyDataSetChanged();
                                    }
                                }

                            });

                        } else {
                            user.put("user_image", "");
                            searched_user.add(user);
                            Bitmap default_image = drawableToBitmap(context.getDrawable(R.drawable.ic_user));
                            user_data.put(searched_email, default_image);
                            Log.d(TAG, "Adding searched user: " + searched_email + " to data: " + data.toString());
                            data.add(user_data);
                            friendadapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(getContext(), "User doesn't exist!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Searched User that doesn't exist");
                    }

                }
            });
        } else {
            Log.d(TAG, "Search empty");
        }
    }

    /*
    Function to convert drawable to bitmap. From:
     https://stackoverflow.com/questions/3035692/how-to-convert-a-drawable-to-a-bitmap
     */
    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void getFriends(Context context) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("User").document(MainActivity.mainemail);
        dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                ArrayList<HashMap<String, String>> friends = (ArrayList<HashMap<String, String>>) task.getResult().getData().get("friends");
                Log.d(TAG, "Getting friends: " + friends.toString());
                addFriendsList(friends, context);
                task_running = false;
            }
        });
    }

    public void addFriendsList(ArrayList<HashMap<String, String>> friendsArray, Context context){


        if (friendsArray.isEmpty()) {

            data.clear();
            friendadapter.notifyDataSetChanged();



        } else {
            for(int i=0; i<friendsArray.size(); i++) {
                HashMap<String, String> friend = friendsArray.get(i);
                HashMap<String, Bitmap> friend_data = new HashMap<>();

                String friend_email = friend.get("email");
                if (!friend.get("user_image").isEmpty()) {

                    String image_url = friend.get("user_image");

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReference();

                    StorageReference path = storageReference.child(image_url);

                    final long one_megabyte = 1024 * 1024;

                    path.getBytes(one_megabyte).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                            Bitmap bitmap;
                            if (inputStream != null) {
                                bitmap = BitmapFactory.decodeStream(inputStream);
                                friend_data.put(friend_email,bitmap);
                                data.add(friend_data);
                                Log.d(TAG, "Adding user: " + friend.get("email") + " to data: " + data.toString());
                                friendadapter.notifyDataSetChanged();

                            }
                        }

                    });

                } else {
                    Bitmap default_image = drawableToBitmap(context.getDrawable(R.drawable.ic_user));
                    friend_data.put(friend_email,default_image);
                    Log.d(TAG, "Adding user: " + friend.get("email") + " to data: " + data.toString());
                    data.add(friend_data);
                    friendadapter.notifyDataSetChanged();
                }


            }
        }

    }
}