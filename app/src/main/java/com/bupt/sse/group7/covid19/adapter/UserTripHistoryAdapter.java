package com.bupt.sse.group7.covid19.adapter;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bupt.sse.group7.covid19.R;
import com.bupt.sse.group7.covid19.model.UserTrip;
import com.bupt.sse.group7.covid19.utils.Constants;

import java.util.List;

public class UserTripHistoryAdapter extends RecyclerView.Adapter<UserTripHistoryAdapter.MyViewHolder>{
    private List<UserTrip> userTripList;

    public UserTripHistoryAdapter(List<UserTrip> userTrips) {
        userTripList = userTrips;
    }

    private OnDeleteClickListener onDeleteClickListener;
    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener){
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip_result, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UserTrip userTrip = userTripList.get(position);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onDeleteClickListener!=null){
                    onDeleteClickListener.onDeleteClick(userTrip);
                }
            }
        });
        holder.date.setText(userTrip.getDate());
        holder.typeNo.setText(Constants.types[userTrip.getType()]+userTrip.getNo());
        //非必填
        if (!TextUtils.isEmpty(userTrip.getNoSub())) {
            holder.subNo.setText(userTrip.getNoSub());
        } else {
            holder.subNo.setVisibility(View.GONE);
        }

        String startPos = userTrip.getStartPos();
        if (!TextUtils.isEmpty(startPos)) {
            holder.startPos.setText(startPos);

        } else {
            holder.start_end.setVisibility(View.GONE);
        }
        String endPos = userTrip.getEndPos();
        if (!TextUtils.isEmpty(endPos)) {
            holder.endPos.setText(endPos);
        } else {
            holder.start_end.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(userTrip.getMemo())) {
            holder.memo.setText(userTrip.getMemo());

        } else {
            holder.memo_layout.setVisibility(View.GONE);
        }
        if(userTrip.getRisk()){
            holder.layout.setBackgroundResource(R.drawable.border_red);
        }
    }

    @Override
    public int getItemCount() {
        return userTripList.size();
    }



    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView date, typeNo, subNo, startPos, endPos, who, memo;
        RelativeLayout start_end;
        LinearLayout who_layout,memo_layout,layout;
        ImageView delete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            start_end = itemView.findViewById(R.id.start_end);
            date = itemView.findViewById(R.id.date);
            typeNo = itemView.findViewById(R.id.type_no);
            subNo = itemView.findViewById(R.id.no_sub);
            startPos = itemView.findViewById(R.id.start_pos);
            endPos = itemView.findViewById(R.id.end_pos);
            memo = itemView.findViewById(R.id.memo);
            memo_layout = itemView.findViewById(R.id.memo_layout);
            who_layout=itemView.findViewById(R.id.who_layout);
            who_layout.setVisibility(View.GONE);
            layout=itemView.findViewById(R.id.layout);
            delete=itemView.findViewById(R.id.delete);


        }
    }
    public interface OnDeleteClickListener{
        //参数（父组件，当前单击的View,单击的View的位置，数据）
        void onDeleteClick(UserTrip userTrip);
    }
}

