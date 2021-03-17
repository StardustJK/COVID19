package com.bupt.sse.group7.covid19.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.graphics.PathParser;

import com.bupt.sse.group7.covid19.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.CheckedOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ChinaMapView extends View {

    private Context context;//上下文
    private List<ProvinceItem> itemList;//各省地图列表 各省地图颜色 与路径
    private Paint paint;    //初始化画笔
    private ProvinceItem select; //选中的省份
    private RectF totalRect;//中国地图的矩形范围
    private float scale = 1.0f;//中国地图的缩放比例

    private Map<String,Integer> data;
    public void setData(Map<String,Integer> data){
        this.data=data;
        init(context);

    }
    public ChinaMapView(Context context) {
        this(context,null);
    }

    public ChinaMapView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ChinaMapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
    }




    private void init(Context context) {
        this.context = context;
        paint = new Paint();
        paint.setAntiAlias(true);
        itemList = new ArrayList<>();
        loadThread.start();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获取当前控件的高度 让地图宽高适配当前控件
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (totalRect != null) {
            double mapWidth = totalRect.width();
            scale = (float) (width / mapWidth); //获取控件高度为了让地图能缩放到和控件宽高适配
        }
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }
    //加载中国地图的路径相对比较耗时，这里开启子线程来加载
    private Thread loadThread = new Thread() {
        @Override
        public void run() {
            final InputStream inputStream = context.getResources().openRawResource(R.raw.china_map);//读取地图svg
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); //获取DocumentBuilderFactory实例
            DocumentBuilder builder = null;
            try {
                builder = factory.newDocumentBuilder();
                Document doc = builder.parse(inputStream);//解析svg的输入流
                Element rootElement = doc.getDocumentElement();
                NodeList items = rootElement.getElementsByTagName("path");
                //获取地图的整个上下左右位置，
                float left = -1;
                float right = -1;
                float top = -1;
                float bottom = -1;
                List<ProvinceItem> list = new ArrayList<>();
                for (int i = 0; i < items.getLength(); i++) {
                    Element element = (Element) items.item(i);
                    String pathData = element.getAttribute("android:pathData");
                    String province=element.getAttribute("android:province");

                    @SuppressLint("RestrictedApi")
                    Path path = PathParser.createPathFromPathData(pathData);
                    ProvinceItem provinceItem = new ProvinceItem(path);//设置路径
                    provinceItem.setProvince(province);//设置省份名字
                    provinceItem.setConfirm(data.get(province));
                    //取每个省的上下左右 最后拿出最小或者最大的来充当 总地图的上下左右
                    RectF rect = new RectF();
                    path.computeBounds(rect, true);
                    left = left == -1 ? rect.left : Math.min(left, rect.left);
                    right = right == -1 ? rect.right : Math.max(right, rect.right);
                    top = top == -1 ? rect.top : Math.min(top, rect.top);
                    bottom = bottom == -1 ? rect.bottom : Math.max(bottom, rect.bottom);
                    list.add(provinceItem);
                }
                itemList = list;
                totalRect = new RectF(left, top, right, bottom);//设置地图的上下左右位置

                //加载完以后刷新界面
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        requestLayout();
                        invalidate();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        handleTouch(event.getX() / scale, event.getY() / scale);
        return super.onTouchEvent(event);
    }

    private void handleTouch(float x, float y) {
        if (itemList == null){
            return;
        }
        ProvinceItem selectItem = null;
        for (ProvinceItem provinceItem : itemList){
            if (provinceItem.isTouch(x,y)){
                selectItem = provinceItem;
            }
        }
        if (selectItem != null){
            select = selectItem;
            postInvalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (itemList != null){
            canvas.save();
            canvas.scale(scale,scale);//把画布缩放匹配到本控件的宽高
            for (ProvinceItem provinceItem : itemList){
                if (provinceItem != select){
                    provinceItem.drawItem(canvas,paint,false);
                }else {
                    provinceItem.drawItem(canvas,paint,true);
                    Log.d("ChinaMapView", "省份"+provinceItem.getProvince());
                    canvas.drawText(provinceItem.getProvince(),0,100,paint);
                }
            }
        }
    }
}




