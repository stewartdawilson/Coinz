package com.example.s1636431.coinz;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import timber.log.Timber;

/*
    Adapter for the friends list fragment. Responsible for displaying all the information on the page. Its given data
    which is sent from the FriendsListFragment in the form of a ArrayList of Hashmaps which hold the friends email as the key
    and the image as the value.
 */
public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.ViewHolder> {

    private ItemClickListener mClickListener;
    private ArrayList<HashMap<String, String>> mData;
    private LayoutInflater layoutInflater;
    private Context context;

    private String TAG = "FriendsListAdapter";

    FriendsListAdapter(Context context, ArrayList<HashMap<String, String>> data) {
        this.context = context;
        this.mData = data;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.friend_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    // Function displays the data passed into the Adapter by the Fragment
    public void onBindViewHolder( ViewHolder holder, int position) {
        HashMap<String, String> user = mData.get(position);


        // Works same way as previous image retrieval has worked, see MenuActivity and the getProfilePicture
        // function for more info
        if (!Objects.requireNonNull(user.get("user_image")).isEmpty()) {
            String image_url = user.get("user_image");

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference();

            StorageReference path = null;
            if (image_url != null) {
                path = storageReference.child(image_url);
            }

            final long one_megabyte = 1024 * 1024;

            if (path != null) {
                path.getBytes(one_megabyte).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                        Bitmap bitmap;
                        bitmap = BitmapFactory.decodeStream(inputStream);
                        displayUser(holder, bitmap, user);
                    }

                });
            }

        } else {
            Bitmap default_image = FriendsListFragment.drawableToBitmap(Objects.requireNonNull(context.getDrawable(R.drawable.ic_user)));
            displayUser(holder, default_image, user);
        }
    }

    private void displayUser(ViewHolder holder, Bitmap image, HashMap<String, String> user) {
        String email = user.get("email");

        holder.friendImage.setImageBitmap(image);
        holder.email.setText(email);
        // Set up Click listener for the add friend button
        holder.ebtAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriend(email, user);
            }
        });
        // Set up Click listener for the remove friend button
        holder.ebtRemoveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFriend(email, user);
            }
        });
        // Set up Click listener for trading button
        holder.btTrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TradeActivity.class);
                intent.putExtra("email", email); // Store the email of the user he wishes to trade with in the intent
                context.startActivity(intent); // Go to trading screen

            }
        });
        Log.d(TAG, String.format("Adding user: %s to page: ", email));
    }

    private void addFriend(String email, HashMap<String, String> user) {
        // Get user info from firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("User").document(MainActivity.mainemail);
        dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                HashMap<String, Object> friends_data = new HashMap<>();
                try {
                    ArrayList<HashMap<String, String>> friends = (ArrayList<HashMap<String, String>>) task.getResult().getData().get("friends");
                    Log.d(TAG, email);
                    if(friends!=null) {
                        // If the player has no friends then add the user.
                        if (friends.isEmpty()) {
                            friends.add(user);
                            friends_data.put("friends", friends);
                            dRef.set(friends_data, SetOptions.merge()); // add friend
                            Toast.makeText(context, R.string.toastAddedFriend, Toast.LENGTH_LONG).show();
                        } else {
                            // Check to see if the player is already friends with user
                            if(friends.contains(user)) {
                                Toast.makeText(context, R.string.toastAlreadyFriend, Toast.LENGTH_LONG).show();
                            } else {
                                friends.add(user);
                                friends_data.put("friends", friends);
                                dRef.update(friends_data); // add friend
                                Toast.makeText(context, R.string.toastAddedFriend, Toast.LENGTH_LONG).show();
                            }

                        }

                    }
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void removeFriend(String email, HashMap<String, String> user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("User").document(MainActivity.mainemail);
        dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                HashMap<String, Object> friends_data = new HashMap<>();
                try {
                    ArrayList<HashMap<String, String>> friends = (ArrayList<HashMap<String, String>>) task.getResult().getData().get("friends");
                    Log.d(TAG, email);
                    if(friends!=null) {
                        // Check to see if the player has friends
                        if(!friends.isEmpty()) {
                            // Check if user is already friends
                            if (friends.contains(user)) {
                                friends.remove(user);
                                friends_data.put("friends", friends);
                                dRef.update(friends_data);
                                Toast.makeText(context, R.string.toastRemovedFriend, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, R.string.toastIsntFriends, Toast.LENGTH_LONG).show(); // If the user wasn't friends then display message
                            }

                        } else {
                            Toast.makeText(context, R.string.toastNoFriends, Toast.LENGTH_LONG).show();

                        }
                    }
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView email;
        ImageView friendImage;
        FloatingActionButton ebtAddUser, ebtRemoveUser;
        Button btTrade;


        ViewHolder(View userView) {
            super(userView);
            email = userView.findViewById(R.id.txEmailRow);
            friendImage = userView.findViewById(R.id.friendImage);
            ebtAddUser = userView.findViewById(R.id.abtAddfriend);
            ebtRemoveUser = userView.findViewById(R.id.abtRemovefriend);
            btTrade = userView.findViewById(R.id.btTrade);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mClickListener != null) {
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
