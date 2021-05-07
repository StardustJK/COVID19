package com.bupt.sse.group7.covid19.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import okhttp3.MediaType;
import okhttp3.RequestBody;
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

        Call<String> data = DBConnector.dao.Post("user/login", args);
        data.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                returnedInfo = (JsonObject) JsonParser.parseString(response.body());
                //登录成功
                if (returnedInfo.get("success").getAsBoolean()) {
                    JsonObject data = returnedInfo.get("data").getAsJsonObject();
                    CurrentUser currentUser = new CurrentUser(data.get("id").getAsInt(),
                            data.get("phone").getAsString(),
                            data.get("name").getAsString(),
                            data.get("status").getAsInt(),
                            data.get("role").getAsInt(),
                            data.get("auth").getAsBoolean(),
                            data.get("showTripRisk").getAsBoolean());
                    CurrentUser.setCurrentUser(currentUser);
                    SharedPreferences sharedPreferences=getContext().getSharedPreferences("Current_User", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("userId",data.get("id").getAsString());
                    editor.commit();
                    getActivity().finish();
                    Toast.makeText(getActivity(), returnedInfo.get("message").getAsString(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), returnedInfo.get("message").getAsString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }


}
