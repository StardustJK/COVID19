package com.bupt.sse.group7.covid19.adapter;

import android.view.View;

import androidx.annotation.NonNull;

import com.bupt.sse.group7.covid19.EditTrackActivity;
import com.bupt.sse.group7.covid19.R;
import com.bupt.sse.group7.covid19.fragment.BusBaseFragment;

import java.util.Map;

public class BusLineRvAdapter extends BusResultRvAdapter {
    private String uid;

    public BusLineRvAdapter(Map<String, String> list, BusBaseFragment context) {
        super(list, context);
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
                uid = map.get(chosenName);
                holder.backgroundView.setBackgroundColor(v.getResources().getColor(R.color.chosen));
                lastChosen = holder;

                ((EditTrackActivity) context.getActivity()).setBusFragment(context);
                ((EditTrackActivity) context.getActivity()).searchBusOrSubway(uid);
            }
        });
    }

    public String getUid() {
        return uid;
    }
}
