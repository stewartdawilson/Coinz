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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class LeaderBoardFragment extends Fragment {

    private static final String TAG = "LeaderBoardFragment";

    RecyclerView leaderboard;
    Spinner spinner;
    private List<String> spinnerData;

    private ArrayList<HashMap<String, String>> data = new ArrayList<>();
    private ArrayAdapter<CharSequence> adapter;

    LeaderBoardAdapter leaderBoardAdapter;


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
                    setLeaderBoard(criteria);

                } else if(criteria.equals("Gold")) {
                    criteria = "gold_alltime";
                    setLeaderBoard(criteria);

                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;

            }
        });
        Log.d(TAG, String.format("DATA: %s", data.toString()));



        leaderboard.setLayoutManager(new LinearLayoutManager(getContext()));
        leaderBoardAdapter = new LeaderBoardAdapter(getContext(), data);

        leaderboard.setAdapter(leaderBoardAdapter);

        return view;
    }

    private void setLeaderBoard(String criteria) {
        ArrayList<HashMap<String, String>> leaderboard_data = new ArrayList<>();
        data.clear();
        leaderboard_data.clear();


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query dRef = db.collection("User").orderBy(criteria);
        dRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {


                Log.d(TAG, task.getResult().getDocuments().toString());
                Log.d(TAG, criteria);
                List<DocumentSnapshot> users = task.getResult().getDocuments();
                for(DocumentSnapshot user : users) {
                    String email = user.getId();
                    Log.d(TAG, String.format("EMAIL: %s", email));

                    HashMap<String, String> user_data = new HashMap<>();

                    String value = user.getData().get(criteria).toString();
                    user_data.put(email, value);

                    Log.d(TAG, String.format("RANKING BY: %s", value));
                    Log.d(TAG, "DATA " + user_data);

                    leaderboard_data.add(user_data);
                    data.add(user_data);
                }
                Collections.reverse(data);
                leaderBoardAdapter.notifyDataSetChanged();
                adapter.notifyDataSetChanged();

            }
        });

    }


    public void renderSpinner(Spinner spinner) {

        adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.options_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }
}