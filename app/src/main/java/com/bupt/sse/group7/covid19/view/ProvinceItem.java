package com.bupt.sse.group7.covid19.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;

public class ProvinceItem {
    private Path path;
    private String province;
    private int confirm;


    /**
     * 绘制颜色
     * */
    private int drawColor;
    public int getConfirm(){
        return confirm;
    }

    public void setDrawColor(int drawColor) {
        this.drawColor = drawColor;
    }

    public void setProvince(String province){
        this.province=province;
    }
    public void setConfirm(int confirm)
    {
        this.confirm=confirm;
    };

    public ProvinceItem(Path path) {
        this.path = path;
    }

    public void drawItem(Canvas canvas, Paint paint, boolean isSelect){
        if (isSelect){
            //绘制内部颜色
            paint.clearShadowLayer();
            paint.setStrokeWidth(1);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.parseColor("#ffe766"));
            canvas.drawPath(path,paint);
            //绘制边界/
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(0xFFD0E8F4);
            canvas.drawPath(path,paint);
        }else {
            //绘制边界
            paint.setStrokeWidth(2);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            paint.setShadowLayer(8,0,0,0xffffff);
            canvas.drawPath(path,paint);

            paint.clearShadowLayer();
            paint.setColor(drawColor);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(2);
            canvas.drawPath(path,paint);
        }
    }

    public boolean isTouch(float x,float y){//注意注意这块是来判断点击位置的 主要知识点Region
        RectF rectF = new RectF();
        path.computeBounds(rectF,true);
        Region region = new Region();
        region.setPath(path,new Region((int)rectF.left,(int)rectF.top,(int)rectF.right,(int) rectF.bottom));
        return  region.contains((int)x,(int)y);
    }


    public String getProvince() {
        return province;
    }
}
