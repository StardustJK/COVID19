package com.bupt.sse.group7.covid19.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bupt.sse.group7.covid19.R;
import com.bupt.sse.group7.covid19.model.PatientTrip;

import java.util.List;

public class TripResultAdapter extends RecyclerView.Adapter<TripResultAdapter.MyViewHolder> {
    private List<PatientTrip> patientTripList;

    public TripResultAdapter(List<PatientTrip> patientTrips) {
        patientTripList = patientTrips;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip_result, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PatientTrip patientTrip = patientTripList.get(position);
        holder.date.setText(patientTrip.getDate());
        holder.typeNo.setText(patientTrip.getTypeNo());
        holder.subNo.setText(patientTrip.getNoSub());
        holder.startPos.setText(patientTrip.getStartPos());
        holder.endPos.setText(patientTrip.getEndPos());
        holder.who.setText(patientTrip.getWho());
        if (patientTrip.getMemo().equals("")) {
            holder.memo_layout.setVisibility(View.GONE);
        } else {
            holder.memo.setText(patientTrip.getMemo());
        }
    }

    @Override
    public int getItemCount() {
        return patientTripList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView date, typeNo, subNo, startPos, endPos, who, memo;
        LinearLayout memo_layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            typeNo = itemView.findViewById(R.id.type_no);
            subNo = itemView.findViewById(R.id.no_sub);
            startPos = itemView.findViewById(R.id.start_pos);
            endPos = itemView.findViewById(R.id.end_pos);
            who = itemView.findViewById(R.id.who);
            memo_layout=itemView.findViewById(R.id.memo_layout);
            memo = itemView.findViewById(R.id.memo);

        }
    }

}
