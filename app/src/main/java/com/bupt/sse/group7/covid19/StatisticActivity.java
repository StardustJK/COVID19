package com.bupt.sse.group7.covid19;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bupt.sse.group7.covid19.view.ChinaMapView;
import com.bupt.sse.group7.covid19.view.StatisticGridView;
import com.bupt.sse.group7.covid19.interfaces.DAO;
import com.bupt.sse.group7.covid19.utils.JsonUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private StatisticGridView domestic_grid;

    private String[] domestic_types ={"现有确诊","境外输入","无症状感染者","累计确诊","累计治愈","累计死亡"};
    private int[] domestic_colors ={Color.parseColor("#E10000"),
            Color.parseColor("#4E8BE6"),
            Color.parseColor("#AE3AC6"),
            Color.parseColor("#BE2121"),
            Color.parseColor("#339966"),
            Color.parseColor("#666666")};

    private String[] city_types ={"新增确诊","新增无症状","累计确诊","累计治愈"};
    private int[] city_colors ={Color.parseColor("#E10000"),
            Color.parseColor("#AE3AC6"),
            Color.parseColor("#BE2121"),
            Color.parseColor("#339966")};
    private StatisticGridView city_gird;
    private ChinaMapView chinaMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        initView();
        Map<String,String> param=new HashMap<>();
        param.put("name","disease_h5");
        Call<ResponseBody> data = dao.executeGet("getOnsInfo",param);
        data.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG,"StatisticActivity onResponse");
                try {
                    String dataString= JsonUtils.inputStream2String(response.body().byteStream());
                    Log.d(TAG, "dataString :"+dataString);

                    JsonObject jsonObject=(JsonObject) JsonParser.parseString(dataString);
                    JsonObject data=  (JsonObject) JsonParser.parseString(jsonObject.get("data").getAsString());
                    JsonObject chinaTotal=data.getAsJsonObject("chinaTotal");
                    Log.i(TAG,"chinaTotal"+chinaTotal.toString());
                    int[] domesticNumber=new int[6];
                    domesticNumber[0]=chinaTotal.get("nowConfirm").getAsInt();
                    domesticNumber[1]=chinaTotal.get("importedCase").getAsInt();
                    domesticNumber[2]=chinaTotal.get("noInfect").getAsInt();
                    domesticNumber[3]=chinaTotal.get("confirm").getAsInt();
                    domesticNumber[4]=chinaTotal.get("heal").getAsInt();
                    domesticNumber[5]=chinaTotal.get("dead").getAsInt();

                    JsonObject chinaAdd=data.getAsJsonObject("chinaAdd");
                    String[]domesticAdd=new String[6];
                    domesticAdd[0]=chinaAdd.get("nowConfirm").getAsString();
                    domesticAdd[1]=chinaAdd.get("importedCase").getAsString();
                    domesticAdd[2]=chinaAdd.get("noInfect").getAsString();
                    domesticAdd[3]=chinaAdd.get("confirm").getAsString();
                    domesticAdd[4]=chinaAdd.get("heal").getAsString();
                    domesticAdd[5]=chinaAdd.get("dead").getAsString();
                    for(int i=0;i<domesticAdd.length;i++){
                        if(!domesticAdd[i].contains("-")){
                            domesticAdd[i]="+"+domesticAdd[i];
                        }
                    }

                    initData(domesticNumber,domesticAdd);
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

    private void initData(int  domestic_numbers[],String domestic_add_numbers[] ) {
        String city_add_numbers[]={"+18","+1","-1","+0"};
        int city_numbers[]={1,2,3,4};
        domestic_grid.setAdapter(new StatisticAdapter(domestic_add_numbers,domestic_numbers, domestic_colors, domestic_types,this));
        city_gird.setAdapter(new StatisticAdapter(city_add_numbers,city_numbers,city_colors,city_types,this));

    }


    public void initView(){
        domestic_grid=findViewById(R.id.domestic_grid);
        domestic_grid.setSelector(new ColorDrawable(Color.TRANSPARENT));
        city_gird=findViewById(R.id.city_grid);
        city_gird.setSelector(new ColorDrawable(Color.TRANSPARENT));
        chinaMapView=findViewById(R.id.china_map);
        chinaMapView.setTest("test");
    };


}

class StatisticAdapter extends BaseAdapter{

    private String[] add_number;//新增数字
    private int[] number;//数字
    private int[] colors;//背景颜色
    private String[] type;//数据类型
    private LayoutInflater inflater;

    public StatisticAdapter(String[] add_number, int[] number, int[] colors, String[] type,Context context) {
        this.add_number = add_number;
        this.number = number;
        this.colors = colors;
        this.type = type;
        this.inflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return number.length;
    }

    @Override
    public Object getItem(int position) {
        return number[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        v=inflater.inflate(R.layout.item_statistic,null);
        TextView tv_add=v.findViewById(R.id.add_number);
        tv_add.setText(add_number[position]);
        tv_add.setTextColor(colors[position]);
        TextView tv_number=v.findViewById(R.id.number);
        tv_number.setText(number[position]+"");
        tv_number.setTextColor(colors[position]);
        TextView tv_type=v.findViewById(R.id.data_type);
        tv_type.setText(type[position]);
        return v;
    }
}