package com.bupt.sse.group7.covid19;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.bupt.sse.group7.covid19.model.CurrentUser;
import com.bupt.sse.group7.covid19.utils.DBConnector;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 用户首次登录设置用户名页面
 */
public class SetUsernameActivity extends AppCompatActivity {
    private static final String TAG = "SetUsernameActivity";
    private CardView submit;
    private EditText usernameView;
    private int id;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_patient_name);
        mContext = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        Bundle bundle = this.getIntent().getExtras();
        this.id = bundle.getInt("id");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        initView();
    }

    private void initView() {
        submit = findViewById(R.id.submit_username);
        usernameView = findViewById(R.id.set_username);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitUsername();
            }
        });
    }

    private void submitUsername() {
        String username = usernameView.getText().toString();

        JsonObject param = new JsonObject();
        param.add("id", new JsonPrimitive(this.id));
        param.add("username", new JsonPrimitive(username));
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), String.valueOf(param));
        Call<String> call = DBConnector.dao.Post("setPatientUsername.php", body);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.i(TAG, "用户名更新成功");
                CurrentUser.setId(id+"");
                CurrentUser.setLabel("patient");
                finish();
                Toast.makeText(mContext, "设置成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i(TAG, "用户名更新失败");
                Toast.makeText(mContext, "当前网络不可用，请检查你的网络", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
