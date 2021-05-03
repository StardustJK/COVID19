package com.bupt.sse.group7.covid19.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bupt.sse.group7.covid19.R;
import com.bupt.sse.group7.covid19.model.TrackPoint;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * 病人轨迹轴的 RecyclerView Adapter
 */
public class TrackLineAdapter extends RecyclerView.Adapter<TrackLineAdapter.TrackHolder> {

    private List<TrackPoint> list;

    public TrackLineAdapter(List<TrackPoint> list, Context context) {
        this.list = list;
    }

    @NonNull
    @Override
    public TrackHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_line_item, null, false);
        TrackHolder holder = new TrackHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TrackHolder holder, int position) {
        TrackPoint trackPoint=list.get(position);
        String[] dateTime = trackPoint.getDate_time().split(" ");
        Log.d("TrackLineAdapter","dateTime："+trackPoint.getDate_time());
        String location =trackPoint.getLocation();
        String descrip = "";
        if(trackPoint.getDescription()!=null){
            descrip=trackPoint.getDescription();
        }
        holder.dateView.setText(dateTime[0]);
        holder.timeView.setText(dateTime[1].substring(0, 5));
        holder.locationView.setText(location);
        holder.desView.setText(descrip);
        holder.numberView.setText((position + 1) + "");
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    class TrackHolder extends RecyclerView.ViewHolder {
        TextView dateView;
        TextView timeView;
        TextView locationView;
        TextView desView;
        TextView numberView;

        public TrackHolder(View itemView) {
            super(itemView);
            dateView = itemView.findViewById(R.id.track_date);
            timeView = itemView.findViewById(R.id.track_time);
            locationView = itemView.findViewById(R.id.track_location);
            desView = itemView.findViewById(R.id.track_des);
            numberView = itemView.findViewById(R.id.track_number);
        }
    }
}
