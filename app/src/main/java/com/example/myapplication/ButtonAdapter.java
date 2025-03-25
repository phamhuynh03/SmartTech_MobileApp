package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ButtonAdapter extends RecyclerView.Adapter<ButtonAdapter.ButtonViewHolder> {

    private final List<String> buttonLabels;
    private final Context context;
    private final OnButtonClickListener listener;
    public interface OnButtonClickListener {
        void onButtonClick(int position);
    }

    public ButtonAdapter(Context context, List<String> buttonLabels, OnButtonClickListener listener) {
        this.context = context;
        this.buttonLabels = buttonLabels;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ButtonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_button, parent, false);
        return new ButtonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ButtonViewHolder holder, int position) {
        String label = buttonLabels.get(position);
        holder.button.setText(label);
        holder.button.setOnClickListener(v -> listener.onButtonClick(position));
    }

    @Override
    public int getItemCount() {
        return buttonLabels.size();
    }

    public static class ButtonViewHolder extends RecyclerView.ViewHolder {
        Button button;

        public ButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.buttonItem);
        }
    }
}
