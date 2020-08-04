package com.example.map;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_CODE_PERMISSIONS = 1000;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private LatLng myLocation_find;
    private LatLng onLocation_lost;
    private Marker marker = null;

    private static final PatternItem DOT = new Dot();
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(DOT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void onLastLocationButtonClicked(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_PERMISSIONS);
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if(location!= null){
                    LatLng onLocation = new LatLng(location.getLatitude(),location.getLongitude());
                    onLocation_lost= onLocation;

                    if(marker == null){
                        marker= mMap.addMarker(new MarkerOptions().position(onLocation).title("조난자 위치"));
                    }
                    else{
                        marker.remove();
                        marker= mMap.addMarker(new MarkerOptions().position(onLocation).title("조난자 위치"));
                    }


                    mMap.moveCamera(CameraUpdateFactory.newLatLng(onLocation));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
                    String address = getCurrentAddress(location.getLatitude(), location.getLongitude());
                    Toast.makeText(getApplicationContext(),address+"현재위치 \n위도 " + location.getLatitude() + "\n경도 " + location.getLongitude(),Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_CODE_PERMISSIONS:
                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"권한 체크 거부 됨", Toast.LENGTH_SHORT).show();
                }
        }
    }

    public void myLocationButtonClicked(View view) {
        // Add a marker in Sydney and move the camera
        LatLng myLocation = new LatLng(36.624342, 127.465913);
        myLocation_find= myLocation;
        mMap.addMarker(new MarkerOptions().position(myLocation).title("구조자 위치")).remove();
        mMap.addMarker(new MarkerOptions().position(myLocation).title("구조자 위치"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
        String address = getCurrentAddress(36.624342, 127.465913);
        Toast.makeText(getApplicationContext(),address+" "+"현재위치 \n위도 " + 36.624342 + "\n경도 " + 127.465913,Toast.LENGTH_LONG).show();
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

    public void polylineButtonClicked(View view) {

        double distance = polyline_meter();
        String meter = String.format("%.2f", distance);

        Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(
                        new LatLng(myLocation_find.latitude, myLocation_find.longitude),
                        new LatLng(onLocation_lost.latitude, onLocation_lost.longitude)));

        polyline1.setTag(meter);

        // Flip from solid stroke to dotted stroke pattern.
        if ((polyline1.getPattern() == null) || (!polyline1.getPattern().contains(DOT))) {
            polyline1.setPattern(PATTERN_POLYLINE_DOTTED);
        } else {
            // The default pattern is a solid stroke.
            polyline1.setPattern(null);
        }

        Toast.makeText(this, "직선 거리는"+ polyline1.getTag().toString()+"미터 입니다.",
                Toast.LENGTH_SHORT).show();
    }

    public String getCurrentAddress( double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }



        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";

    }
}