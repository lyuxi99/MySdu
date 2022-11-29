package com.lv.sdumap.ui.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.location.Location;
import android.util.AttributeSet;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.lv.sdumap.R;

/**
 * 用于显示地图的 ImageView
 */
public class MyMapView extends SubsamplingScaleImageView {
    private final Paint paint = new Paint();
    private final PointF vPin = new PointF();
    float pinX = -10000, pinY = -10000, pinR = 0;
    float orientation = 0;
    // 一些坐标变换用的参数
    final double X0 = 120.69028,
            Y0 = 36.362018,
            kx = 5120.0 / 15251.0,
            ky = -7482.0 / 17482.0,
            x0 = 5330, y0 = 7407;
    final double scale = 3.8458333333333333333333333333333;
    private Bitmap pin;

    public MyMapView(Context context) {
        super(context, null);
    }

    public MyMapView(Context context, AttributeSet attr) {
        super(context, attr);

        float density = getResources().getDisplayMetrics().densityDpi;
        pin = BitmapFactory.decodeResource(this.getResources(), R.drawable.map_arrow);
        float w = (density / 420f) * pin.getWidth() / 2;
        float h = (density / 420f) * pin.getHeight() / 2;
        pin = Bitmap.createScaledBitmap(pin, (int) w, (int) h, true);
    }

    /**
     * 设置当前位置
     *
     * @param location 位置
     */
    public void setLocation(Location location) {
        double x = (location.getLongitude() - X0) * 1e6;
        double y = (location.getLatitude() - Y0) * 1e6;
        pinR = (float) (location.getAccuracy() * scale);
        pinX = (float) (x0 + x * 5120.0 / 15251.0);
        pinY = (float) (y0 - y * 7482.0 / 17482.0);
    }

    /**
     * 设置当前朝向
     *
     * @param orientation 朝向角度
     */
    public void setOrientation(float orientation) {
        this.orientation = orientation;
    }

    /**
     * 绘制
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Don't draw pin before image is ready so it doesn't move around during setup.
        if (!isReady() || pin == null) {
            return;
        }

        sourceToViewCoord(pinX, pinY, vPin);
        float actualR = pinR * getScale();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        //绘制精度范围
        paint.setColor(0x300099ff);
        canvas.drawCircle(vPin.x, vPin.y, actualR, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0x7f0099ff);
        canvas.drawCircle(vPin.x, vPin.y, actualR, paint);

        paint.reset();
        float vX = vPin.x - (pin.getWidth() / 2.0f);
        float vY = vPin.y - (pin.getHeight() / 2.0f);
        canvas.rotate(orientation, vPin.x, vPin.y);
        canvas.drawBitmap(pin, vX, vY, paint);

    }
}
