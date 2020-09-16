package com.example.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MapsActivity extends Fragment implements OnMapReadyCallback {

    private static final int REQUEST_CODE_PERMISSIONS = 1000;
    public  GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient; //위치값 획득
    private LatLng myLocation_find=null; //조난자 위치정보 저장 변수
    private LatLng onLocation_lost=null; //구조가 위치정보 저장 변수
    private Marker marker = null; //구조자 마커정보 저장 변수
    private Marker marker2 = null; //조난자 위치정보 저장변수
    private Polyline polyline1=null; //직선거리 정보 저장변수

    private MapView MapView_Map;

    //직선거리 디자인 부분
    private static final PatternItem DOT = new Dot();
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(DOT);

    //블루투스에 온 위도경도 데이터 저장 변수
    private double latitude;
    private double longtitude;
    //블루투스 연결 변수
    private BluetoothSPP bt;
    private  HomeActivity blue;
    private Context con;
    private View rootView;

    private Button onLast_button,myLocation_button,Poly_line;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_maps, container, false);
        blue = new HomeActivity();

        MapView_Map = rootView.findViewById(R.id.MapView_Map);
        MapView_Map.onCreate(savedInstanceState);
        MapView_Map.onResume();
        MapView_Map.getMapAsync(this);

        return rootView;
    }

    void init() {
        myLocation_button = rootView.findViewById(R.id.myLocation_button);
        onLast_button = rootView.findViewById(R.id.onLast_button);
        Poly_line = rootView.findViewById(R.id.Poly_line);

        con = ((HomeActivity) getActivity()).getApplicationContext();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(con);

        onLast_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(con, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(con, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(((HomeActivity) getActivity()), new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_PERMISSIONS);
                    return;
                }

                mFusedLocationClient.getLastLocation().addOnSuccessListener(((HomeActivity)getActivity()), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if(location!= null){

                            LatLng onLocation = new LatLng(location.getLatitude(),location.getLongitude());
                            onLocation_lost= onLocation;

                            //System.out.println("위도 경도 : " + onLocation.latitude + " " + onLocation.longitude);

                            if(marker == null){//지도에 마커가 없으면 표시
                                marker= mMap.addMarker(new MarkerOptions().position(onLocation).title("구조자"));
                            }
                            else{//지도에 마커가 있으면 지우고 다시 표시
                                marker.remove();
                                marker= mMap.addMarker(new MarkerOptions().position(onLocation).title("구조자"));
                            }

                            mMap.moveCamera(CameraUpdateFactory.newLatLng(onLocation));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
                            String address = ((HomeActivity) getActivity()).getCurrentAddress(location.getLatitude(), location.getLongitude());
                            Toast.makeText(((HomeActivity) getActivity()).getApplicationContext(),address+"현재위치 \n위도 " + location.getLatitude() + "\n경도 " + location.getLongitude(),Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
        });

        myLocation_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserData data = ((HomeActivity)getActivity()).userData;

                if(data.getLat() == -1e9 ||  data.getLog() == -1e9) return;

                LatLng myLocation = new LatLng(data.getLat(), data.getLog());
                myLocation_find= myLocation;
                if(marker2==null) {//지도에 마커가 없으면 표시
                    marker2=mMap.addMarker(new MarkerOptions().position(myLocation).title("조난자 위치"));
                }
                else{//지도에 마커가 있으면 지우고 다시 표시
                    marker2.remove();
                    marker2=mMap.addMarker(new MarkerOptions().position(myLocation).title("조난자 위치"));
                }

                mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
                String address = ((HomeActivity) getActivity()).getCurrentAddress(myLocation_find.latitude, myLocation_find.longitude);
                Toast.makeText(((HomeActivity) getActivity()).getApplicationContext(),address+" "+"현재위치 \n위도 " + myLocation_find.latitude + "\n경도 " + myLocation_find.longitude,Toast.LENGTH_LONG).show();
            }
        });

        Poly_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myLocation_find==null){
                    Toast.makeText(con, "위치 데이터가 없습니다.",Toast.LENGTH_SHORT).show();
                }
                else {
                    double distance = polyline_meter();
                    String meter = String.format("%.2f", distance);

                    if (polyline1 == null) {
                        polyline1 = mMap.addPolyline(new PolylineOptions()
                                .clickable(true)
                                .add(
                                        new LatLng(myLocation_find.latitude, myLocation_find.longitude),
                                        new LatLng(onLocation_lost.latitude, onLocation_lost.longitude)));
                    } else {
                        polyline1.remove();
                        polyline1 = mMap.addPolyline(new PolylineOptions()
                                .clickable(true)
                                .add(
                                        new LatLng(myLocation_find.latitude, myLocation_find.longitude),
                                        new LatLng(onLocation_lost.latitude, onLocation_lost.longitude)));
                    }
                    polyline1.setTag(meter);

                    // Flip from solid stroke to dotted stroke pattern.
                    if ((polyline1.getPattern() == null) || (!polyline1.getPattern().contains(DOT))) {
                        polyline1.setPattern(PATTERN_POLYLINE_DOTTED);
                    } else {
                        // The default pattern is a solid stroke.
                        polyline1.setPattern(null);
                    }

                    Toast.makeText(con, "직선 거리는" + polyline1.getTag().toString() + "미터 입니다.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //구글에서 제공하는 구글맵 정보
        mMap = googleMap;
        init();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_CODE_PERMISSIONS:
                if(ActivityCompat.checkSelfPermission(con, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(con, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(con,"권한 체크 거부 됨", Toast.LENGTH_SHORT).show();
                }
        }
    }

    public double polyline_meter(){

        double earthRadius = 3958.75;
        double dLat = Math.toRadians(myLocation_find.latitude-onLocation_lost.latitude);
        double dLng = Math.toRadians(myLocation_find.longitude-onLocation_lost.longitude);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(onLocation_lost.latitude)) * Math.cos(Math.toRadians(myLocation_find.latitude)) * Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c*1000.0;

        return distance;
    }

    @SuppressWarnings("unused")
    public String getCurrentAddress( double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(con, Locale.getDefault());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(con, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(con, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(con, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";
    }

    //조난자 버튼 실행

    //Button myLocation_button = (Button)findViewById(R.id.myLocation_button);

//    public void myLocationButtonClicked(View view) {
//        // Add a marker in Sydney and move the camera
//
//        LatLng myLocation = new LatLng(latitude, longtitude);
//        myLocation_find= myLocation;
//        if(marker2==null) {//지도에 마커가 없으면 표시
//            marker2=mMap.addMarker(new MarkerOptions().position(myLocation).title("조난자 위치"));
//        }
//        else{//지도에 마커가 있으면 지우고 다시 표시
//            marker2.remove();
//            marker2=mMap.addMarker(new MarkerOptions().position(myLocation).title("조난자 위치"));
//        }
//
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
//        String address = ((HomeActivity) getActivity()).getCurrentAddress(latitude, longtitude);
//        Toast.makeText(((HomeActivity) getActivity()).getApplicationContext(),address+" "+"현재위치 \n위도 " + latitude + "\n경도 " + longtitude,Toast.LENGTH_LONG).show();
//    }
    //직선 거리 정보 얻기

    //직선 생성
//    public void polylineButtonClicked(View view) {
//
//        if(myLocation_find==null){
//            Toast.makeText(con, "위치 데이터가 없습니다.",Toast.LENGTH_SHORT).show();
//        }
//        else {
//            double distance = polyline_meter();
//            String meter = String.format("%.2f", distance);
//
//            if (polyline1 == null) {
//                polyline1 = mMap.addPolyline(new PolylineOptions()
//                        .clickable(true)
//                        .add(
//                                new LatLng(myLocation_find.latitude, myLocation_find.longitude),
//                                new LatLng(onLocation_lost.latitude, onLocation_lost.longitude)));
//            } else {
//                polyline1.remove();
//                polyline1 = mMap.addPolyline(new PolylineOptions()
//                        .clickable(true)
//                        .add(
//                                new LatLng(myLocation_find.latitude, myLocation_find.longitude),
//                                new LatLng(onLocation_lost.latitude, onLocation_lost.longitude)));
//            }
//            polyline1.setTag(meter);
//
//            // Flip from solid stroke to dotted stroke pattern.
//            if ((polyline1.getPattern() == null) || (!polyline1.getPattern().contains(DOT))) {
//                polyline1.setPattern(PATTERN_POLYLINE_DOTTED);
//            } else {
//                // The default pattern is a solid stroke.
//                polyline1.setPattern(null);
//            }
//
//            Toast.makeText(con, "직선 거리는" + polyline1.getTag().toString() + "미터 입니다.",
//                    Toast.LENGTH_SHORT).show();
//        }
//    }
}