package com.bupt.sse.group7.covid19;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.bupt.sse.group7.covid19.interfaces.DAO;
import com.bupt.sse.group7.covid19.utils.JsonUtils;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class StatisticActivity extends AppCompatActivity {

    private static String TAG="StatisticActivity";

    private  Retrofit retrofit=new Retrofit.Builder()
            .baseUrl("https://view.inews.qq.com/g2/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build();

    private DAO dao=retrofit.create(DAO.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        Map<String,String> param=new HashMap<>();
        param.put("name","disease_h5");
        Call<ResponseBody> data = dao.executeGet("getOnsInfo",param);
        data.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG,"StatisticActivity onResponse");
                try {
                    String data= JsonUtils.inputStream2String(response.body().byteStream());
                    Log.d(TAG, "data :"+data);
                } catch (IOException e) {

                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG,"StatisticActivity onFailed");
            }
        });
    }
}