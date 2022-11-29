package com.lv.sdumap.ui.map;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.lv.sdumap.R;
import com.lv.sdumap.utils.BaseFragment;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 地图
 */
public class MapFragment extends BaseFragment {
    TextView textView;
    MyMapView mapView;
    AMapLocation location;
    AMapLocationClient mLocationClient = null;
    boolean initiated = false; // 是否已经初始化
    CompassListener compassListener;
    private final Handler emptyHandler = new Handler();
    final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    // 用于更新时间
    Runnable runnableUpdateTextView = new Runnable() {
        @Override
        public void run() {
            mapView.setLocation(location);
            mapView.invalidate();
            String date = dateFormat.format(new Date(location.getTime()));
            textView.setText(date);
        }
    };

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        textView = root.findViewById(R.id.textView);
        mapView = root.findViewById(R.id.mapView);
        mapView.setImage(ImageSource.resource(R.drawable.map_image));
        final ProgressBar progressBar = root.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        emptyHandler.postDelayed(() -> { // 5秒后执行该方法
            // handler自带方法实现定时器
            try {
                progressBar.setVisibility(View.GONE); // 隐藏
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 5000);
        return root;
    }

    /**
     * 初始化位置服务和罗盘服务
     */
    void init() {
        initAmap();
        compassListener = new CompassListener();
        compassListener.start();
        openGPS();
        mLocationClient.startLocation();
        initiated = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (initiated) {
            openGPS();
            compassListener.start();
            mLocationClient.startLocation();
        } else {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            } else {
                init();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (initiated) {
            try {
                compassListener.stop();
                mLocationClient.stopLocation();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mLocationClient.onDestroy();
            compassListener.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        initiated = false;
    }

    /**
     * 初始化高德位置服务
     */
    void initAmap() {
        AMapLocationListener mAMapLocationListener = amapLocation -> {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    location = amapLocation;
                    emptyHandler.post(runnableUpdateTextView);
                }
            }
        };
        try {
            AMapLocationClient.updatePrivacyShow(this.getActivity(), true, true);
            AMapLocationClient.updatePrivacyAgree(this.getActivity(), true);
            mLocationClient = new AMapLocationClient(getContext());
            AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
            mLocationOption.setInterval(1000);
            mLocationClient.setLocationOption(mLocationOption);
            mLocationClient.setLocationListener(mAMapLocationListener);
        } catch (Exception e) {
            Toast.makeText(this.getActivity(), "位置服务错误！", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "成功获取定位权限。", Toast.LENGTH_LONG).show();
                init();
            } else {
                Toast.makeText(getActivity(), "未能获取定位权限。", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 引导用户打开 gps
     */
    private void openGPS() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getActivity(), "请打开GPS", Toast.LENGTH_SHORT);
            final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle("请打开GPS");
            dialog.setMessage("打开GPS才能在地图中显示你的位置哦！");
            dialog.setPositiveButton("设置", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, 0);
                }
            });
            dialog.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            dialog.show();
        }
    }

    /**
     * 罗盘传感器监听器
     */
    class CompassListener {
        SensorManager mSensorManager = null;
        Sensor accelerometer, magnetic;
        float[] accelerometerValues, magneticFieldValues;
        CompassSensorEventListener mSensorEventListener;
        long lastCompassTime = System.currentTimeMillis();

        CompassListener() {
            mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
            accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            magnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }

        void start() {
            mSensorEventListener = new CompassSensorEventListener();
            mSensorManager.registerListener(mSensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
            mSensorManager.registerListener(mSensorEventListener, magnetic, SensorManager.SENSOR_DELAY_UI);
        }

        void stop() {
            mSensorManager.unregisterListener(mSensorEventListener);
        }

        void calculateOrientation() {
            if (System.currentTimeMillis() - lastCompassTime < 50) return;
            if (accelerometerValues == null || magneticFieldValues == null) return;
            float[] values = new float[3];
            float[] R = new float[9];
            SensorManager.getRotationMatrix(R, null, accelerometerValues,
                    magneticFieldValues);
            SensorManager.getOrientation(R, values);
            values[0] = (float) Math.toDegrees(values[0]);
            mapView.setOrientation(values[0]);
            mapView.invalidate();
            lastCompassTime = System.currentTimeMillis();
        }


        class CompassSensorEventListener implements SensorEventListener {

            @Override
            public void
            onSensorChanged(SensorEvent event) {

                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    accelerometerValues = event.values;
                }
                if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                    magneticFieldValues = event.values;
                }
                calculateOrientation();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        }
    }
}