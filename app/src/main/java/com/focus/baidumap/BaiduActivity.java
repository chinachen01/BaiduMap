package com.focus.baidumap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

/**
 * Created by chenyong on 2015/10/16.
 */
public class BaiduActivity extends AppCompatActivity {
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    private MyLocationListener mMyLocationListener;
    private double lastX;
    private double lastY;
    private String lastCity;
    private String lastStress;
    private boolean isFirstIn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_baidu);
        initMap();
        initLocation();
    }

    private void initMap() {
        mMapView = (MapView) findViewById(R.id.id_mapview);
        mBaiduMap = mMapView.getMap();
        //设置为普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //设置地图的拖曳时间
        mBaiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
            }

            @Override
            public void onMarkerDragStart(Marker marker) {

            }
        });
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
//                Toast.makeText(getApplicationContext(),"维度是:" + latLng.latitude + " 精度是:" + latLng.longitude,Toast.LENGTH_LONG).show();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                Toast.makeText(getApplicationContext(),"地址名字:" + mapPoi.getName(),Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }
    /**
     * 标注覆盖物
     */
    private void markMap(double x, double y) {
        mBaiduMap.clear();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //定义Maker坐标点,x为维度,y位经度,注意百度地图拾取坐标系统是先经度再维度
        LatLng point = new LatLng(x, y);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.icon_track_mark);
        OverlayOptions options = new MarkerOptions()
                .position(point)  //设置marker的位置
                .icon(bitmap)  //设置marker图标
                .zIndex(7)  //设置marker所在层级
                .draggable(true);  //设置手势拖拽
        mBaiduMap.addOverlay(options);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(point);
        mBaiduMap.setMapStatus(msu);
    }

    /**
     * 地图定位
     */
    private void initLocation() {
        mLocationClient = new LocationClient(getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
        mLocationClient.start();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_location:
                centerToMyLocation();
                break;
            case R.id.action_normal:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                break;
            case R.id.action_satellite:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.action_traffic:
                if (mBaiduMap.isTrafficEnabled()) {
                    mBaiduMap.setTrafficEnabled(false);
                    item.setTitle("交通地图(off)");
                } else {
                    mBaiduMap.setTrafficEnabled(true);
                    item.setTitle("交通地图(on)");
                }
                break;
            case R.id.action_marker:
                markMap(30.581813,104.070713);
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 定位到我的位置
     */
    private void centerToMyLocation() {
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(new LatLng(lastX, lastY));
        mBaiduMap.animateMapStatus(msu);
    }

    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            lastX = bdLocation.getLatitude();
            lastY = bdLocation.getLongitude();
            lastCity = bdLocation.getCity();
            lastStress = bdLocation.getAddrStr();
            MyLocationData data = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude())
                    .build();
            mBaiduMap.setMyLocationData(data);
            if (isFirstIn) {
                centerToMyLocation();
                isFirstIn = false;
                Toast.makeText(BaiduActivity.this, "你现在的位置是:" + lastStress,Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mBaiduMap.setMyLocationEnabled(true);
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume(); //实现地图的生命周期管理
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause(); //实现地图的生命周期管理
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocationClient.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy(); //实现地图的生命周期管理
    }
    
}
