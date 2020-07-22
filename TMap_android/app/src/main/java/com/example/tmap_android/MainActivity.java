package com.example.tmap_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {

    private Context mContext = null;
    private  boolean m_bTrackingMode = true;

    private  TMapGpsManager tmapgps = null;
    private TMapView tmapView = null;
    private static  String mApiKey = "l7xxf24764cb422249989a969b9dc2b55533";
    private static  int mMarkerID;

    private ArrayList<TMapPoint> m_tmapPoint = new ArrayList<TMapPoint>();
    private ArrayList<String> mArrayMarkerID = new ArrayList<String>();
    private  ArrayList<MapPoint> m_mapPoint = new ArrayList<MapPoint>();

    @Override
    public void onLocationChange(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        TMapPoint point = tmapgps.getLocation();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext=this;

        //Tmap지도 표시
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayoutTmap);
        tmapView = new TMapView(this);
        linearLayout.addView(tmapView);
        tmapView.setSKTMapApiKey(mApiKey);
        tmapView.setIconVisibility(true);

        TMapGpsManager gps = new TMapGpsManager(this);
        gps.setMinTime(1000);
        gps.setMinDistance(5);
        gps.setProvider(gps.GPS_PROVIDER);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1); //위치권한 탐색 허용 관련 내용
            }
            return;
        }
        tmapView.setIconVisibility(true);

        gps.OpenGps();

        tmapView.setTrackingMode(true);
        tmapView.setSightVisible(true);


    }

    public void onLastLocationButtonClicked(View view) {
//        TMapGpsManager gps = new TMapGpsManager(this);
//        gps.setMinTime(1000);
//        gps.setMinDistance(5);
//        gps.setProvider(gps.GPS_PROVIDER);
//        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1); //위치권한 탐색 허용 관련 내용
//            }
//            return;
//        }
//        tmapView.setIconVisibility(true);
//
//        gps.OpenGps();
//
//        tmapView.setTrackingMode(true);
//        tmapView.setSightVisible(true);
    }
}