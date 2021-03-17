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

    private int[] colorArray = new int[]{Color.parseColor("#e2ebf4"),//0人
            Color.parseColor("#ffe7b2"),//1-9人78
            Color.parseColor("#ffcea0"),//10-99
            Color.parseColor("#ffa577"),//100-499
            Color.parseColor("#ff6341"),//500-999
            Color.parseColor("#ff2736"),//1000-9999
            Color.parseColor("#de1f05")//10000
    };
    /**
     * 绘制颜色
     * */
    private int drawColor;

    public void setProvince(String province){
        this.province=province;
    }
    public void setConfirm(int confirm)
    {
        this.confirm=confirm;
        this.drawColor = getColor(confirm);
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
            //绘制边界
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
    public int getColor(int number){
        if (number==0){
            return colorArray[0];
        }
        else if (number>=1&&number<10){
            return colorArray[1];
        }
        else if (number>=10&&number<100){
            return colorArray[2];
        }
        else if (number>=100&&number<500){
            return colorArray[3];
        }
        else if (number>=500&&number<1000){
            return colorArray[4];
        }
        else if(number>=1000&&number<10000){
            return colorArray[5];
        }
        else return colorArray[6];
    }
}
