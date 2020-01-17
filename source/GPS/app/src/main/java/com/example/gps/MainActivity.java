package com.example.gps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final String[] MULTI_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    public static final  String TAG="MainActivity";
    private MapView mMapView = null;
    private TextView tx = null;
    LocationManager locationManager;
    Location location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        tx = findViewById(R.id.textView);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // 运行时权限管理
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,MULTI_PERMISSIONS,100);
        }
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); // 通过GPS获取位置
        // 注册监听，当时间为间隔100毫秒或位移变化超过0米时更新
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, locationListener);
        BaiduMap mBaiduMap = mMapView.getMap();
        // 开启地图控件的定位功能
        mBaiduMap.setMyLocationEnabled(true);
        // 获取经纬度及海拔高度信息，存入百度地图指定的坐标信息类MyLocationData
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getAccuracy())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        // 在地图上显示定位图标
        mBaiduMap.setMyLocationData(locData);
        Log.v("pcw","setUserMapCenter : lat : "+ location.getLatitude()+" lon : " + location.getLongitude());
        LatLng cenpt = new LatLng(location.getLatitude(),location.getLongitude());
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .zoom(18)
                .target(cenpt)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
    }

    public void click(View view)
    {
        getLocation();
        tx.setText("当前经度："+location.getLongitude()+"\n"+"当前纬度："+location.getLatitude());
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    /**
     * 实现位置监听接口
     */
    private LocationListener locationListener = new LocationListener() {
        /**
         * 位置信息变化时触发:当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
         * @param location
         */
        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "时间：" + location.getTime());
            Log.i(TAG, "经度：" + location.getLongitude());
            Log.i(TAG, "纬度：" + location.getLatitude());
            Log.i(TAG, "海拔：" + location.getAltitude());
            tx.setText("当前经度："+location.getLongitude()+"\n"+"当前纬度："+location.getLatitude());
            // 地图控件管理对象
            BaiduMap mBaiduMap = mMapView.getMap();
            // 开启地图控件的定位功能
            mBaiduMap.setMyLocationEnabled(true);
            // 获取经纬度及海拔高度信息，存入百度地图指定的坐标信息类MyLocationData
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getAccuracy())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            // 在地图上显示定位图标
            mBaiduMap.setMyLocationData(locData);
            Log.v("pcw","setUserMapCenter : lat : "+ location.getLatitude()+" lon : " + location.getLongitude());
            LatLng cenpt = new LatLng(location.getLatitude(),location.getLongitude());
            //定义地图状态
            MapStatus mMapStatus = new MapStatus.Builder()
                    .zoom(18)
                    .target(cenpt)
                    .build();
            //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
            //改变地图状态
            mBaiduMap.setMapStatus(mMapStatusUpdate);
        }

        /**
         * GPS状态变化时触发:Provider被disable时触发此函数，比如GPS被关闭
         * @param provider
         * @param status
         * @param extras
         */
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                //GPS状态为可见时
                case LocationProvider.AVAILABLE:
                    Toast.makeText(MainActivity.this, "onStatusChanged：当前GPS状态为可见状态", Toast.LENGTH_SHORT).show();
                    break;
                //GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
                    Toast.makeText(MainActivity.this, "onStatusChanged:当前GPS状态为服务区外状态", Toast.LENGTH_SHORT).show();
                    break;
                //GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Toast.makeText(MainActivity.this, "onStatusChanged:当前GPS状态为暂停服务状态", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        /**
         * 方法描述：GPS开启时触发
         * @param provider
         */
        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(MainActivity.this, "onProviderEnabled:方法被触发", Toast.LENGTH_SHORT).show();
            getLocation();
        }

        /**
         * 方法描述： GPS禁用时触发
         * @param provider
         */
        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public void getLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // 运行时权限管理
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,MULTI_PERMISSIONS,100);
        }
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }


}