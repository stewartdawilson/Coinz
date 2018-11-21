package com.example.s1636431.coinz;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;


public class FriendsListFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "FriendsListFragment";
    private FirebaseAuth mAuth;

    SearchView search_list;

    RecyclerView friendlist;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friendslist_fragment,container,false);


        mAuth = FirebaseAuth.getInstance();

        search_list = (SearchView) view.findViewById(R.id.search_list);
        friendlist = (RecyclerView) view.findViewById(R.id.friendlist);

        search_list.setOnSearchClickListener(this);






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
}