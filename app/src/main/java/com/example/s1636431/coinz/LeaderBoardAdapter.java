package com.example.s1636431.coinz;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.icu.text.DecimalFormat;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import timber.log.Timber;

public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.ViewHolder> {

    private LeaderBoardAdapter.ItemClickListener mClickListener;
    private ArrayList<HashMap<String, String>> mData;
    private LayoutInflater layoutInflater;
    private Context context;

    private String TAG = "LeaderBoardAdapter";

    LeaderBoardAdapter(Context context, ArrayList<HashMap<String, String>> data) {
        this.context = context;
        this.mData = data;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public LeaderBoardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.leaderboard_row, parent, false);

        return new LeaderBoardAdapter.ViewHolder(view);
    }

    @Override
    @SuppressLint("DefaultLocale")
    public void onBindViewHolder(LeaderBoardAdapter.ViewHolder holder, int position) {
        HashMap<String, String> user = mData.get(position);
        Log.d(TAG,user.toString());

        for ( String key : user.keySet() ) {
            Log.d(TAG, Integer.toString(position+1));
            holder.rank.setText(Integer.toString(position+1));
            holder.user.setText(key);
            Log.d(TAG, user.get(key));
            String result = String.format("%.2f", Double.parseDouble(user.get(key)));
            holder.criteria.setText(result);

            Log.d(TAG,"Adding user: " + key + " to page: ");

        }



    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView rank;
        TextView user;
        TextView criteria;


        ViewHolder(View userView) {
            super(userView);
            rank = userView.findViewById(R.id.txRank);
            user = userView.findViewById(R.id.txLeaderUser);
            criteria = userView.findViewById(R.id.txLeaderValue);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mClickListener != null) {
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    void setClickListener(LeaderBoardAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
