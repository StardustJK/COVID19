package com.bupt.sse.group7.covid19.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bupt.sse.group7.covid19.R;
import com.bupt.sse.group7.covid19.model.TrackPoint;

import org.w3c.dom.Text;

import java.util.List;

public class ShowMapListAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    List<List<TrackPoint>> trackList;

    public ShowMapListAdapter(Context context, List<List<TrackPoint>> trackList) {
        this.trackList = trackList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return trackList.size();
    }

    @Override
    public Object getItem(int position) {
        return trackList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_showmap, null);
            holder.textView = convertView.findViewById(R.id.text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        List<TrackPoint> points = trackList.get(position);
        String text = "\n";
        for (int i = 0; i < points.size(); i++) {
            TrackPoint trackPoint=points.get(i);
            text+=trackPoint.getDate_time()+"\n"
                    +trackPoint.getLocation()+"\n";
        }
        holder.textView.setText(text);

        return convertView;
    }

    public class ViewHolder {
        public TextView textView;
    }
}
