package com.bupt.sse.group7.covid19;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bupt.sse.group7.covid19.fragment.NotAvailable;
import com.bupt.sse.group7.covid19.fragment.PatientTrackBlockFragment;
import com.bupt.sse.group7.covid19.fragment.StatusLineFragment;
import com.bupt.sse.group7.covid19.interfaces.IPatientViewCallBack;
import com.bupt.sse.group7.covid19.model.CurrentUser;
import com.bupt.sse.group7.covid19.model.Patient;
import com.bupt.sse.group7.covid19.presenter.PatientPresenter;
import com.bupt.sse.group7.covid19.utils.Constants;

import java.text.MessageFormat;

public class PatientMainPageActivity extends AppCompatActivity implements IPatientViewCallBack {
    private static final String TAG = "PatientMainPageActivity";
    private PatientPresenter patientPresenter;
    private StatusLineFragment statusLineFragment;
    private PatientTrackBlockFragment patientTrackBlockFragment;
    private NotAvailable notAvailable;
    private String id;
    public TextView busTrackTv;
    public LinearLayout busTrackLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_main_page);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        patientPresenter = PatientPresenter.getInstance();
        patientPresenter.registerCallBack(this);
        busTrackLayout = findViewById(R.id.busTrackLayout);
        busTrackTv = findViewById(R.id.busTrackTv);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update_track:
                CurrentUser currentUser = CurrentUser.getCurrentUser();
                if (currentUser.isAuth() && currentUser.getUserId().equals(this.id)) {
                    Intent intent = new Intent(PatientMainPageActivity.this, EditTrackActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "请先登录或认证本用户账号", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_patient_main, menu);
        return true;
    }

    @Override
    public void onPatientInfoReturned(Patient patient) {
        this.id = patient.getId();
        FragmentManager fragmentManager = getSupportFragmentManager();
        statusLineFragment = new StatusLineFragment();
        statusLineFragment.setList(patient.getStatuses());
        FragmentTransaction tranStatus = fragmentManager.beginTransaction();
        tranStatus.add(R.id.patient_content, statusLineFragment);
        tranStatus.commitAllowingStateLoss();

        if (patient.getTrackPoints().size() > 0) {
            patientTrackBlockFragment = new PatientTrackBlockFragment();
            patientTrackBlockFragment.setId(patient.getId());
            FragmentTransaction trackTran = fragmentManager.beginTransaction();
            trackTran.add(R.id.patient_content, patientTrackBlockFragment);
            trackTran.commitAllowingStateLoss();
        } else {
            notAvailable = new NotAvailable();
            notAvailable.setTitle("ta的轨迹");
            FragmentTransaction notTran = fragmentManager.beginTransaction();
            notTran.add(R.id.patient_content, notAvailable);
            notTran.commitAllowingStateLoss();
        }

//        String desc = MessageFormat.format("{0}  |  {1}",
//                Constants.statuses.get(patient.getStatus()),
//                patient.getH_name());
        String auth;
        if (patient.isAuth()) {
            auth = "已认证";
        } else {
            auth = "未认证";
        }
        String desc = MessageFormat.format("{0}  ", auth);

        ((TextView) this.findViewById(R.id.patient_name)).setText(patient.getUsername());
        ((TextView) this.findViewById(R.id.patient_desc)).setText(desc);
    }

    @Override
    public void onGetDataFailed() {

        Toast.makeText(this, "当前网络不可用，请检查你的网络", Toast.LENGTH_SHORT).show();

    }


}
