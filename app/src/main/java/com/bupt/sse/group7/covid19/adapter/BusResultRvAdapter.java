package com.bupt.sse.group7.covid19.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bupt.sse.group7.covid19.R;
import com.bupt.sse.group7.covid19.fragment.BusBaseFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BusResultRvAdapter extends RecyclerView.Adapter<BusResultRvAdapter.Holder> {
    // <name, uid>
    Map<String, String> map;
    List<String> nameList;
    private AdapterView.OnItemClickListener onItemClickListener;
    BusBaseFragment context;
    Holder lastChosen;
    String chosenName;

    public BusResultRvAdapter(Map<String, String> list, BusBaseFragment context) {
        this.map = list;
        this.context = context;
        this.nameList = new ArrayList<>();
        for (String name : map.keySet()) {
            nameList.add(name);
        }
    }

    public BusResultRvAdapter(List<String> nameList, BusBaseFragment context) {
        this.nameList = nameList;
        this.context = context;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_result_item, null, false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.nameView.setText(nameList.get(position));
        setItemOnClick(holder);
    }

    abstract void setItemOnClick(@NonNull Holder holder);

    @Override
    public int getItemCount() {
        if (map != null) {
            return map.size();
        }
        return nameList.size();
    }

    public String getChosenName() {
        return chosenName;
    }

    class Holder extends RecyclerView.ViewHolder {

        TextView nameView;
        View backgroundView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.bus_result_name);
            backgroundView = itemView.findViewById(R.id.bus_result_bg);
        }
    }
}
