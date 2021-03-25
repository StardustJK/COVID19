package com.bupt.sse.group7.covid19;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bupt.sse.group7.covid19.view.ChinaMapView;
import com.bupt.sse.group7.covid19.view.StatisticGridView;
import com.bupt.sse.group7.covid19.interfaces.DAO;
import com.bupt.sse.group7.covid19.utils.JsonUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class StatisticActivity extends AppCompatActivity {

    private static String TAG = "StatisticActivity";

    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://view.inews.qq.com/g2/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build();

    private DAO dao = retrofit.create(DAO.class);

    private StatisticGridView domestic_grid;

    private String[] domestic_types = {"现有确诊", "境外输入", "无症状感染者", "累计确诊", "累计治愈", "累计死亡"};
    private int[] domestic_colors = {Color.parseColor("#E10000"),
            Color.parseColor("#4E8BE6"),
            Color.parseColor("#AE3AC6"),
            Color.parseColor("#BE2121"),
            Color.parseColor("#339966"),
            Color.parseColor("#666666")};

    private String[] city_types = {"现有确诊", "本土无症状", "累计确诊", "累计治愈"};
    private int[] city_colors = {Color.parseColor("#E10000"),
            Color.parseColor("#AE3AC6"),
            Color.parseColor("#BE2121"),
            Color.parseColor("#339966")};
    private StatisticGridView city_gird;
    private ChinaMapView chinaMapView;
    private TextView tv_location;
    private String currentProvince;
    private Map<String, Integer> cities_now_confirm;
    private Map<String, Integer> cities_confirm;

    private LineChartView lineChartView;
    private List<AxisValue> axisValues = new ArrayList<>();
    private List<PointValue> pointValues = new ArrayList<>();
    private int showXSize=20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        initView();
        Map<String, String> param = new HashMap<>();
        param.put("name", "disease_h5");
        Call<ResponseBody> data = dao.executeGet("getOnsInfo", param);
        data.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, "StatisticActivity onResponse");
                try {
                    String dataString = JsonUtils.inputStream2String(response.body().byteStream());
                    Log.d(TAG, "dataString :" + dataString);
                    JsonObject jsonObject = (JsonObject) JsonParser.parseString(dataString);
                    JsonObject data = (JsonObject) JsonParser.parseString(jsonObject.get("data").getAsString());
                    Log.d(TAG, "data:" + data.toString());
                    //国内总
                    JsonObject chinaTotal = data.getAsJsonObject("chinaTotal");
                    Log.i(TAG, "chinaTotal" + chinaTotal.toString());
                    int[] domesticNumber = new int[6];
                    domesticNumber[0] = chinaTotal.get("nowConfirm").getAsInt();
                    domesticNumber[1] = chinaTotal.get("importedCase").getAsInt();
                    domesticNumber[2] = chinaTotal.get("noInfect").getAsInt();
                    domesticNumber[3] = chinaTotal.get("confirm").getAsInt();
                    domesticNumber[4] = chinaTotal.get("heal").getAsInt();
                    domesticNumber[5] = chinaTotal.get("dead").getAsInt();

                    //国内新增
                    JsonObject chinaAdd = data.getAsJsonObject("chinaAdd");
                    String[] domesticAdd = new String[6];
                    domesticAdd[0] = chinaAdd.get("nowConfirm").getAsString();
                    domesticAdd[1] = chinaAdd.get("importedCase").getAsString();
                    domesticAdd[2] = chinaAdd.get("noInfect").getAsString();
                    domesticAdd[3] = chinaAdd.get("confirm").getAsString();
                    domesticAdd[4] = chinaAdd.get("heal").getAsString();
                    domesticAdd[5] = chinaAdd.get("dead").getAsString();
                    for (int i = 0; i < domesticAdd.length; i++) {
                        if (!domesticAdd[i].contains("-")) {
                            domesticAdd[i] = "+" + domesticAdd[i];
                        }
                    }

                    //本地疫情
                    JsonArray areaTree = data.getAsJsonArray("areaTree");
                    JsonObject area = (JsonObject) areaTree.get(0);
                    JsonArray children = area.getAsJsonArray("children");
                    cities_now_confirm = new HashMap<>();
                    cities_confirm = new HashMap<>();

                    JsonObject local = null;

                    for (int i = 0; i < children.size(); i++) {
                        JsonObject child = (JsonObject) children.get(i);
                        String province = child.get("name").getAsString();
                        if (currentProvince.contains(province)) {
                            local = child.getAsJsonObject("total");
                        }
                        JsonObject total = child.getAsJsonObject("total");
                        int nowConfirm = total.get("nowConfirm").getAsInt();
                        cities_now_confirm.put(province, nowConfirm);
                        int confirm = total.get("confirm").getAsInt();
                        cities_confirm.put(province, confirm);
                    }
                    chinaMapView.setData(cities_now_confirm);


                    initData(domesticNumber, domesticAdd, local);
                } catch (IOException e) {

                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, "StatisticActivity onFailed");
            }
        });

        initTrendData();


    }

    private void initTrendData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.inews.qq.com/newsqa/v1/query/inner/publish/modules/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        DAO dao = retrofit.create(DAO.class);
        Map<String, String> param = new HashMap<>();
        param.put("modules", "chinaDayList,chinaDayAddList");
        Call<ResponseBody> data = dao.executeGet("list", param);
        data.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String dataString = JsonUtils.inputStream2String(response.body().byteStream());
                    Log.d(TAG, "initTrendData onResponse: " + dataString);
                    JsonObject jsonObject = (JsonObject) JsonParser.parseString(dataString);
                    JsonObject data = jsonObject.get("data").getAsJsonObject();
                    JsonArray chinaDayAddList = data.getAsJsonArray("chinaDayAddList");
                    for (int i = 0; i < showXSize; i++) {

                        JsonObject dayAdd = chinaDayAddList.get(chinaDayAddList.size()-showXSize+i).getAsJsonObject();
                        axisValues.add(new AxisValue(i).setLabel(dayAdd.get("date").getAsString()));

                        pointValues.add(new PointValue(i, dayAdd.get("localConfirmadd").getAsInt()));
                    }
                    initLineChart();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, "trend data get failed");
            }
        });
    }

    private void initData(int domestic_numbers[], String domestic_add_numbers[], JsonObject local) {
        int province_numbers[] = {local.get("nowConfirm").getAsInt(), local.get("suspect").getAsInt(), local.get("confirm").getAsInt(), local.get("heal").getAsInt()};
        domestic_grid.setAdapter(new StatisticAdapter(domestic_add_numbers, domestic_numbers, domestic_colors, domestic_types, this));
        city_gird.setAdapter(new StatisticAdapter(null, province_numbers, city_colors, city_types, this));

    }


    public void initView() {
        domestic_grid = findViewById(R.id.domestic_grid);
        domestic_grid.setSelector(new ColorDrawable(Color.TRANSPARENT));
        city_gird = findViewById(R.id.city_grid);
        city_gird.setSelector(new ColorDrawable(Color.TRANSPARENT));
        tv_location = findViewById(R.id.tv_location);
        MyApplication application = (MyApplication) getApplication();
        currentProvince = application.getCurrentProvince();
        Log.d(TAG, "current province" + currentProvince);
        if (currentProvince == null) {
            currentProvince = "北京市";
        }
        tv_location.setText(currentProvince);
        chinaMapView = findViewById(R.id.china_map);
        lineChartView = findViewById(R.id.line_chart);

    }

    /**
     * 疫情趋势
     */
    public void initLineChart() {
        Line line = new Line(pointValues).setColor(Color.parseColor("#e57631"));
        List<Line> lines = new ArrayList<>();
        line.setShape(ValueShape.CIRCLE);//每个数据点的形状
        line.setCubic(false);
        line.setFilled(false);
        line.setHasLabels(true);
        line.setHasPoints(true);
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis();
        axisX.setHasTiltedLabels(true);
        axisX.setTextColor(getResources().getColor(R.color.textGrey));
        axisX.setTextSize(10);
        axisX.setValues(axisValues);
        axisX.setMaxLabelChars(5);
        axisX.setHasLines(true);
        data.setAxisXBottom(axisX);

        Axis axisY = new Axis();
        axisY.setTextSize(8);
        axisY.setInside(true);
        data.setAxisYLeft(axisY);

        lineChartView.setInteractive(true);
        lineChartView.setZoomType(ZoomType.HORIZONTAL);
        lineChartView.setMaxZoom(3);
        lineChartView.setLineChartData(data);

        Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
        viewport.left = 0;
        viewport.right = axisValues.size() - 1;
        lineChartView.setCurrentViewport(viewport);

    }


}

class StatisticAdapter extends BaseAdapter {

    private String[] add_number;//新增数字
    private int[] number;//数字
    private int[] colors;//背景颜色
    private String[] type;//数据类型
    private LayoutInflater inflater;

    public StatisticAdapter(String[] add_number, int[] number, int[] colors, String[] type, Context context) {
        this.add_number = add_number;
        this.number = number;
        this.colors = colors;
        this.type = type;
        this.inflater = LayoutInflater.from(context);
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
        v = inflater.inflate(R.layout.item_statistic, null);
        TextView tv_add = v.findViewById(R.id.add_number);
        if (add_number == null) {
            LinearLayout linearLayout = v.findViewById(R.id.add_layout);
            linearLayout.setVisibility(View.GONE);
        } else {
            tv_add.setText(add_number[position]);

        }
        tv_add.setTextColor(colors[position]);
        TextView tv_number = v.findViewById(R.id.number);
        tv_number.setText(number[position] + "");
        tv_number.setTextColor(colors[position]);
        TextView tv_type = v.findViewById(R.id.data_type);
        tv_type.setText(type[position]);

        return v;
    }
}


