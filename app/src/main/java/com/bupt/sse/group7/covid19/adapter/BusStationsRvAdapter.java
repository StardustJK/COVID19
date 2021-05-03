package com.bupt.sse.group7.covid19.adapter;

import android.view.View;

import androidx.annotation.NonNull;

import com.bupt.sse.group7.covid19.R;
import com.bupt.sse.group7.covid19.fragment.BusBaseFragment;

import java.util.List;

public class BusStationsRvAdapter extends BusResultRvAdapter {
    public BusStationsRvAdapter(List<String> nameList, BusBaseFragment context) {
        super(nameList, context);
    }

    @Override
    void setItemOnClick(@NonNull Holder holder) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastChosen != null) {
                    lastChosen.backgroundView.setBackgroundColor(v.getResources().getColor(R.color.colorPrimary));
                }
                chosenName = nameList.get(holder.getAdapterPosition());
                holder.backgroundView.setBackgroundColor(v.getResources().getColor(R.color.chosen));
                lastChosen = holder;
            }
        });
    }
}
