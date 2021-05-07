package com.bupt.sse.group7.covid19.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bupt.sse.group7.covid19.R;
import com.bupt.sse.group7.covid19.model.HealthInfo;

import java.util.List;


public class HealthInfoHistoryAdapter extends RecyclerView.Adapter<HealthInfoHistoryAdapter.ViewHolder> {
    private static final String TAG = "HealthInfoAdapter";

    private List<HealthInfo> healthInfoList;


    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }


    static class ViewHolder extends RecyclerView.ViewHolder{
        View healthInfoView;

        TextView typeText;
        TextView submitTimeText;
        TextView auditStatusText;
        View showDetailsView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            healthInfoView = itemView;
            typeText = itemView.findViewById(R.id.type_text);
            submitTimeText = (TextView) itemView.findViewById(R.id.submitTime_text);
            auditStatusText = (TextView)itemView.findViewById(R.id.auditStatus_text);
            showDetailsView = (View) itemView.findViewById(R.id.showHealthInfoDetails);
        }
    }

    public HealthInfoHistoryAdapter(List<HealthInfo> healthInfoList){
        this.healthInfoList = healthInfoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.healthinfo_history_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.showDetailsView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                HealthInfo healthInfo = healthInfoList.get(position);
                Log.d(TAG, "onClick: 点击查看详情"+healthInfo.getSubmitTime());

                //通过接口名调用方法
                mOnItemClickListener.onItemClick(v, position);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HealthInfo healthInfo = healthInfoList.get(position);
        holder.typeText.setText(healthInfo.getType());
        holder.submitTimeText.setText(healthInfo.getSubmitTime());
        holder.auditStatusText.setText(healthInfo.getAuditStatus());
    }

    @Override
    public int getItemCount() {
        return healthInfoList.size();
    }




}
