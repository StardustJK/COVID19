package com.bupt.sse.group7.covid19.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 认证页面，医院认证
 */
public class RegisterFragment extends Fragment {
    private static final String TAG = "HospitalAuthFragment";
    private EditText emailView;
    private EditText passView;
    private EditText codeView;
    private Button sendCode;
    private View view;

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
        emailView = view.findViewById(R.id.email);
        passView = view.findViewById(R.id.password);
        codeView = view.findViewById(R.id.captcha);
        CardView register = (CardView) view.findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
        sendCode = view.findViewById(R.id.send_code);
        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerifyCode();
            }
        });
    }

    public void sendVerifyCode() {
        if (emailView.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "邮箱不能为空", Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, String> args = new HashMap<>();
        args.put("phone", emailView.getText().toString());
        Call<String> data = DBConnector.dao.Get("user/checkRegister", args);
        data.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JsonObject returnedInfo = (JsonObject) JsonParser.parseString(response.body());
                if (returnedInfo.get("success").getAsBoolean()) {
                    Toast.makeText(getActivity(), "验证码已发送，有效期为10分钟", Toast.LENGTH_LONG).show();

                } else {
                    String msg = returnedInfo.get("message").getAsString();
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getActivity(), "网络错误，请稍后重试", Toast.LENGTH_LONG).show();

            }
        });
    }

    public void register() {
        String email = emailView.getText().toString();
        String password = passView.getText().toString();
        String code = codeView.getText().toString();
        if (email.equals("")) {
            Toast.makeText(getActivity(), "邮箱不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if (password.equals("")) {
            Toast.makeText(getActivity(), "密码不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if (code.equals("")) {
            Toast.makeText(getActivity(), "验证码不能为空", Toast.LENGTH_LONG).show();
            return;
        }

        JsonObject param = new JsonObject();
        param.add("phone", new JsonPrimitive(email));
        param.add("password", new JsonPrimitive(password));
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), String.valueOf(param));

        Call<String> call = DBConnector.dao.Post("user/register", body, code);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JsonObject returnedInfo = (JsonObject) JsonParser.parseString(response.body());
                if (returnedInfo.get("success").getAsBoolean()) {
                    Toast.makeText(getActivity(), "注册成功", Toast.LENGTH_LONG).show();
                    //TODO 自动登录
                    logIn(email,password);
                    getActivity().finish();
                } else {
                    String msg = returnedInfo.get("message").getAsString();
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getActivity(), "网络错误，请稍后重试", Toast.LENGTH_LONG).show();

            }
        });

    }

    private void logIn(String phone,String password){
        JsonObject user = new JsonObject();
        user.add("phone", new JsonPrimitive(phone));
        user.add("password", new JsonPrimitive(password));
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), String.valueOf(user));

        Call<String> data = DBConnector.dao.Post("user/login", body);
        data.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JsonObject info = (JsonObject) JsonParser.parseString(response.body());
                //登录成功
                if (info.get("success").getAsBoolean()) {
                    JsonObject data = info.get("data").getAsJsonObject();
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
                    Toast.makeText(getActivity(), info.get("message").getAsString(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), info.get("message").getAsString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }



}
