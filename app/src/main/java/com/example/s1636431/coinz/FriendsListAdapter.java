package com.example.s1636431.coinz;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import static android.support.constraint.Constraints.TAG;

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
    public void onBindViewHolder( ViewHolder holder, int position) {
        HashMap<String, Bitmap> user = mData.get(position);
        String frEmail = user.keySet().toString();
        if (user.get(frEmail) !=null) {
            Bitmap image = user.get(frEmail);
            holder.friendImage.setImageBitmap(image);
        } else {
            holder.friendImage.setImageDrawable(context.getDrawable(R.drawable.ic_user));

        }
        holder.email.setText(frEmail);
        Log.d(TAG, "Adding user: " + frEmail + " to page: ");


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView email;
        ImageView friendImage;


        ViewHolder(View userView) {
            super(userView);
            email = userView.findViewById(R.id.txEmailRow);
            friendImage = userView.findViewById(R.id.friendImage);
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
