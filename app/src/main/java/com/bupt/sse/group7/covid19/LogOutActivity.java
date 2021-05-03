package com.bupt.sse.group7.covid19;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bupt.sse.group7.covid19.interfaces.IPatientViewCallBack;
import com.bupt.sse.group7.covid19.model.CurrentUser;
import com.bupt.sse.group7.covid19.model.Patient;
import com.bupt.sse.group7.covid19.presenter.PatientPresenter;

import java.text.MessageFormat;

public class LogOutActivity extends AppCompatActivity implements IPatientViewCallBack {
    private Button btn_logout;
    PatientPresenter patientPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_out);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        patientPresenter=PatientPresenter.getInstance();
        patientPresenter.registerCallBack(this);

    }

    @Override
    public void onPatientInfoReturned(Patient patient) {
        String auth;
        if (patient.isAuth()) {
            auth = "已认证";
        } else {
            auth = "未认证";
        }
        String desc = MessageFormat.format("{0}  ", auth);

        ((TextView) this.findViewById(R.id.patient_name)).setText(patient.getUsername());
        ((TextView) this.findViewById(R.id.patient_desc)).setText(desc);


        btn_logout=findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("Current_User", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userId", null);
                editor.commit();
                CurrentUser.setCurrentUser(null);
                finish();
                Toast.makeText(getApplicationContext(), "成功注销", Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    public void onGetDataFailed() {

    }
}