package com.bupt.sse.group7.covid19.fragment;

import android.app.AlertDialog;
import android.content.Intent;
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
import com.bupt.sse.group7.covid19.SetUsernameActivity;
import com.bupt.sse.group7.covid19.model.CurrentUser;
import com.bupt.sse.group7.covid19.utils.DBConnector;
import com.bupt.sse.group7.covid19.utils.JsonUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 认证页面，个人用户认证
 */
public class PatientAuthFragment extends Fragment {
    private static final String TAG = "PatientAuthFragment";
    private AlertDialog.Builder builder;
    private EditText patientTelView;
    private EditText patientPassWordView;
    private JsonObject returnedInfo;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.patient_auth, container, false);
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
        patientTelView = view.findViewById(R.id.patient_auth_tel);
        patientPassWordView = view.findViewById(R.id.patient_auth_pw);
        CardView submit = (CardView) view.findViewById(R.id.patient_auth_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAuth();
            }
        });
    }

    public void submitAuth() {
        String tel = patientTelView.getText().toString();
        String password = patientPassWordView.getText().toString();
        if (tel.equals("") || password.equals("")) {
            Toast.makeText(getActivity(), "用户名或密码不能为空", Toast.LENGTH_SHORT).show();

        } else {
            JsonObject user = new JsonObject();
            user.add("phone", new JsonPrimitive(tel));
            user.add("password", new JsonPrimitive(password));
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), String.valueOf(user));
            getAuthInfo(body);
        }


    }

    private void getAuthInfo(RequestBody args) {

        Call<String> data = DBConnector.dao.executePost("user/login", args);
        data.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                returnedInfo = (JsonObject) JsonParser.parseString(response.body());
                //登录成功
                if(returnedInfo.get("success").getAsBoolean()){
                 //TODO 修改当前用户
                 getActivity().finish();
                 Toast.makeText(getActivity(),returnedInfo.get("message").getAsString(),Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getActivity(),returnedInfo.get("message").getAsString(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
//        data.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                try {
//                    returnedInfo = JsonUtils.parseInfo(response.body().byteStream()).get(0).getAsJsonObject();
//                    String tel = patientPassWordView.getText().toString();
//                    checkAuth(tel);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.i(TAG, "getAuthInfoOnFailure");
//                Toast.makeText(getActivity(), "当前网络不可用，请检查你的网络", Toast.LENGTH_SHORT).show();
//
//            }
//        });
    }

    private void checkAuth(String tel) {
        int status = returnedInfo.get("status").getAsInt();
        if (status == 1) {
            if (returnedInfo.get("tel").getAsString().equals(tel)) {
                Toast.makeText(getActivity(), "认证成功", Toast.LENGTH_SHORT).show();
                CurrentUser.setId(returnedInfo.get("p_id").getAsInt());
                CurrentUser.setLabel("patient");

                getActivity().finish();
            } else {
                Toast.makeText(getActivity(), "病案号或手机错误", Toast.LENGTH_SHORT).show();
            }
        } else if (status == 0) {
            Toast.makeText(getActivity(), "用户不存在", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this.getActivity(), SetUsernameActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("id", returnedInfo.get("p_id").getAsInt());
            intent.putExtras(bundle);
            startActivity(intent);
            this.getActivity().finish();
        }
    }

}
