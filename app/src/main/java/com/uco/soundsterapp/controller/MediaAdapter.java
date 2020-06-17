package com.uco.soundsterapp.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uco.soundsterapp.R;
import com.uco.soundsterapp.model.Reproduccion;

import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder> {
    private Context context;
    private List<Reproduccion> uploads;

    public MediaAdapter(Context context, List<Reproduccion> uploads) {
        this.context = context;
        this.uploads = uploads;
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.reproduccion_item, parent, false);
        return new MediaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        Reproduccion reproduccion = uploads.get(position);
        holder.textViewName.setText(reproduccion.getName());
    }

    @Override
    public int getItemCount() {
        return uploads.size();
    }

    public class MediaViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewName;

        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.txt_view_name);
        }
    }
}
