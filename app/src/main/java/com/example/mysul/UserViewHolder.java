package com.example.mysul;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mysul.Interface.IRecyclerItemClickListener;

public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView userEmail;
    IRecyclerItemClickListener iRecyclerItemClickListener;

    public void setiRecyclerItemClickListener(IRecyclerItemClickListener iRecyclerItemClickListener) {
        this.iRecyclerItemClickListener = iRecyclerItemClickListener;
    }

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        userEmail = (TextView)itemView.findViewById(R.id.userEmail); // Set the owner email
        itemView.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        iRecyclerItemClickListener.onItemClickListener(view,getAdapterPosition());
    }
}
