package com.bupt.sse.group7.covid19.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bupt.sse.group7.covid19.R;

public  class TypeChooseAdapter extends RecyclerView.Adapter<TypeChooseAdapter.MyViewHolder> {

        String[] types;

        public TypeChooseAdapter(String [] types){
            this.types=types;
        }

        //TODO 改类别
        private OnItemClickListener onItemClickListener;

        public void setOnItemClickListener(OnItemClickListener li) {
            onItemClickListener = li;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip_type, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.tv.setText(types[position]);
            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(position,
                                types[position]);
                    }
                });
            }

        }


        @Override
        public int getItemCount() {
            return types.length;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView tv;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                tv = itemView.findViewById(R.id.text);
            }
        }

        public interface OnItemClickListener {
            void onItemClick(int position, String text);
        }

}
