package com.example.s1636431.coinz;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
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
    private ArrayList<HashMap<String, Bitmap>> mData;
    private LayoutInflater layoutInflater;
    private Context context;

    private String TAG = "FriendsListAdapter";

    FriendsListAdapter(Context context, ArrayList<HashMap<String, Bitmap>> data) {
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
        HashMap<String, Bitmap> user = mData.get(position);

        // Iterates over each friend in the array and displays them on the friends list
        for ( String key : user.keySet() ) {
            Bitmap image = user.get(key);
            holder.friendImage.setImageBitmap(image);
            holder.email.setText(key);
            // Set up Click listener for the add friend button
            holder.ebtAddUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get user info from firebase
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference dRef = db.collection("User").document(MainActivity.mainemail);
                    dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            Boolean not_friend = false;
                            HashMap<String, Object> friends_data = new HashMap<>();
                            ArrayList<HashMap<String, String>> friends = (ArrayList<HashMap<String, String>>) task.getResult().getData().get("friends");
                            Timber.tag(TAG).d(key);
                            if(friends!=null) {
                                // If the player has no friends then add the user.
                                if (friends.isEmpty()) {
                                    friends_data.put("friends", FriendsListFragment.searched_user);
                                    dRef.set(friends_data, SetOptions.merge());
                                    Toast.makeText(context, "Added as friend!", Toast.LENGTH_SHORT).show();
                                }
                                // Check to see if the player is already friends with user
                                for(HashMap<String, String> friend : friends) {

                                    FriendsListFragment.searched_user.add(friend);
                                    if (!Objects.equals(friend.get("email"), key)) {
                                        not_friend = true;
                                    } else {
                                        not_friend = false;
                                        Toast.makeText(context, "Already friend!", Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                }
                                // If not friends then add the user
                                if(not_friend) {
                                    friends_data.put("friends", FriendsListFragment.searched_user);
                                    dRef.update(friends_data);
                                    Toast.makeText(context, "Added as friend!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            });
            // Set up Click listener for the remove friend button
            holder.ebtRemoveUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference dRef = db.collection("User").document(MainActivity.mainemail);
                    dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            Boolean is_friend = false;
                            HashMap<String, Object> friends_data = new HashMap<>();
                            ArrayList<HashMap<String, String>> friends = (ArrayList<HashMap<String, String>>) task.getResult().getData().get("friends");
                            Timber.tag(TAG).d(key);
                            if(friends!=null) {
                                // Check to see if the player has friends
                                if(!friends.isEmpty()) {
                                    // Iterate over every friend the player has and see if he's friends with the user
                                    for(HashMap<String, String> friend : friends) {

                                        if (Objects.equals(friend.get("email"), key)) {
                                            is_friend = true;
                                            friends.remove(friend);
                                            friends_data.put("friends", friends);
                                            dRef.update(friends_data);
                                            Toast.makeText(context, "Removed friend!", Toast.LENGTH_SHORT).show();
                                            break;
                                        } else {
                                            is_friend = false;
                                        }
                                    }
                                    // If the user wasn't friends then display message
                                    if(!is_friend) {
                                        Toast.makeText(context, "User isn't friends with you.", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Toast.makeText(context, "You don't have any friends!", Toast.LENGTH_SHORT).show();

                                }
                            }
                        }
                    });

                }
            });

            // Set up Click listener for trading button
            holder.btTrade.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, TradeActivity.class);
                    intent.putExtra("email", key); // Store the email of the user he wishes to trade with in the intent
                    context.startActivity(intent); // Go to trading screen

                }
            });

            Timber.tag(TAG).d("Adding user: " + key + " to page: ");
        }



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
