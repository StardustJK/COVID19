package com.bupt.sse.group7.covid19.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bupt.sse.group7.covid19.R;
import com.bupt.sse.group7.covid19.adapter.StatusLineAdapter;
import com.bupt.sse.group7.covid19.model.Status;

import java.util.List;

/**
 * 病人主页，状态轴部分
 */
public class StatusLineFragment extends Fragment {

    private List<Status> list;
    private RecyclerView statusView;
    private StatusLineAdapter adapter;
    private LinearLayoutManager layoutManager;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_status_line, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initView();
    }

    private void initView() {
        statusView = view.findViewById(R.id.status_line_view);
        layoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.HORIZONTAL, false);

        adapter = new StatusLineAdapter(this.list, this.getContext());

        statusView.setLayoutManager(layoutManager);
        statusView.setAdapter(adapter);
    }

    public void setList(List<Status> list) {
        this.list = list;
    }

}
