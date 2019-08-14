package com.example.kmp.Helper;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

class BasicViewHolder extends RecyclerView.ViewHolder {

    private TextView textView;

    public BasicViewHolder(View itemView) {
        super(itemView);
        textView = itemView.findViewById(android.R.id.text1);
    }

    public TextView getTextView(){
        return textView;
    }
}
