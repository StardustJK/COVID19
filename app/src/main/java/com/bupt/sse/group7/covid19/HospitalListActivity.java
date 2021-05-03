package com.bupt.sse.group7.covid19;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bupt.sse.group7.covid19.adapter.HospitalListAdapter;
import com.bupt.sse.group7.covid19.interfaces.IAreaSelectionCallBack;
import com.bupt.sse.group7.covid19.interfaces.IHospitalListCallBack;
import com.bupt.sse.group7.covid19.model.Area;
import com.bupt.sse.group7.covid19.model.HospitalBrief;
import com.bupt.sse.group7.covid19.presenter.AreaSelectionPresenter;
import com.bupt.sse.group7.covid19.presenter.HospitalListPresenter;
import com.bupt.sse.group7.covid19.presenter.HospitalPresenter;

import java.util.List;

/**
 * 医院列表页面
 */
public class HospitalListActivity extends AppCompatActivity implements IHospitalListCallBack, IAreaSelectionCallBack {
    private static final String TAG = "HospitalListActivity";
    private RecyclerView listView;
    private HospitalListAdapter adapter;
    private LinearLayoutManager layoutManager;

    private HospitalListPresenter listPresenter = HospitalListPresenter.getInstance();
    private AreaSelectionPresenter areaPresenter = AreaSelectionPresenter.getInstance();

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
        listPresenter.registerCallBack(this);
        areaPresenter.registerCallBack(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hospital_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        initView();
    }

    @Override
    public void onAreaSelected(Area area) {
        listPresenter.setAreaId(area.getAreaId());
        listPresenter.initData();
    }

    public void intoMainPage(int id) {
        HospitalPresenter.getInstance().setID(id);
        Intent intent = new Intent(this, HospitalMainPageActivity.class);
        startActivity(intent);
    }

    @Override
    public void onHospitalListChanged(List<HospitalBrief> hospitalList) {
        updateView(hospitalList);
    }

    @Override
    public void onGetDataFailed() {
        Toast.makeText(this, "当前网络不可用，请检查你的网络", Toast.LENGTH_SHORT).show();
    }

    private void updateView(List<HospitalBrief> hospitals) {
        adapter = new HospitalListAdapter(hospitals, this);

        listView.setLayoutManager(layoutManager);
        listView.setAdapter(adapter);
    }

    private void initView() {
        listView = findViewById(R.id.hosp_list);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    }
}