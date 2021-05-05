package com.bupt.sse.group7.covid19.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bupt.sse.group7.covid19.R;
import com.bupt.sse.group7.covid19.model.HealthInfo;

import java.util.List;


public class HealthInfoHistoryAdapter extends RecyclerView.Adapter<HealthInfoHistoryAdapter.ViewHolder> {

    private List<HealthInfo> healthInfoList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView typeText;
        TextView contentText;
        TextView submitTimeText;
        TextView auditStatusText;
        TextView auditOpinionText;
        TextView auditTimeText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            typeText = itemView.findViewById(R.id.type_text);
            submitTimeText = (TextView) itemView.findViewById(R.id.submitTime_text);
            auditStatusText = (TextView)itemView.findViewById(R.id.auditStatus_text);
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
        ViewHolder holder = new ViewHolder(view);
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
