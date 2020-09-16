package com.example.map;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;



public class HomeActivity extends AppCompatActivity {

    private MapsActivity mapsActityiv;
    private MyDataActivity myDataActivity;

    public BluetoothSPP bt;
    public UserData userData;

    private String dataStructure[] = {"@NAME:", "@GDR:","@BR:","@ADDR:", "@HP1:","@HP2:","@HP3:","@lat:","@lng:"};

    private int check = 1;
    private Button TextView_MyData,TextView_Map,TextView_Connect;

//    private double latitude;
//    private double longtitude;

    public LatLng myLocation_find=null; //조난자 위치정보 저장 변수
//    public LatLng onLocation_lost=null; //구조가 위치정보 저장 변수
//    private Marker marker = null; //구조자 마커정보 저장 변수
    private Marker marker2 = null; //조난자 위치정보 저장변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_home);
        init();
    }

    void init(){
        userData = new UserData();

        bt = new BluetoothSPP(this); //Initializing

        TextView_MyData = findViewById(R.id.TextView_MyData);
        TextView_Map = findViewById(R.id.TextView_Map);
        TextView_Connect = findViewById(R.id.TextView_Connect);

        myDataActivity = new MyDataActivity();
        mapsActityiv = new MapsActivity();

        //블루투스 부분

        if (!bt.isBluetoothAvailable()) { //블루투스 사용 불가
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
            public void onDataReceived(byte[] data, String message) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                splits(message);

                System.out.println(userData.getLat() + " " + userData.getLog());

                if(userData.getLat() == -1e9 || userData.getLog() == -1e9){
                    if(userData.getAddr() != null && !userData.getAddr().isEmpty())
                        myDataActivity.EditText_Address.setText(userData.getAddr());

                    if(userData.getName() != null && !userData.getName().isEmpty())
                        myDataActivity.EditText_Name.setText(userData.getName());

                    if(userData.getHp() != null && !userData.getHp().isEmpty())
                        myDataActivity.EditText_Phone.setText(userData.getHp());

                    if(userData.getH2() != null && !userData.getH2().isEmpty())
                        myDataActivity.EditText_Phone_Second.setText(userData.getH2());

                    if(userData.getHp3() != null && !userData.getHp3().isEmpty())
                        myDataActivity.EditText_Phone_Third.setText(userData.getHp3());

                    if(userData.getBr() != null && !userData.getBr().isEmpty()) {
                        String br[] = userData.getBr().split("-");
                        myDataActivity.EditText_Year.setText(br[0]);
                        myDataActivity.Spinner_Month.setSelection(Integer.parseInt(br[1]) - 1);
                        myDataActivity.Spinner_Day.setSelection(Integer.parseInt(br[2]) - 1);
                    }
                    return;
                }

                LatLng myLocation = new LatLng(userData.getLat(), userData.getLog());
                myLocation_find = myLocation;

                if (marker2 == null) {
                    marker2 = mapsActityiv.mMap.addMarker(new MarkerOptions().position(myLocation).title("조난자"));
                } else {
                    marker2.remove();
                    marker2 = mapsActityiv.mMap.addMarker(new MarkerOptions().position(myLocation).title("조난자"));
                }

                mapsActityiv.mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                mapsActityiv.mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
//                String address = getCurrentAddress(latitude, longtitude);
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() { //연결해제
                Toast.makeText(getApplicationContext()
                        , "Connection lost", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() { //연결실패
                Toast.makeText(getApplicationContext()
                        , "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });

        TextView_Connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bt.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });

        TextView_MyData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(check == 1 ) return;
                check = 1;
                getSupportFragmentManager().beginTransaction().replace(R.id.Home_Fragment_Layout, myDataActivity).commit();
            }
        });

        TextView_Map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(check == 2) return;
                check = 2;
                getSupportFragmentManager().beginTransaction().replace(R.id.Home_Fragment_Layout, mapsActityiv).commit();
            }
        });

     //   getSupportFragmentManager().beginTransaction().replace(R.id.Home_Fragment_Layout, myDataActivity).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.Home_Fragment_Layout, mapsActityiv).commit();
    }

    void splits(String message) { // 받은 데이터를 분리해서 뷰와 매칭시켜줍니다.
        System.out.println("message : " + message);
        for(int i=0;i<dataStructure.length;i++){
            int len = message.length();
            String ans = message.replace(dataStructure[i],"");
            if(len != ans.length()){
                if(i == 0) userData.setName(ans);
                else if(i == 1) userData.setSx(ans);
                else if(i == 2){
                    if(ans.isEmpty()) return;
                    userData.setBr(ans);
                }
                else if(i == 3) userData.setAddr(ans);
                else if(i == 4) userData.setHp(ans);
                else if(i == 5) userData.setH2(ans);
                else if(i == 6) userData.setHp3(ans);
                else if(i == 7) userData.setLat(Double.parseDouble(ans));
                else if(i == 8) userData.setLog(Double.parseDouble(ans));
                return;
            }
        }
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


    public void onDestroy() {
        super.onDestroy();
        bt.stopService(); //블루투스 중지
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) { //
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기 끼리
               // setup();
            }
        }
    }

//    public void setup() {
////        Button btnSend = findViewById(R.id.btnSend); //데이터 전송
////        btnSend.setOnClickListener(new View.OnClickListener() {
////            public void onClick(View v) {
////                bt.send("Text", true);
////            }
////        });
////    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
//                setup();
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
