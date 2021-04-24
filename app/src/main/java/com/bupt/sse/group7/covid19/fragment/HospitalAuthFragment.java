package com.bupt.sse.group7.covid19.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bupt.sse.group7.covid19.R;
import com.bupt.sse.group7.covid19.model.CurrentUser;
import com.bupt.sse.group7.covid19.utils.DBConnector;
import com.bupt.sse.group7.covid19.utils.JsonUtils;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 认证页面，医院认证
 */
public class HospitalAuthFragment extends Fragment {
    private static final String TAG = "HospitalAuthFragment";
    private EditText userView;
    private EditText passView;
    private JsonObject returnedInfo;
    private View view;
    private Context mContext=getActivity();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        initView();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void initView() {
        userView = view.findViewById(R.id.hospital_auth_username);
        passView = view.findViewById(R.id.hospital_auth_password);
        CardView submit = (CardView) view.findViewById(R.id.hospital_submit_auth);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitHospitalAuth();
            }
        });
    }

    public void submitHospitalAuth() {
        String username = userView.getText().toString();
        Map<String, String> args = new HashMap<>();
        args.put("username", username);

        getAuthInfo(args);
    }

    private void checkAuth(String password) {
        if (returnedInfo.get("status").getAsInt() == 1) {
            if (returnedInfo.get("password").getAsString().equals(password)) {
                Toast.makeText(getActivity(), "认证成功", Toast.LENGTH_SHORT).show();
                CurrentUser.setId(returnedInfo.get("h_id").getAsString());
                CurrentUser.setLabel("hospital");

                getActivity().finish();
            } else {
                Toast.makeText(getActivity(), "登录名或密码错误", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "用户不存在", Toast.LENGTH_SHORT).show();
        }
    }

    private void getAuthInfo(Map<String, String> args) {
        Call<ResponseBody> data = DBConnector.dao.executeGet("getHospitalAuthInfo.php", args);
        data.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    returnedInfo = JsonUtils.parseInfo(response.body().byteStream()).get(0).getAsJsonObject();
                    String password = passView.getText().toString();
                    checkAuth(password);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, "getAuthInfoOnFailure");
                Toast.makeText(getActivity(), "当前网络不可用，请检查你的网络", Toast.LENGTH_SHORT).show();

            }
        });

    }


}
