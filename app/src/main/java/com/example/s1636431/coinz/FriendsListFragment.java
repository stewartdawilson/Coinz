package com.example.s1636431.coinz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import timber.log.Timber;

/*
    Fragment for the friendslist. Get's the friends the player has from firebase, then sends them to
    the FriendsListAdapter to be displayed.
 */
public class FriendsListFragment extends Fragment {

    private static final String TAG = "FriendsListFragment";

    SearchView search_list;

    RecyclerView friendlist;

    private ArrayList<HashMap<String, String>> data = new ArrayList<>();

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


        search_list = (SearchView) view.findViewById(R.id.search_list);
        friendlist = (RecyclerView) view.findViewById(R.id.friendlist);


        // Set up search query listener
        search_list.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // If the player searches his own name, do nothing.
                // Otherwise proceed with search
                if ( query.equals(MainActivity.mainemail)) {
                    return false;
                } else {
                    search_user(query, getContext());
                    search_list.clearFocus();
                    return false;
                }
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                data.clear();
                // Check if the search box is empty, if the data is empty and addFriendsList is not running.
                // This is used to redisplay the friends once the player has finished searching.
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

    /*
        Function responsible for getting the information to the display the searched user
        on the friends list recyclerview
     */
    private void search_user(String query, Context context) {
        searched_user.clear();

        if (!query.isEmpty()) {
            data.clear();

            // Get firebase info for player
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference dRef = db.collection("User").document(query);
            dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    // Check if searched user exists
                    if (Objects.requireNonNull(task.getResult()).exists()) {
                        HashMap<String, String> user = new HashMap<>();
                        user.put("email", query);

                        String search_image_url = task.getResult().getData().get("user_image").toString();
                        user.put("user_image", search_image_url);
                        data.add(user);
                        friendadapter.notifyDataSetChanged(); // notify adapter the data has changed

                    } else {
                        Toast.makeText(getContext(), "User doesn't exist!", Toast.LENGTH_SHORT).show();
                        Timber.tag(TAG).d("Searched User that doesn't exist");
                    }

                }
            });
        } else {
            Timber.tag(TAG).d("Search empty");
        }
    }

    /*
    Function to convert drawable to bitmap. From:
     https://stackoverflow.com/questions/3035692/how-to-convert-a-drawable-to-a-bitmap
     */
    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap;

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

    /*
        Get the all the friends the player has from firebase and store them in an ArrayList
    */
    private void getFriends(Context context) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("User").document(MainActivity.mainemail);
        dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                ArrayList<HashMap<String, String>> friends = (ArrayList<HashMap<String, String>>) task.getResult().getData().get("friends");
                if (friends != null) {
                    Timber.tag(TAG).d("Getting friends: %s", friends.toString());
                }
                addFriendsList(friends, context);
                task_running = false;
            }
        });
    }

    /*
        Function responsible for adding friends player has to friendslist recyclerview.
     */
    public void addFriendsList(ArrayList<HashMap<String, String>> friendsArray, Context context){

        // Check if player has friends
        if (friendsArray.isEmpty()) {
            data.clear();
            friendadapter.notifyDataSetChanged();
        } else {
            // Iterate over friends, then add them to data
            for(int i=0; i<friendsArray.size(); i++) {
                HashMap<String, String> friend = friendsArray.get(i);
                HashMap<String, String> friend_data = new HashMap<>();

                String friend_email = friend.get("email");
                String image_url = friend.get("user_image");

                friend_data.put("email",friend_email);
                friend_data.put("user_image",image_url);

                data.add(friend_data); // add friend to data to be sent to adapter
                Timber.tag(TAG).d("Adding user: " + friend_email+ " to data: " + data.toString());
                friendadapter.notifyDataSetChanged(); // notify adapter the data has changed

            }
        }

    }
}