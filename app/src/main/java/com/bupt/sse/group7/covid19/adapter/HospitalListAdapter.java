package com.bupt.sse.group7.covid19.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bupt.sse.group7.covid19.HospitalListActivity;
import com.bupt.sse.group7.covid19.R;
import com.bupt.sse.group7.covid19.model.HospitalBrief;

import java.util.List;

/**
 * 医院列表的RecyclerView Adapter，给每个医院信息数据绑定对应的视图
 */
public class HospitalListAdapter extends RecyclerView.Adapter<HospitalListAdapter.Holder> {
    private List<HospitalBrief> list;
    private HospitalListActivity context;

    public HospitalListAdapter(List<HospitalBrief> list, HospitalListActivity context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, null, false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.nameView.setText(list.get(position).getName());
        String addrElement = list.get(position).getAddress();
        if (addrElement != null) {
            holder.addressView.setText(addrElement);
        } else {
            holder.addressView.setText("暂无地址");
        }
        holder.noView.setText((position + 1) + "");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.intoMainPage(Integer.parseInt(list.get(holder.getAdapterPosition()).getH_id()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        TextView nameView;
        TextView addressView;
        TextView noView;

        public Holder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.list_hosp_name);
            addressView = itemView.findViewById(R.id.list_hosp_desc);
            noView = itemView.findViewById(R.id.hosp_no);
        }
    }
}
