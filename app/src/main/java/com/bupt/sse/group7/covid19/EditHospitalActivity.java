package com.bupt.sse.group7.covid19;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bupt.sse.group7.covid19.interfaces.IHospitalViewCallBack;
import com.bupt.sse.group7.covid19.model.Hospital;
import com.bupt.sse.group7.covid19.model.Supplies;
import com.bupt.sse.group7.covid19.presenter.HospitalPresenter;

/**
 * 编辑医院信息页面
 */
public class EditHospitalActivity extends AppCompatActivity implements IHospitalViewCallBack {
    private int id;
    private HospitalPresenter hospitalPresenter = HospitalPresenter.getInstance();

    private TextView nameTv;
    private EditText mildTv, severeTv, inChargeTv, addrTv, n95Tv, surgeonTv, ventTv,
            clotheTv, glassesTv, telTv, alcoholTv, pantsTv;
    private Hospital mHospital;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_hospital);

        Toolbar toolbar = findViewById(R.id.toolbar_edit);
        setSupportActionBar(toolbar);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        initView();
        hospitalPresenter.registerCallBack(this);
        hospitalPresenter.getHospitalDetails();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hospital_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                submit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initView() {
        nameTv = findViewById(R.id.hospital_name_edit);
        mildTv = findViewById(R.id.set_mild);
        severeTv = findViewById(R.id.set_severe);
        inChargeTv = findViewById(R.id.set_inCharge);
        addrTv = findViewById(R.id.set_address);
        telTv = findViewById(R.id.set_tel);

        //初始化物资框为主页显示
        n95Tv = (EditText) findViewById(R.id.set_n95);
        surgeonTv = (EditText) findViewById(R.id.set_surgeon);
        ventTv = (EditText) findViewById(R.id.set_ventilator);
        clotheTv = (EditText) findViewById(R.id.set_clothe);
        glassesTv = (EditText) findViewById(R.id.set_glasses);
        alcoholTv = (EditText) findViewById(R.id.set_alcohol);
        pantsTv = (EditText) findViewById(R.id.set_pants);
    }

    public void submit() {
        Supplies supplies = new Supplies(n95Tv.getText().toString(),
                surgeonTv.getText().toString(),
                ventTv.getText().toString(),
                clotheTv.getText().toString(),
                glassesTv.getText().toString(),
                alcoholTv.getText().toString(),
                pantsTv.getText().toString());

        mHospital.setSupplies(supplies);
        mHospital.setPeople(inChargeTv.getText().toString());
        mHospital.setTel(telTv.getText().toString());
        mHospital.setAddress(addrTv.getText().toString());
        mHospital.setMildLeft(mildTv.getText().toString());
        mHospital.setSevereLeft(severeTv.getText().toString());

        hospitalPresenter.updateData(mHospital);

        Toast.makeText(this, "已提交", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onHospitalInfoReturned(Hospital hospital) {
        this.mHospital = hospital;
        id = hospital.getId();
        nameTv.setText(hospital.getName());
        mildTv.setText(hospital.getMildLeft());
        severeTv.setText(hospital.getSevereLeft());
        inChargeTv.setText(hospital.getPeople());
        telTv.setText(hospital.getTel());
        addrTv.setText(hospital.getAddress());
        n95Tv.setText(hospital.getSupplies().getN95());
        surgeonTv.setText(hospital.getSupplies().getSurgeon());
        ventTv.setText(hospital.getSupplies().getVentilator());
        clotheTv.setText(hospital.getSupplies().getClothe());
        alcoholTv.setText(hospital.getSupplies().getAlcohol());
        glassesTv.setText(hospital.getSupplies().getGlasses());
        pantsTv.setText(hospital.getSupplies().getPants());
    }

    @Override
    public void onGetDataFailed() {
        Toast.makeText(this, "当前网络不可用，请检查你的网络", Toast.LENGTH_SHORT).show();

    }
}
