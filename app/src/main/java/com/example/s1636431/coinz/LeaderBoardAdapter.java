package com.example.s1636431.coinz;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/*
    Adapter for the leadboard fragment. Responsible for displaying all the information on the page. Its given data
    which is sent from the LeaderBoardFragment in the form of a ArrayList of Hashmaps which holds the users email as the key
    and their amount for the searched criteria as the value.
 */
public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.ViewHolder> {

    private LeaderBoardAdapter.ItemClickListener mClickListener;
    private ArrayList<HashMap<String, String>> mData;
    private LayoutInflater layoutInflater;

    private final String TAG = "LeaderBoardAdapter";

    LeaderBoardAdapter(Context context, ArrayList<HashMap<String, String>> data) {
        this.mData = data;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public LeaderBoardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.leaderboard_row, parent, false);

        return new LeaderBoardAdapter.ViewHolder(view);
    }

    @Override
    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    // Function displays the data passed into the Adapter by the Fragment
    public void onBindViewHolder(LeaderBoardAdapter.ViewHolder holder, int position) {
        HashMap<String, String> user = mData.get(position);
        Log.d(TAG,user.toString());

        // Iterate over each of the users and display the info on the leaderboard
        for ( String key : user.keySet() ) {
            Log.d(TAG, Integer.toString(position+1));
            holder.rank.setText(Integer.toString(position+1));
            holder.user.setText(key);
            Log.d(TAG, user.get(key));
            String result = String.format("%.2f", Double.parseDouble(Objects.requireNonNull(user.get(key))));
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
