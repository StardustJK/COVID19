package com.bupt.sse.group7.covid19;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bupt.sse.group7.covid19.fragment.HospitalContactFragment;
import com.bupt.sse.group7.covid19.fragment.HospitalStatusFragment;
import com.bupt.sse.group7.covid19.fragment.HospitalSuppliesFragment;
import com.bupt.sse.group7.covid19.interfaces.IHospitalViewCallBack;
import com.bupt.sse.group7.covid19.model.CurrentUser;
import com.bupt.sse.group7.covid19.model.Hospital;
import com.bupt.sse.group7.covid19.presenter.HospitalPresenter;

import java.text.MessageFormat;

public class HospitalMainPageActivity extends AppCompatActivity implements IHospitalViewCallBack {

    private HospitalContactFragment contactFragment;
    private HospitalStatusFragment statusFragment;
    private HospitalSuppliesFragment suppliesFragment;

    private HospitalPresenter hospitalPresenter;

    private TextView nameTv;
    private TextView descTv;

    private Hospital hospital;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hospital_main_page);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        initView();

        hospitalPresenter = HospitalPresenter.getInstance();
        hospitalPresenter.registerCallBack(this);
        hospitalPresenter.getHospitalDetails();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hospital_main, menu);
        return true;
    }

    private void initView() {
        nameTv = findViewById(R.id.hospital_name);
        descTv = findViewById(R.id.hospital_desc);

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (this.statusFragment == null) {
            statusFragment = new HospitalStatusFragment();
            FragmentTransaction tranStatus = fragmentManager.beginTransaction();
            tranStatus.add(R.id.hosp_main_content, statusFragment);
            tranStatus.commitAllowingStateLoss();
        }

        if (this.contactFragment == null) {
            contactFragment = new HospitalContactFragment();
            FragmentTransaction tran = fragmentManager.beginTransaction();
            tran.add(R.id.hosp_main_content, contactFragment);
            tran.commitAllowingStateLoss();
        }

        if (this.suppliesFragment == null) {
            suppliesFragment = new HospitalSuppliesFragment();
            FragmentTransaction tranSupplies = fragmentManager.beginTransaction();
            tranSupplies.add(R.id.hosp_main_content, suppliesFragment);
            tranSupplies.commitAllowingStateLoss();
        }
    }

    @Override
    public void onHospitalInfoReturned(Hospital hospital) {
        this.hospital = hospital;
        nameTv.setText(hospital.getName());
        descTv.setText(MessageFormat.format("剩余床位  轻症 {0} | 重症 {1}",
                hospital.getMildLeft(), hospital.getSevereLeft()));

        contactFragment.updateView(hospital.getTel(), hospital.getAddress(), hospital.getPeople());

        statusFragment.updateView(hospital.getStatistics());
        suppliesFragment.updateView(hospital.getSupplies());
    }

    @Override
    public void onGetDataFailed() {
        Toast.makeText(this, "当前网络不可用，请检查你的网络", Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                if (CurrentUser.getLabel().equals("hospital") && CurrentUser.getId() == hospital.getId()+"") {
                    Intent intent = new Intent(this, EditHospitalActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "请先认证本医院账号", Toast.LENGTH_SHORT).show();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
