package com.example.s1636431.coinz;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class LeaderBoardFragment extends Fragment {

    private static final String TAG = "LeaderBoardFragment";

    RecyclerView leaderboard;
    Spinner spinner;
    private List<String> spinnerData;

    private ArrayList<HashMap<String, Object>> data = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.leaderboard_fragment,container,false);



        leaderboard = (RecyclerView) view.findViewById(R.id.leaderboard);
        spinner = (Spinner) view.findViewById(R.id.sorter);
        renderSpinner(spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String criteria = parent.getItemAtPosition(pos).toString();
                if(criteria.equals("Distance")) {
                    criteria = "distance";
                } else if(criteria.equals("Gold")) {
                    criteria = "wallet";
                }
                setLeaderBoard(criteria);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        leaderboard.setLayoutManager(new LinearLayoutManager(getContext()));
        LeaderBoardAdapter leaderBoardAdapter = new LeaderBoardAdapter(getContext(), data);

        leaderboard.setAdapter(leaderBoardAdapter);

        return view;
    }

    private void setLeaderBoard(String criteria) {
        ArrayList<HashMap<String, String>> leaderboard_data = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query dRef = db.collection("User").orderBy(criteria);
        dRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                Log.d(TAG, task.getResult().getDocuments().toArray().toString());
                List<DocumentSnapshot> users = task.getResult().getDocuments();
                for(DocumentSnapshot user : users) {
                    String email = user.getData().get("email").toString();
                    Object dist = user.getData().get("distance");
                    HashMap<String, Object> user_data = new HashMap<>();
                    user_data.put(email, dist);
                    data.add(user_data);
                }
            }
        });

    }


    public void renderSpinner(Spinner spinner) {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.options_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }
}